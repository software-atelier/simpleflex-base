package ch.software_atelier.simpleflex.integration;

import ch.software_atelier.simpleflex.ConnectionHandler;
import ch.software_atelier.simpleflex.Domain;
import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.WebAppHandler;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.apps.defaultapp.DefaultApp;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.StringDoc;
import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Exercises the real socket-to-RequestHandler path without reserving a fixed port. */
class HttpServerIntegrationTest {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    @TempDir
    File documentRoot;

    private EphemeralConnectionHandler server;
    private URI baseUri;

    @AfterEach
    void stopServer() throws Exception {
        if (server != null) {
            server.stopListening();
            server.join(3_000);
        }
    }

    @Test
    void servesStaticFileAndDefaultIndexWithStandardHeaders() throws Exception {
        Files.write(new File(documentRoot, "index.html").toPath(), "home page".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(documentRoot, "hello.txt").toPath(), "hello".getBytes(StandardCharsets.UTF_8));
        start(defaultApp());

        HttpResponse<String> index = send(HttpRequest.newBuilder(baseUri.resolve("/")).GET().build());
        HttpResponse<String> file = send(HttpRequest.newBuilder(baseUri.resolve("/hello.txt")).GET().build());

        assertEquals(200, index.statusCode());
        assertEquals("home page", index.body());
        assertEquals(200, file.statusCode());
        assertEquals("hello", file.body());
        assertEquals("close", file.headers().firstValue("Connection").orElseThrow().toLowerCase());
        assertEquals("*", file.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
        assertTrue(file.headers().firstValue("Server").orElseThrow().startsWith("SimpleFlex base"));
    }

    @Test
    void returnsRedirectDocumentAndErrorDocumentForMissingPaths() throws Exception {
        File folder = new File(documentRoot, "folder");
        assertTrue(folder.mkdir());
        Files.write(new File(folder, "index.html").toPath(), "folder index".getBytes(StandardCharsets.UTF_8));
        start(defaultApp());

        HttpResponse<String> redirect = send(HttpRequest.newBuilder(baseUri.resolve("/folder")).GET().build());
        HttpResponse<String> missing = send(HttpRequest.newBuilder(baseUri.resolve("/missing/")).GET().build());

        assertEquals(200, redirect.statusCode());
        assertEquals("<meta http-equiv=\"refresh\" content=\"0; URL=/folder/\">", redirect.body());
        assertEquals(400, missing.statusCode());
        assertTrue(missing.body().contains("404 - File not found"));
    }

    @Test
    void parsesPostBodyAndRequestHeadersBeforeDispatching() throws Exception {
        start(new EchoApp());

        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve("/echo"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-Trace", "trace-123")
                .POST(HttpRequest.BodyPublishers.ofString("message=hello+world"))
                .build();
        HttpResponse<String> response = send(request);

        assertEquals(200, response.statusCode());
        assertEquals("POST|/echo|hello world|trace-123", response.body());
    }

    @Test
    void closesEachConnectionAndAcceptsSubsequentRequests() throws Exception {
        start(new EchoApp());
        HttpClient client = newHttpClient();

        HttpResponse<String> first = send(client, HttpRequest.newBuilder(baseUri.resolve("/first")).GET().build());
        HttpResponse<String> second = send(client, HttpRequest.newBuilder(baseUri.resolve("/second")).GET().build());

        assertEquals("GET|/first||", first.body());
        assertEquals("GET|/second||", second.body());
        assertEquals("close", first.headers().firstValue("Connection").orElseThrow().toLowerCase());
        assertEquals("close", second.headers().firstValue("Connection").orElseThrow().toLowerCase());
    }

    private DefaultApp defaultApp() {
        DefaultApp app = new DefaultApp();
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("$DOCPATH", documentRoot.getAbsolutePath());
        app.start("", config, null);
        return app;
    }

    private void start(WebApp app) throws Exception {
        Map<String, WebApp> applications = new HashMap<String, WebApp>();
        Domain domain = new Domain("localhost", new HashMap<String, WebApp>(applications));
        domain.setDefaultWebApp(app);
        HashMap<String, Domain> domains = new HashMap<String, Domain>();
        domains.put("localhost", domain);
        server = new EphemeralConnectionHandler(new GlobalConfig(), new WebAppHandler(domains));
        server.start();
        baseUri = new URI("http://localhost:" + server.port().get(3, TimeUnit.SECONDS) + "/");
    }

    private HttpResponse<String> send(HttpRequest request) throws Exception {
        return send(newHttpClient(), request);
    }

    private HttpResponse<String> send(HttpClient client, HttpRequest request) throws Exception {
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpClient newHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
    }

    private static final class EphemeralConnectionHandler extends ConnectionHandler {
        private final CompletableFuture<Integer> port = new CompletableFuture<Integer>();

        private EphemeralConnectionHandler(GlobalConfig config, WebAppHandler webAppHandler) {
            super(config, webAppHandler);
        }

        @Override
        public ServerSocket getServerSocket(GlobalConfig ignored) {
            try {
                ServerSocket socket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
                socket.setSoTimeout(200);
                port.complete(socket.getLocalPort());
                return socket;
            } catch (Exception exception) {
                port.completeExceptionally(exception);
                return null;
            }
        }

        private CompletableFuture<Integer> port() {
            return port;
        }
    }

    public static final class EchoApp implements WebApp {
        @Override
        public WebDoc process(Request request) {
            String body = request.isFormPostReq() ? request.getRecievedText("message").getURLDecodedText() : "";
            String trace = request.getHeaderValue("X-Trace");
            return StringDoc.text(request.getMethod() + "|" + request.getReqestString() + "|" + body + "|" + (trace == null ? "" : trace));
        }

        @Override
        public void start(String name, HashMap<String, Object> config, SimpleFlexAccesser accesser) {
        }

        @Override
        public long maxPostingSize(String requestedPath) {
            return UNLIMITED_UPLOAD;
        }

        @Override
        public void quit() {
        }
    }
}
