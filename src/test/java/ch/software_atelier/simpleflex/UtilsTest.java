package ch.software_atelier.simpleflex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void resolvesBundledAndFallbackMimeTypes() {
        assertEquals("text/html", Utils.getMimeFromFilePath("page.HTML"));
        assertEquals("application/octet-stream", Utils.getMimeFromFilePath("archive.unknown-extension"));
    }

    @Test
    void registeredMimeTypesAreCaseInsensitive() {
        Utils.registerMime("CuStOm", "application/x-custom");
        assertEquals("application/x-custom", Utils.getMimeFromFilePath("file.CUSTOM"));
    }

    @Test
    void splitsOnlyHeadersWithKeyAndValue() {
        assertArrayEquals(new String[]{"Content-Type", " text/plain"}, Utils.splitHeader("Content-Type: text/plain"));
        assertNull(Utils.splitHeader("Content-Type"));
    }

    @Test
    void parsesDigitsUpToDecimalSeparator() {
        assertEquals(1L, Utils.parseLong("CHF 1,234.50"));
        assertEquals(-42L, Utils.parseLong("-42.75"));
        assertEquals(0L, Utils.parseLong("no digits"));
    }

    @Test
    void tokenizesWhilePreservingDelimitedSections() {
        assertEquals(Arrays.asList("one", "\"two,three\"", "four"),
                Utils.tokenizeByIgnoringEnclosure("one,\"two,three\",four", ',', '\"'));
    }

    @Test
    void removesOnlyUnescapedCharactersAndDropsEscapeMarker() {
        assertEquals("abc-d", Utils.removeNonEscaped("a-bc\\-d", '-', '\\'));
    }

    @Test
    void readsLineIncludingNewlineAndRepresentsEmptyEofAsNewline() throws IOException {
        assertArrayEquals("hello\n".getBytes("UTF-8"),
                Utils.readBytesUntilNewLine(new ByteArrayInputStream("hello\nworld".getBytes("UTF-8"))));
        assertArrayEquals(new byte[]{'\n'}, Utils.readBytesUntilNewLine(new ByteArrayInputStream(new byte[0])));
    }
}
