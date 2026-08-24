package ch.software_atelier.simpleflex.interfaces.file;

import java.io.File;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class FileInterfaceTest {
    @TempDir File directory;
    @Test void testInterfaceRequiresTrueProcFlag() { TestInterface plugin = new TestInterface(); HashMap<String,Object> config = new HashMap<String,Object>(); assertFalse(plugin.process(null, config)); config.put("PROC", "TrUe"); assertTrue(plugin.process(null, config)); config.put("PROC", "false"); assertFalse(plugin.process(null, config)); }
    @Test void writerRejectsExistingTargetAndWritesCommandsBeforeTimeout() throws Exception { File target = new File(directory, "interface"); assertTrue(target.createNewFile()); FileInterfaceWriter writer = new FileInterfaceWriter(target); assertFalse(writer.execute()); assertEquals(FileInterfaceWriter.ERRORCODE_FILE_ALREADY_EXISTS, writer.errorReason()); assertTrue(target.delete()); writer.addCommand("example.Plugin", new String[][]{{"PROC"},{"TRUE"}}); Thread remover = new Thread(() -> { while (!target.exists()) Thread.yield(); target.delete(); }); remover.start(); assertTrue(writer.execute()); remover.join(1000); assertEquals(FileInterfaceWriter.ERRORCODE_EVERYTHING_OK, writer.errorReason()); }
}
