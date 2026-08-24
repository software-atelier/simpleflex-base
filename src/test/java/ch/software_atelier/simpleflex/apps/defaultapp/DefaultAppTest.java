package ch.software_atelier.simpleflex.apps.defaultapp;

import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.ErrorDoc;
import ch.software_atelier.simpleflex.docs.impl.FileDoc;
import ch.software_atelier.simpleflex.docs.impl.FolderRedirectorDoc;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class DefaultAppTest {
    @TempDir File root;
    @Test void servesFilesIndexesAndRedirectsFolders() throws Exception {
        Files.write(new File(root, "hello.txt").toPath(), "hello".getBytes(StandardCharsets.UTF_8)); File folder = new File(root, "folder"); assertTrue(folder.mkdir()); Files.write(new File(folder, "index.html").toPath(), "index".getBytes(StandardCharsets.UTF_8));
        DefaultApp app = app();
        assertTrue(app.process(request("GET", "/hello.txt")) instanceof FileDoc);
        assertTrue(app.process(request("GET", "/folder")) instanceof FolderRedirectorDoc);
        WebDoc index = app.process(request("GET", "/folder/")); assertTrue(index instanceof FileDoc); assertEquals("index.html", index.name());
        assertTrue(app.allowUpload("hello.txt")); assertFalse(app.allowUpload("missing")); assertEquals(10240, app.maxPostingSize("anything"));
    }
    @Test void rejectsUnsupportedTraversalAndProtectedPaths() throws Exception {
        Files.write(new File(root, "PW").toPath(), new byte[]{1}); DefaultApp app = app();
        assertTrue(app.process(request("POST", "/x")) instanceof ErrorDoc);
        assertTrue(app.process(request("GET", "/PW")) instanceof ErrorDoc);
        assertTrue(app.process(request("GET", "/safe/../secret")) instanceof ErrorDoc);
        assertTrue(app.process(request("GET", "/missing")) instanceof FolderRedirectorDoc);
    }
    private DefaultApp app() { DefaultApp app = new DefaultApp(); HashMap<String,Object> config = new HashMap<String,Object>(); config.put("$DOCPATH", root.getPath() + "/"); app.start("", config, null); return app; }
    private Request request(String method, String path) { Request request = new Request(); request.setMethod(method); request.setRequestString(path); return request; }
}
