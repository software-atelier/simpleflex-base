package ch.software_atelier.simpleflex;

import ch.software_atelier.simpleflex.conf.DomainConfig;
import ch.software_atelier.simpleflex.conf.FileInterfaceConfig;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import ch.software_atelier.simpleflex.conf.WebAppConfig;
import ch.software_atelier.simpleflex.docs.HeaderField;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {
    @Test void configurationObjectsRetainValuesAndDefaults() {
        GlobalConfig global = new GlobalConfig(); assertEquals(80, global.port()); assertEquals(443, global.sslPort()); assertFalse(global.useSSL());
        File interfaceFile = new File("queue"); global.setPort(1234); global.setSSLPort(4321); global.setUseSSL(true); global.setUseSecurityManager(true); global.setUseFileInterface(true); global.setFileInterfaceInterval(9); global.setFileInterfaceFile(interfaceFile);
        assertEquals(1234, global.port()); assertEquals(4321, global.sslPort()); assertTrue(global.useSSL()); assertTrue(global.useSecurityManager()); assertTrue(global.useFileInterface()); assertEquals(9, global.fileInterfaceInterval()); assertEquals(interfaceFile, global.fileInterfaceFile());
        FileInterfaceConfig fileConfig = new FileInterfaceConfig(); fileConfig.setInterval(3); fileConfig.setInterfaceFile("in"); assertEquals(3, fileConfig.interval()); assertEquals("in", fileConfig.interfaceFile());
        WebAppConfig app = new WebAppConfig("example.App", "api"); app.config().put("key", "value"); DomainConfig domain = new DomainConfig("example"); domain.setDefaultWebAppConfig(app); domain.appendWebApp(app); assertEquals("example", domain.name()); assertSame(app, domain.defaultWebApp()); assertSame(app, domain.webAppConfigs()[0]);
    }
    @Test void receivedDataAndHeaderObjectsExposePayload() {
        RecievedText text = new RecievedText(); text.setFieldName("message"); text.appendText("one+"); text.appendText("two"); assertEquals(RecievedData.TYPE_TEXT, text.type()); assertEquals("message", text.fieldName()); assertEquals("one two", text.getURLDecodedText());
        RecievedFile file = new RecievedFile(); file.setFileName("upload.bin"); file.appendToFile("data".getBytes(StandardCharsets.UTF_8)); file.done(); assertEquals(RecievedData.TYPE_FILE, file.type()); assertEquals("upload.bin", file.fileName()); assertArrayEquals("data".getBytes(StandardCharsets.UTF_8), file.getData()); file.deleteTmpFile(); assertFalse(file.file().exists());
        HeaderField header = new HeaderField("X-Test"); assertEquals("null", header.value()); header.setValue("ok"); assertEquals("X-Test", header.name()); assertEquals("ok", header.value()); assertEquals("OK", HTTPCodes.getMsg(200));
    }
}
