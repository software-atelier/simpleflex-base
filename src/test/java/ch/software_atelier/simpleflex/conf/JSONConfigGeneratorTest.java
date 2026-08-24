package ch.software_atelier.simpleflex.conf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class JSONConfigGeneratorTest {

    @TempDir
    File temporaryDirectory;

    @Test
    void mapsGlobalDomainAndWebAppConfiguration() throws IOException {
        File config = writeConfig("{\n"
                + "  \"port\": 9090,\n"
                + "  \"file_interface\": true,\n"
                + "  \"file_interface_path\": \"queue/input\",\n"
                + "  \"file_interface_interval\": 1500,\n"
                + "  \"domains\": [{\n"
                + "    \"name\": \"example.test\",\n"
                + "    \"default_app\": {\"classpath\": \"apps.Default\", \"name\": \"root\"},\n"
                + "    \"apps\": [{\"classpath\": \"apps.Api\", \"name\": \"api\", \"config\": {\"enabled\": true, \"limit\": 3}}]\n"
                + "  }]\n"
                + "}");

        JSONConfigGenerator generator = new JSONConfigGenerator(config);

        assertEquals(9090, generator.globalConfig().port());
        assertTrue(generator.globalConfig().useFileInterface());
        assertEquals("queue/input", generator.globalConfig().fileInterfaceFile().getPath());
        assertEquals(1500, generator.globalConfig().fileInterfaceInterval());
        assertEquals(1, generator.domainConfigs().size());
        DomainConfig domain = generator.domainConfigs().get(0);
        assertEquals("example.test", domain.name());
        assertEquals("apps.Default", domain.defaultWebApp().classPath());
        assertEquals("root", domain.defaultWebApp().name());
        assertEquals(1, domain.webAppConfigs().length);
        assertEquals("api", domain.webAppConfigs()[0].name());
        assertEquals(Boolean.TRUE, domain.webAppConfigs()[0].config().get("enabled"));
        assertEquals(3, domain.webAppConfigs()[0].config().get("limit"));
    }

    @Test
    void usesDefaultsWhenOptionalFieldsAndDomainsAreMissing() throws IOException {
        JSONConfigGenerator generator = new JSONConfigGenerator(writeConfig("{\"port\": 8088}"));

        assertEquals(8088, generator.globalConfig().port());
        assertFalse(generator.globalConfig().useFileInterface());
        assertTrue(generator.domainConfigs().isEmpty());
    }

    private File writeConfig(String content) throws IOException {
        File config = new File(temporaryDirectory, "simpleflex.json");
        try (FileWriter writer = new FileWriter(config)) {
            writer.write(content);
        }
        return config;
    }
}
