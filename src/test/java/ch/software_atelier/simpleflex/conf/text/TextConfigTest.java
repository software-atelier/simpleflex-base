package ch.software_atelier.simpleflex.conf.text;

import ch.software_atelier.simpleflex.conf.DomainConfig;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class TextConfigTest {
    @TempDir File directory;

    @Test void elementKeepsDuplicateKeysAndFiltersByRegex() throws Exception {
        ConfigElement element = new ConfigElement("App");
        element.appendValue("key", "one"); element.appendValue("key", "two"); element.appendValue("$flag", "yes");
        assertArrayEquals(new String[]{"one", "two"}, element.getValuesByKey("key"));
        assertArrayEquals(new String[]{"$flag"}, element.getValuesAndKeysByRegexOfKey("^\\$.*")[0]);
        assertThrows(ConfigElementException.class, () -> element.appendValue(null, "x"));
    }

    @Test void configFileRoundTripsAndReportsMalformedContent() throws Exception {
        File file = new File(directory, "roundtrip.conf"); ConfigElement original = new ConfigElement("Global"); original.appendValue("PORT", "8081");
        ConfigFileIO writer = new ConfigFileIO(file, new Collector()); writer.apendConfigElement(original); writer.write();
        ConfigFileIO reader = new ConfigFileIO(file, new Collector()); reader.read();
        assertEquals("8081", reader.configElements()[0].getValuesByKey("PORT")[0]);
        Files.write(file.toPath(), "<Bad>\nA=B=C\nEMPTY=\n</Bad>\n<Open>\nX=Y\n".getBytes(StandardCharsets.UTF_8));
        Collector collector = new Collector(); reader = new ConfigFileIO(file, collector); reader.read();
        assertEquals(4, collector.messages.size()); assertTrue(collector.fatal.contains(Boolean.TRUE));
    }

    @Test void generatorLoadsGlobalFileInterfaceAndDomainFiles() throws Exception {
        File domain = new File(directory, "domain.conf");
        Files.write(domain.toPath(), ("<DefaultWebApp>\nCLASSPATH=example.Root\nNAME=root\n$theme=dark\n</DefaultWebApp>\n<WebApp>\nCLASSPATH=example.Api\nNAME=api\n</WebApp>\n").getBytes(StandardCharsets.UTF_8));
        File queue = new File(directory, "queue");
        File main = new File(directory, "main.conf");
        Files.write(main.toPath(), ("<Global>\nPORT=8181\nSSLPORT=9443\nUSESSL=yes\nSECURITYMANAGER=YES\nFILEINTERFACE=yes\n</Global>\n<FileInterface>\nFILE=" + queue.getPath() + "\nINTERVAL=42\n</FileInterface>\n<Domain>\nNAME=example.test\nCONFIG=" + domain.getPath() + "\n</Domain>\n").getBytes(StandardCharsets.UTF_8));
        SimpleFlexConfigGenerator generator = new SimpleFlexConfigGenerator(main);
        assertEquals(8181, generator.globalConfig().port()); assertEquals(9443, generator.globalConfig().sslPort()); assertTrue(generator.globalConfig().useSSL());
        assertTrue(generator.globalConfig().useSecurityManager()); assertTrue(generator.globalConfig().useFileInterface()); assertEquals(42, generator.globalConfig().fileInterfaceInterval());
        List<DomainConfig> domains = generator.domainConfigs(); assertEquals(1, domains.size()); assertEquals("example.Root", domains.get(0).defaultWebApp().classPath()); assertEquals("dark", domains.get(0).defaultWebApp().config().get("$theme")); assertEquals("api", domains.get(0).webAppConfigs()[0].name());
    }

    private static class Collector implements SyntaxErrorNotifiable {
        final List<String> messages = new ArrayList<String>(); final List<Boolean> fatal = new ArrayList<Boolean>();
        public void syntaxError(String message, boolean isFatal) { messages.add(message); fatal.add(isFatal); }
    }
}
