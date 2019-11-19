package ch.software_atelier.simpleflex.docs.impl;
import ch.software_atelier.simpleflex.HTTPCodes;
import ch.software_atelier.simpleflex.Utils;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.InputStream;

public class ErrorDoc extends WebDoc{
    private String _message;
    
    private String _content;

    public ErrorDoc(String message) {
        _message = message;
        setHTTPCode(400, HTTPCodes.getMsg(400));
        generate();
    }

    public ErrorDoc(String message, int code, String msg) {
        _message = message;
        setHTTPCode(code, msg);
        generate();
    }

    @Override
    public long size() {
        return _content.length();
    }

    @Override
    public String mime() {
        return Utils.getMime("html");
    }

    @Override
    public String name() {
        return "error.html";
    }

    @Override
    public byte[] byteData() {
        return _content.getBytes();
    }

    @Override
    public InputStream streamData() {
        return null;
    }

    @Override
    public String dataType() {
        return WebDoc.DATA_BYTE;
    }

    @Override
    public void close() {
    }

    private void generate(){
        _content = "<html><body>"+_message+"</body></html>";
        
    }

    public static WebDoc err500_InternalServerError(String message){
        return new ErrorDoc(message, 500, "Internal Server Error");
    }

    public static WebDoc err501_NotImplemented(String message){
        return new ErrorDoc(message, 501, "Not Implemented");
    }
    
}
