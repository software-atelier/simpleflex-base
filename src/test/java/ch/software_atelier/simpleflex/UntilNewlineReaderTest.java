package ch.software_atelier.simpleflex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class UntilNewlineReaderTest {

    @Test
    void returnsSuccessiveLinesAndRetainsBytesAfterNewline() throws IOException {
        UntilNewlineReader reader = new UntilNewlineReader(
                new ByteArrayInputStream("first\nsecond\nlast".getBytes("UTF-8")), 32);

        assertArrayEquals("first\n".getBytes("UTF-8"), reader.read());
        assertArrayEquals("second\n".getBytes("UTF-8"), reader.read());
        assertArrayEquals("last".getBytes("UTF-8"), reader.read());
    }

    @Test
    void combinesInputReadAcrossSmallBuffers() throws IOException {
        UntilNewlineReader reader = new UntilNewlineReader(
                new ByteArrayInputStream("abcdef\n".getBytes("UTF-8")), 2);

        assertArrayEquals("abcdef\n".getBytes("UTF-8"), reader.read());
    }
}
