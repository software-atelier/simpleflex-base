# Simpleflex Base

Simpleflex Base is a small Java 8 HTTP server framework for applications that
want a direct, dependency-light route from a socket request to Java code. It
maps a host and the first URL path component to a `WebApp`, parses common
request bodies, and lets the application return a `WebDoc` response. The
project can run as a standalone server from a configuration file or be started
from another Java application.

Current version: **2.3.0**. The project is built with Maven and targets Java 8.

## Features

- HTTP server with one `WebApp` instance per configured route and host
- Optional HTTPS listener backed by a Java keystore
- URL query parameters, form posts, multipart uploads, JSON, JSON Patch,
  XML, and single-part request bodies
- Static-file default application, directory index handling, and safe
  rejection of `../` path traversal attempts
- Response abstractions for strings, bytes, files, streams, JSON, XML,
  templates, classpath resources, redirects, ZIP downloads, and errors
- JSON and legacy text configuration, plus a small programmatic bootstrap API
- A file-triggered extension interface and plugin JAR discovery from
  `sf.plugins/`
- Log4j 2 logging and permissive CORS response headers

## Requirements

- Java 8 or newer (the source and target bytecode level are Java 8)
- Maven 3.6.3 or newer to build this repository

## Quick start: embed a WebApp

Add Simpleflex Base to the application that contains your `WebApp`:

```xml
<dependency>
    <groupId>ch.software-atelier</groupId>
    <artifactId>simpleflex-base</artifactId>
    <version>2.3.0</version>
</dependency>
```

The following complete example starts an application on
`http://localhost:8080/`. `serveOnLocalhost` installs the supplied class as
the default application when `path` is empty.

```java
package example;

import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.SimpleFlexBase;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.StringDoc;
import java.util.HashMap;

public final class HelloApp implements WebApp {
    @Override
    public void start(String name, HashMap<String, Object> config,
                      SimpleFlexAccesser accesser) {
        // Called once after construction and before the first request.
    }

    @Override
    public WebDoc process(Request request) {
        if (Request.METHOD_GET.equals(request.getMethod())
                && "/".equals(request.getReqestString())) {
            return StringDoc.htmlContent("<h1>Hello, Simpleflex!</h1>");
        }
        return new ch.software_atelier.simpleflex.docs.impl.ErrorDoc(
                "Not found", 404, "Not Found");
    }

    @Override
    public long maxPostingSize(String requestedPath) {
        return NO_UPLOAD;
    }

    @Override
    public void quit() {
        // Release application resources here.
    }

    public static void main(String[] args) {
        SimpleFlexBase.serveOnLocalhost(
                HelloApp.class.getName(), new HashMap<String, Object>(), 8080);
    }
}
```

`SimpleFlexBase.serveOnLocalhost(classPath, config, port, path)` also accepts
a non-empty `path`; the application is then registered under `/app` (the
framework currently uses `"app"` as the configured application name for this
convenience method). For explicit host and route names, use the programmatic
configuration shown below.

## Implement a WebApp

`ch.software_atelier.simpleflex.apps.WebApp` is the application boundary:

```java
public interface WebApp {
    long UNLIMITED_UPLOAD = -1;
    long NO_UPLOAD = 0;

    WebDoc process(Request request);
    void start(String name, HashMap<String, Object> config,
               SimpleFlexAccesser sfa);
    long maxPostingSize(String requestedPath);
    void quit();
}
```

### Lifecycle and routing

For every configured `WebApp`, Simpleflex constructs the class with its
no-argument constructor and calls `start`. `name` is the route name for a
named app and is `""` for the default app. The default app receives requests
to `/` and to a first path component for which no named app exists. A named
app called `api` receives `/api`, `/api/users`, and so on.

Before reading a request body, the server calls `maxPostingSize` with the
requested path. Return `NO_UPLOAD`, `UNLIMITED_UPLOAD`, or a byte limit.
`quit` is called through `SimpleFlexAccesser.quitWebApps()` during a controlled
shutdown.

Here is a small routed application which handles a query parameter, a form
post, and an unknown endpoint:

```java
package example;

import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.RecievedText;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.ErrorDoc;
import ch.software_atelier.simpleflex.docs.impl.StringDoc;
import java.util.HashMap;

public final class ApiApp implements WebApp {
    @Override
    public void start(String name, HashMap<String, Object> config,
                      SimpleFlexAccesser accesser) {
    }

    @Override
    public WebDoc process(Request request) {
        String path = request.getReqestString();

        if (Request.METHOD_GET.equals(request.getMethod())
                && "/api/greeting".equals(path)) {
            String name = request.getArgument("name");
            return StringDoc.text("Hello " + (name == null ? "world" : name));
        }

        if (Request.METHOD_POST.equals(request.getMethod())
                && "/api/echo".equals(path) && request.isFormPostReq()) {
            RecievedText message = request.getRecievedText("message");
            String text = message == null ? "" : message.getURLDecodedText();
            return StringDoc.text(text);
        }

        return new ErrorDoc("Not found", 404, "Not Found");
    }

    @Override
    public long maxPostingSize(String requestedPath) {
        return 16 * 1024;
    }

    @Override
    public void quit() {
    }
}
```

The `Request` API exposes `getMethod()`, `getReqestString()` (the spelling is
part of the public API), `getArgument(String)`, `arguments()`,
`getHeaderValue(String)`, `getHeaders()`, `getHost()`, `getPort()`,
`getClient()`, and `isSecureConnection()`. It parses these body types:

| Content type | Request accessors |
| --- | --- |
| `application/x-www-form-urlencoded` | `isFormPostReq()`, `getRecievedText(String)`, `getRecievedData()` |
| `multipart/form-data` | `getRecievedData()`, `getRecievedText(String)`, `getRecievedFile(String)` |
| `application/json` | `isJSONReq()`, `getJSONReq()` |
| `application/json-patch+json` | `isJSONArrReq()`, `getJSONArrReq()` |
| `application/xml` | `isXMLReq()`, `getXMLReq()` |
| any other body | `isSinglePartReq()`, `getSinglePartFile()` |

`RecievedFile` provides `file()`, `fileName()`, `getData()`, and
`successfulReceived()`. Uploaded temporary files are cleaned up by the server
after dispatch; copy or move a file during `process` if it must be retained.

## Configuration

There are three ways to configure a server.

### JSON: `config.json`

Running the packaged server without arguments first looks for `config.json` in
the working directory. The repository includes an example that serves
`webroot/` on `localhost` as the default app and under `/app`.

```json
{
  "port": 8080,
  "file_interface": true,
  "file_interface_interval": 1000,
  "file_interface_path": "./fileInterface",
  "domains": [
    {
      "name": "localhost",
      "default_app": {
        "classpath": "ch.software_atelier.simpleflex.apps.defaultapp.DefaultApp",
        "config": { "$DOCPATH": "webroot/" }
      },
      "apps": [
        {
          "name": "api",
          "classpath": "example.ApiApp",
          "config": { "greeting": "Hello" }
        }
      ]
    }
  ]
}
```

`JSONConfigGenerator` reads `port`, the three `file_interface*` settings, and
`domains`. Every app needs `classpath`; named apps additionally need `name`.
Each `config` member is passed unchanged to `WebApp.start`. The checked-in
`local_libs`, `remote_libs`, and `modules` fields are not consumed by the
current JSON configuration reader.

### Legacy text configuration: `simpleflex.conf`

If `config.json` is absent, the standalone launcher looks for the lowercase
file name `simpleflex.conf`. The checked-in `Simpleflex.conf` illustrates the
format, but should be renamed to lowercase when used directly on a
case-sensitive filesystem. A main file points to a per-domain file:

```text
<Global>
PORT=8080
USESSL=yes
SSLPORT=8443
SSLKEYSTORE=/absolute/path/server.jks
SSLKEYSTOREPASSWORD=changeit
FILEINTERFACE=yes
</Global>

<FileInterface>
FILE=./interface
INTERVAL=1000
</FileInterface>

<Domain>
NAME=localhost
CONFIG=localhost.conf
</Domain>
```

`localhost.conf` then declares a default app and named applications. Keys
whose names begin with `$` become WebApp configuration entries.

```text
<DefaultWebApp>
CLASSPATH=ch.software_atelier.simpleflex.apps.defaultapp.DefaultApp
NAME=ignored-for-default-app
$DOCPATH=webroot/
</DefaultWebApp>

<WebApp>
CLASSPATH=example.ApiApp
NAME=api
$DATABASE_URL=jdbc:example
</WebApp>
```

The text reader also recognizes `SECURITYMANAGER`; `GlobalConfig` preserves
that value, although the current server startup does not install a Java
`SecurityManager` itself.

### Programmatic configuration

Use `GlobalConfig`, `DomainConfig`, and `WebAppConfig` when the embedding
application owns configuration. This gives route names without depending on
the convenience bootstrap:

```java
import ch.software_atelier.simpleflex.SimpleFlexBase;
import ch.software_atelier.simpleflex.conf.DomainConfig;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import ch.software_atelier.simpleflex.conf.WebAppConfig;
import java.util.ArrayList;
import java.util.List;

GlobalConfig global = new GlobalConfig();
global.setPort(8080);

WebAppConfig defaultApp = new WebAppConfig(example.HelloApp.class.getName(), "");
WebAppConfig api = new WebAppConfig(example.ApiApp.class.getName(), "api");

DomainConfig localhost = new DomainConfig("localhost");
localhost.setDefaultWebAppConfig(defaultApp);
localhost.appendWebApp(api);

List<DomainConfig> domains = new ArrayList<DomainConfig>();
domains.add(localhost);
new SimpleFlexBase(global, domains).start();
```

The HTTP `Host` header selects a `DomainConfig`. A domain named `DEFAULT` is
used as a fallback when no host-specific domain is registered.

## Static files with DefaultApp

`DefaultApp` is the built-in static-file app. Configure `$DOCPATH` to its
document root. It supports only `GET` requests and limits request bodies to
10 KiB. A file is streamed through `FileDoc`; a directory request ending in
`/` serves `index.html`; a directory request without the trailing slash
returns a `FolderRedirectorDoc` to the slash form. Missing files and missing
index files return `404 Not Found`, while unsupported methods return a default
`400 Bad Request` error document. Requests containing `/../` are rejected.

The included `webroot/index.html` is a ready-to-serve document root for the
example `config.json`.

## Responses and documents

`WebApp.process` returns a `WebDoc`. The server sends its status code,
headers, MIME type, length, and either byte or stream data, then closes the
document using try-with-resources. Use the ready-made implementations when
possible:

| Need | Class / factory |
| --- | --- |
| Plain text or HTML | `StringDoc.text(...)`, `StringDoc.html(...)`, `StringDoc.htmlContent(...)` |
| Bytes | `new ByteDoc(bytes, name, mime)` |
| A file | `new FileDoc(file)` or `new FileDoc(file, true)` to delete it on close |
| An input stream | `InputStreamDoc`, then `setSize`, `setMime`, and `setName` |
| JSON | `JSONDoc.json(JSONObject)` or `JSONDoc.json(JSONArray)` |
| XML | `XMLDoc.xml(XmlElement)` |
| Classpath resource | `new RessourceDoc(path)` |
| Redirect body | `new RedirectorDoc(url)` or `new FolderRedirectorDoc(requestPath)` |
| Generated archive | `new ZipDoc(files, fileName)` |
| Error response | `new ErrorDoc(message)` or `new ErrorDoc(message, code, statusText)` |

For example, an application can return structured JSON directly:

```java
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.JSONDoc;
import org.json.JSONObject;

JSONObject payload = new JSONObject();
payload.put("status", "ok");
WebDoc response = JSONDoc.json(payload);
```

`WebDoc` defaults to HTTP `200 OK`. Set another status with
`setHTTPCode(int, String)` and append response headers through `getHeaders()`:

```java
WebDoc response = StringDoc.text("created");
response.setHTTPCode(201, "Created");
response.getHeaders().add(new HeaderField("X-Request-Id:", "abc-123"));
return response;
```

`HeaderField` is written as `name + " " + value`; include the colon in a
custom header name, as in the example. Simpleflex adds `Connection: Close`,
server and date headers, and CORS headers allowing `POST, GET, DELETE, PUT,
PATCH, HEAD, OPTIONS`. `OPTIONS` is handled by the server before the WebApp.

## FileInterface and plugins

The optional file interface is a simple command hook. When enabled,
`FileInterfaceHandler` watches the configured file at its polling interval,
parses one or more `<Interface>` blocks, instantiates the declared
`FileInterface`, and deletes the command file after processing.

```java
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.interfaces.file.FileInterface;
import java.util.HashMap;

public final class ReloadCommand implements FileInterface {
    @Override
    public boolean process(SimpleFlexAccesser accesser,
                           HashMap<String, Object> config) {
        return accesser.ready();
    }
}
```

The command file uses the legacy element syntax:

```text
<Interface>
CLASSPATH=example.ReloadCommand
ACTION=reload
</Interface>
```

`FileInterfaceWriter` can create and wait for these files from Java.
`Quiter` is the built-in command implementation that calls
`quitWebApps()`, `stopListening()`, and `quitFileInterface()` through the
`SimpleFlexAccesser`. At startup, Simpleflex also attempts to load non-hidden
files from `sf.plugins/` into the system classpath; use this only with trusted
plugins.

## HTTPS / TLS

`GlobalConfig` supports an additional TLS listener. Set `useSSL`, its port,
keystore path, and password programmatically (or use the text configuration
keys in the example above):

```java
global.setUseSSL(true);
global.setSSLPort(8443);
global.setSSLKeyStore("/absolute/path/server.jks");
global.setSSLKeyStorePassword("changeit");
```

When enabled, `SimpleFlexBase.start()` starts both the ordinary HTTP listener
and an `SSLServerSocket` listener. The HTTPS handler sets the JVM properties
`javax.net.ssl.keyStore` and `javax.net.ssl.keyStorePassword` before creating
the server socket. The current `JSONConfigGenerator` does not parse TLS
settings, so use text or programmatic configuration for HTTPS.

## Logging

Simpleflex uses Log4j 2 (`log4j-core`). Request and response debug logging is
tagged with the markers `SF_CONNECTION`, `SF_REQUEST`, and `SF_RESPONSE`, with
the request host as a child marker. Request headers are added to the Log4j
thread context while a request is processed. Configure Log4j in the embedding
application as usual (for example with `log4j2.xml`) to control output and
levels.

## REST extensions

Simpleflex Base is the transport and `WebApp` layer for two companion
repositories:

- **simpleflex-rest** (`ch.software-atelier:simpleflex-rest:2.3.0`) builds
  REST APIs on top of Simpleflex with `RestApp`, resources, request/response
  helpers, and Swagger documentation classes.
- **simpleflex-rest-auth** (`ch.software-atelier:simpleflex-auth:2.4.3`)
  builds on `simpleflex-rest` and provides authentication-oriented REST
  resources, token handling, access control lists, and MongoDB data handling.

Add the appropriate companion dependency to an application rather than
re-implementing REST dispatch or authentication directly in a base `WebApp`.

## Testing

The test suite uses JUnit Jupiter (JUnit 5). Run all unit and integration
tests with:

```bash
mvn test
```

The tests cover value objects, request and route parsing, JSON and text
configuration, document implementations, the file interface, and a real
socket-level HTTP integration test. The integration test starts a temporary
loopback listener on an ephemeral port and verifies static files, redirects,
404 and malformed-request handling, form post parsing, CORS/server headers,
and connection closure.

## Build and deployment

Build the JAR and copy runtime dependencies under `target/lib`:

```bash
mvn package
```

To run the standalone launcher from a built distribution, ensure that
`config.json` or `simpleflex.conf` is in the current working directory; the
manifest main class is `ch.software_atelier.simpleflex.SimpleFlexBase`.

`deployToNexus.sh` is the release helper. It sets `GPG_TTY` and the required
`MAVEN_OPTS` for its environment, then runs `mvn clean install deploy`. It is
intended for maintainers with Sonatype and signing credentials; ordinary local
development should use `mvn test` or `mvn package`.

## License

Simpleflex Base is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).
