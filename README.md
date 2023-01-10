# Simpleflex
Simpleflex is a simple, lightweight Webserver written in java. It supports Websites scripted with Beanshell and integration of servlets in a very easy way.


```xml
        <dependency>
            <groupId>ch.software-atelier</groupId>
            <artifactId>simpleflex-base</artifactId>
            <version>2.2.1</version>
        </dependency>
```
## Setup as Standalone Server


## Setup as an Embedded Server
### Serve a single WebApp on localhost
```java
HashMap<String,Object> settings = new HashMap<>();
// ...
SimpleFlexBase.serveOnLocalhost("com.yourCompany.awesomewebapp.App",settings,8080)
```

Your WebApp will be served on localhost on port 8080. It will be accessible as default app and on path /app.
- `http://localhost:8080/`
- `http://localhost:8080/app`

### Complex Setup
```java
// The Global Configuration
GlobalConfig gc = new GlobalConfig();
gc.setPort(8080);

// A Security Manager if necessary. We do not need one.
SecurityManagerConfig smc = null;

// The Domain Configurations.
List<DomainConfig> dcs = new ArrayList();

// We only need one, listening on the domain localhost.
DomainConfig localhostDomain = new DomainConfig("localhost");

// As Default App, we use the internal Default App that serves Files.
WebAppConfig defaultApp = new WebAppConfig(
  "DefaultApp",
  "" /*we do not need a name for the default App*/);
// Now we setup the default app with the document root...
defaultApp.config.put("$DOCPATH","html_docs/");
// we set the default app
localhostDomain.setDefaultWebAppConfig(defaultApp);

// Now we set up new App on path /myawesomeapp:
WebAppConfig awesomeApp = new WebAppConfig(
  "com.yourCompany.awesomewebapp.App", // <- this class has to be in the classpath
  "myawesomeapp" /* < !! */);
// Put some configurations
awesomeApp.config.put("jdbc","jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
// and add the app
localhostDomain.appendWebApp(awesomeApp);


List<ch.hom3.simpleflex.conf.LogConfig> lcs = new ArrayList();
StartupOutputNotifiable son = new StartupOutputPrintln();
List<GlobalModuleConfig> moduleConfigs = new ArrayList();


// TODO


new SimpleFlexBase(gc, smc, dcs, lcs, son, moduleConfigs).start();
```



## Implement a WebApp
### The WebApp Interface
The WebApp Interface consists of four Methods:
- `public void start(String name, HashMap<String,Object> config, SimpleFlexAccesser sfa)`
- `public WebDoc process(Request request)`
- `public long maxPostingSize(String requestedPath)`
- `public void quit()`

If the server starts up, the WebApp is instantiated and the `start()` Method is called. Here you can setup your WebApp with the settings provided in `config`. `name` holds the name of the WebApp as it is shown in the URL. If your WebApp was installed as a default App, served on the root of the URL-Path, this String is empty. The `sfa`-Object provides access to some functionality of the server.

If the server is running, it will accept HTTP-Connections of the port you configured. If a client makes a request, the server will check to which WebApp he has to route the request. If this is determined and the request has a body, the WebApp's `maxPostingSize()` method is called. If the bodys data is larger than the value provided there, the request will be rejected. If the request is accepted, the `process()` Method will be called, where you do your Magic and return an object that implements the `WebDoc` Interface.

In case Simpleflex will be quitted, the `quit()` Method will be called where you can cleanup all the mess, flush Steams and close connections, if necessary.

### The Request object


### The WebDoc Interface
The WebDoc Interface consists of eight Methods:
- `public long size()`
- `public String mime()`
- `public String name()`
- `public String dataType()`
- `public byte[] byteData()`
- `public InputStream streamData()`
- `public void close()`
- `public boolean isCachable()`

`size()` returns the contents size in bytes. `mime()` returns its mime type. `mime()` has to return either `WebDoc.DATA_STREAM` or `WebDoc.DATA_BYTE`, depending on the type of data that is provided. `byteData()` has to return a byte array, holding the responses body. `streamData()` should provide an Inputstream to the responses body. `close()` will be called if the transition is done, to cleanup. And  `isCachable()` can return whatever you want. It is currently not in use.

The classpath `ch.hom3.simpleflex.docs` contains several implementations of this interface. From simple file serving, over zip file creation to JSON Serving...

### Custom Headers in the Response
If you want to serve custom HTTP headers in your responses, implement the Interface `OptionalHeaders` in your WebDocs. Its method `fields` has to return an array of `HeaderField`.
