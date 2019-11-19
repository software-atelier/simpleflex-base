package ch.software_atelier.simpleflex;

public abstract class RecievedData {

    protected boolean _successfulReceived = true;

    protected String _fieldName = "";

    public static String _HEADER_CONTENT_TYPE = "CONTENT-TYPE";

    public static String _HEADER_CONTENT_DISPOSITION = "CONTENT-DISPOSITION";

    public static int TYPE_TEXT = 1;

    public static int TYPE_FILE = 2;

    public abstract int type();

    public abstract void done();

    public void setFieldName(String fieldName) {
        _fieldName = fieldName;
    }

    public String fieldName() {
        return _fieldName;
    }
}
