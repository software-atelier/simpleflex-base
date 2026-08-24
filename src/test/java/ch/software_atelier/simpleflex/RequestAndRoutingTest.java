package ch.software_atelier.simpleflex;

import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.ByteDoc;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequestAndRoutingTest {
    @Test void requestParsesHeadersQueryAndStructuredBodies() throws Exception {
        Request request = new Request(); request.setRequestString("/api%20v1/items?a=one+two&flag"); request.setMethod("POST"); request.setProtocoll("HTTP/1.1");
        assertTrue(request.addHeaderLine("Host: example.test:8080")); assertFalse(request.addHeaderLine("broken")); request.setClient(InetAddress.getLoopbackAddress());
        assertEquals("/api v1/items", request.getReqestString()); assertEquals("one two", request.getArgument("a")); assertEquals("", request.getArgument("flag")); assertEquals("example.test", request.getHost()); assertEquals(8080, request.getPort()); assertEquals(InetAddress.getLoopbackAddress(), request.getClient());
        request.apendJSON(input("{\"answer\":42}"), "UTF-8", 13); assertTrue(request.isJSONReq()); assertEquals(42, request.getJSONReq().getInt("answer"));
        request.apendJSONArray(input("[1,2]"), "UTF-8", 5); assertTrue(request.isJSONArrReq()); assertEquals(2, request.getJSONArrReq().length());
        request.apendURLEncoded(input("x=hello+world&empty="), 20); assertTrue(request.isFormPostReq()); assertEquals("hello world", request.getRecievedText("x").getURLDecodedText());
    }

    @Test void requestDefaultsPortAndStoresSinglePart() throws Exception {
        Request request = new Request(); request.addHeaderLine("Host: secure.test"); request.setSecure(true); assertEquals(443, request.getPort());
        request.appendSinglePart(input("binary"), 6); assertTrue(request.isSinglePartReq()); assertArrayEquals("binary".getBytes("UTF-8"), Utils.readFile(request.getSinglePartFile())); request.cleanup();
    }

    @Test void domainAndHandlerChooseNamedDefaultAndFallbackDomains() {
        WebApp defaultApp = new StubApp("default"), api = new StubApp("api"), fallback = new StubApp("fallback");
        HashMap<String, WebApp> apps = new HashMap<String, WebApp>(); apps.put("api", api); Domain domain = new Domain("host", apps); domain.setDefaultWebApp(defaultApp);
        assertSame(api, domain.getWebApp("api")); assertSame(defaultApp, domain.getWebApp("missing")); assertSame(defaultApp, domain.getWebApp(null)); assertEquals(2, domain.apps().size());
        HashMap<String, Domain> domains = new HashMap<String, Domain>(); domains.put("host", domain); HashMap<String, WebApp> fallbackApps = new HashMap<String, WebApp>(); Domain defaultDomain = new Domain("DEFAULT", fallbackApps); defaultDomain.setDefaultWebApp(fallback); domains.put("DEFAULT", defaultDomain);
        WebAppHandler handler = new WebAppHandler(domains); Request request = new Request(); request.setRequestString("/api/x"); request.addHeaderLine("Host: host"); assertSame(api, handler.getWebApp(request)); request.addHeaderLine("Host: other"); assertSame(fallback, handler.getWebApp(request));
    }
    private BufferedInputStream input(String value) throws Exception { return new BufferedInputStream(new ByteArrayInputStream(value.getBytes("UTF-8"))); }
    private static class StubApp implements WebApp { final String name; StubApp(String name) { this.name=name; } public WebDoc process(Request r) { return new ByteDoc(new byte[0], name, "text/plain"); } public void start(String n, HashMap<String,Object> c, SimpleFlexAccesser s) {} public long maxPostingSize(String p) { return 0; } public void quit() {} }
}
