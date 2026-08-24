package ch.software_atelier.simpleflex.docs.impl;

import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class DocumentImplementationsTest {
    @TempDir File directory;
    @Test void byteStringJsonErrorAndRedirectDocsExposePayloadAndMetadata() throws Exception {
        ByteDoc bytes = new ByteDoc(new byte[]{1,2}, "a.bin", "application/x"); assertEquals(2, bytes.size()); assertEquals("a.bin", bytes.name()); assertArrayEquals(new byte[]{1,2}, bytes.byteData());
        WebDoc html = StringDoc.htmlContent("hello"); assertEquals("<html><body>hello</body></html>", new String(html.byteData(), "UTF-8")); assertEquals("text.html", html.name());
        JSONDoc json = JSONDoc.json(new JSONArray().put("x")); assertEquals("[\"x\"]", new String(json.byteData(), "UTF-8"));
        ErrorDoc error = new ErrorDoc("bad"); assertEquals(400, error.getHttpCode().code); assertTrue(new String(error.byteData(), "UTF-8").contains("bad")); assertEquals(500, ErrorDoc.err500_InternalServerError("x").getHttpCode().code);
        assertEquals("<meta http-equiv=\"refresh\" content=\"0; URL=/folder/\">", new String(new FolderRedirectorDoc("/folder").byteData(), "UTF-8"));
    }
    @Test void fileTemplateStreamAndZipDocsUseLocalFiles() throws Exception {
        File plain = new File(directory, "hello.txt"); Files.write(plain.toPath(), "hello".getBytes(StandardCharsets.UTF_8)); FileDoc file = new FileDoc(plain); assertEquals(5, file.size()); assertEquals("hello.txt", file.name()); assertEquals("hello", new String(readAll(file.streamData()), "UTF-8"));
        File templ = new File(directory, "template.html"); Files.write(templ.toPath(), "Hi <!--NAME-->".getBytes(StandardCharsets.UTF_8)); TemplateDoc template = new TemplateDoc(templ); template.add("NAME", "Ada"); template.replace(); assertEquals("Hi Ada", new String(template.byteData(), "UTF-8")); template.clear(); template.replace(); assertEquals("Hi <!--NAME-->", new String(template.byteData(), "UTF-8"));
        InputStreamDoc stream = new InputStreamDoc(new ByteArrayInputStream("data".getBytes("UTF-8"))); stream.setName("in"); stream.setMime("text/x"); stream.setSize(4); assertEquals("data", new String(readAll(stream.streamData()), "UTF-8")); assertEquals(4, stream.size());
        ZipDoc zip = new ZipDoc(new File[]{plain}, "bundle.zip"); assertEquals(WebDoc.DATA_STREAM, zip.dataType()); try (ZipInputStream in = new ZipInputStream(zip.streamData())) { assertEquals("hello.txt", in.getNextEntry().getName()); assertEquals("hello", new String(readAll(in), "UTF-8")); } zip.close();
    }
    @Test void fileDocRejectsMissingFileAndDeletesWhenRequested() throws Exception {
        assertThrows(FileDocException.class, () -> new FileDoc(new File(directory, "none"))); File temporary = new File(directory, "delete.txt"); Files.write(temporary.toPath(), new byte[]{1}); FileDoc doc = new FileDoc(temporary, true); doc.close(); assertFalse(temporary.exists());
    }
    private byte[] readAll(java.io.InputStream input) throws Exception { java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(); byte[] buffer = new byte[64]; for (int n; (n=input.read(buffer)) != -1;) out.write(buffer,0,n); return out.toByteArray(); }
}
