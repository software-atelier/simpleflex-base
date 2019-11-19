package ch.software_atelier.simpleflex.docs;
import ch.software_atelier.simpleflex.HTTPCodes;
import java.io.InputStream;
import java.util.ArrayList;

public abstract class WebDoc {
	
    private HTTPCode _httpCode = new HTTPCode(200,HTTPCodes.getMsg(200));
    
    private final ArrayList<HeaderField> _headers = new ArrayList<>();
    
    /**
     * This static var returns that the
     * data of a WebDoc-Object is a stream.
     */
    public static final String DATA_STREAM = "STREAM";

    /**
     * This static var defines that the
     * data of a WebDoc-Object is a byte[].
     */
    public static final String DATA_BYTE = "BYTE";

    /**
     * This method has to return the size of the Document in bytes.
     * @return 
     */
    public abstract long size();

    /**
     * This method has to return the MIME-Type of the Document.
     * Example: text/plain. You can also use the static Method
     * Utils.getMime(String ext) to
     * get the MIME-Type of a File-Extension.
     * 
     * @return 
     */
    public abstract String mime();

    /**
     * This method has to return the FileName of the Document.
     * @return 
     */
    public abstract String name();

    /**
     * If you want to return the documents data as a byte[], 
     * you have to do that here. Be sure, that the method dataType()
     * returns WebDoc.DATA_BYTE! Else just return null or something else.
     * If the method dataType() does not return WebDoc.DATA_BYTE, 
     * this method will never be called.
     * @return 
     */
    public abstract byte[] byteData();

    /**
     * If you want to return the documents data as a Stream, 
     * you have to do that here. Be sure, that the method dataType()
     * returns WebDoc.DATA_STREAM! Else just return null or something else.
     * If the method dataType() does not return WebDoc.DATA_STREAM, 
     * this method will never be called. The Stream will be closed by the Server-App
     * so you have nothing to do with that.
     * @return 
     */
    public abstract InputStream streamData();

    /**
     * Specifies the Type of served data.
     * Look at streamData() and/or byteData()
     * @return 
     */
    public abstract String dataType();

    /**
     * This method is called after the transmission. 
     * It can be used to cleanup something.
     */
    public abstract void close();        
    
    /**
     * Sets the HTTP Response Code. Default is 200 - OK.
     * HTTPCodes.getMsg() returns the message for all known codes, defined by the RFC.
     * @param code
     * @param message 
     */
    public final void setHTTPCode(int code, String message){
        _httpCode = new HTTPCode(code,message);
    }

    public HTTPCode getHttpCode(){
        return _httpCode;
    }

    public ArrayList<HeaderField> getHeaders(){
        return _headers;
    }

    public class HTTPCode{
        public HTTPCode(int code, String msg){
            this.code = code;
            this.message = msg;
        }
        
        public String message;
        public int code;
    }
}
