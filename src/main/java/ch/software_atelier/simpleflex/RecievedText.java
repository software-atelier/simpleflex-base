package ch.software_atelier.simpleflex;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecievedText extends RecievedData {

    static Logger LOG = LogManager.getLogger(RecievedText.class);

    private final StringBuffer _fullText = new StringBuffer();

    public RecievedText() {

    }

    public int type() {
        return this.TYPE_TEXT;
    }

    public void apendText(String text) {
        _fullText.append(text);
    }

    public String getText() {
        return _fullText.toString();
    }

    public String getURLDecodedText() {
        try {
            return URLDecoder.decode(_fullText.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            LOG.error("encoding not supported", e);
            return ""; // never happens if host supports utf-8!
        }
    }

    @Override
    public void done() {

    }
}
