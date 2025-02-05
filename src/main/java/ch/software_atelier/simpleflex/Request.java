/*
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.software_atelier.simpleflex;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import xmlwise.XmlElement;
import xmlwise.XmlParseException;
import xmlwise.Xmlwise;

/**
 * Holds a request with all contents sent by a WebBrowser.
 */
public class Request {

    public static String CONTENT_TYPE_MULTIPART = "multipart/form-data";

    public static String CONTENT_TYPE_APPLICATION = "application/x-www-form-urlencoded";

    public static String CONTENT_TYPE_XML = "application/xml";

    public static String CONTENT_TYPE_JSON = "application/json";

    public static String CONTENT_TYPE_JSON_PATCH = "application/json-patch+json";

    private JSONObject _json = null;

    private JSONArray _jsonArr = null;

    private XmlElement _xml = null;

    private byte[] rawData = new byte[0];

    private final ArrayList<RecievedData> _recievedData;

    private String _protocoll;

    private String _method;

    private String _requestString;

    private final HashMap<String, String> _headerContent;
    private final HashMap<String, Integer> _arrayCounter = new HashMap<>();
    
    private InetAddress _client;

    private boolean _secureConnection;

    private final ArrayList<RequestArgument> _arguments;

    private File _file;

    private String _fileName;

    ////
    // Some static Header-Specifiers
    ////
    public static final String HTTPHEADER_CONTENT_LENGTH = "Content-Length";

    public static final String HTTPHEADER_CONTENT_DISPOSITION = "Content-Disposition";

    public static final String HTTPHEADER_ACCEPT_LANGUAGE = "Accept-Language";

    public static final String HTTPHEADER_ACCEPT_ENCODING = "Accept-Encoding";

    public static final String HTTPHEADER_ACCEPT_CHARSET = "Accept-Charset";

    public static final String HTTPHEADER_ACCEPT = "Accept";

    public static final String HTTPHEADER_USER_AGENT = "User-Agent";

    public static final String HTTPHEADER_HOST = "Host";

    public static final String HTTPHEADER_REFERER = "Referer";

    public static final String HTTPHEADER_CONTENT_TYPE = "Content-Type";

    public static final String HTTPHEADER_CONNECTION = "Connection";

    public static final String METHOD_POST = "POST";

    public static final String METHOD_GET = "GET";

    public static final String METHOD_OPTIONS = "OPTIONS";

    public static final String METHOD_PUT = "PUT";

    public static final String METHOD_DELETE = "DELETE";

    public static final String METHOD_PATCH = "PATCH";

    static Logger LOG = LogManager.getLogger(RequestHandler.class);

    /**
     * 
     */
    public Request() {
        this._recievedData = new ArrayList<>();
        this._headerContent = new HashMap<>();
        this._arguments = new ArrayList<>();
    }

    ///////////////////////////////////
    // Setter
    ///////////////////////////////////

    /**
     * Adds a Header-Object to the Request-Object.
     * @param line The Headerline given by the Client
     * @return sucessfully added? yes/no
     */
    protected boolean addHeaderLine(String line) {
        StringTokenizer st = new StringTokenizer(line, ":");
        String key;
        String value;
        if (st.hasMoreElements())
            key = st.nextToken();
        else
            return false;

        if (line.length() > (key.length() + 1))
            value = line.substring(key.length() + 1).trim();
        else
            return false;

        _headerContent.put(key, value);
        return true;
    }

    /**
     * 
     * @param secure
     */
    public void setSecure(boolean secure) {
        _secureConnection = secure;
    }

    /**
     * 
     * @param client
     */
    public void setClient(InetAddress client) {
        _client = client;
    }

    /**
     * 
     * @param protocoll
     */
    public void setProtocoll(String protocoll) {
        _protocoll = protocoll;
    }

    /**
     * 
     * @param method
     */
    public void setMethod(String method) {
        _method = method;
    }

    /**
     * 
     * @param requestString
     */
    public void setRequestString(String requestString) {
        StringTokenizer st = new StringTokenizer(requestString, "?");
        _requestString = decode(st.nextToken());
        if (st.hasMoreTokens()) {
            String arguments = st.nextToken();
            StringTokenizer argumentTokenizer = new StringTokenizer(arguments, "&");
            while (argumentTokenizer.hasMoreTokens()) {
                String argVal = argumentTokenizer.nextToken();
                String key = null;
                String val = null;
                StringTokenizer argValTokenizer = new StringTokenizer(argVal, "=");
                if (argValTokenizer.hasMoreTokens())
                    key = argValTokenizer.nextToken();
                if (argValTokenizer.hasMoreTokens())
                    val = argValTokenizer.nextToken();
                if ((key != null) && (val != null))
                    _arguments.add(new RequestArgument(decode(key), decode(val)));
                else if (!argVal.isEmpty())
                    _arguments.add(new RequestArgument(decode(argVal), ""));
            }
        }

    }

    /**
     * 
     * @return
     */
    public RequestArgument[] arguments() {
        RequestArgument[] args = new RequestArgument[_arguments.size()];
        args = _arguments.toArray(args);
        return args;
    }

    /**
     * 
     * @param key
     * @return
     */
    public String getArgument(String key) {
        for (int i = 0; i < _arguments.size(); i++) {
            RequestArgument arg = _arguments.get(i);
            if (arg.key().equals(key))
                return arg.value();
        }
        return null;
    }

    /**
     * 
     * @param str
     * @return
     */
    private String decode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            // never thrown if host supports UTF-8
            return str;
        }
    }

    ///////////////////////////////////
    // Getter
    ///////////////////////////////////

    /**
     * Returns the version of the used protocol. Example: HTTP/1.0
     */
    public String getProtocoll() {
        return _protocoll;
    }

    /**
     * This method returns GET or POST
     * @return
     */
    public String getMethod() {
        return _method;
    }

    /**
     * Returns the Request-String. It is a Path to a File.
     * @return a path to a File like /index.htm or /something/abc.mpg
     */
    public String getReqestString() {
        return _requestString;
    }

    /**
     * 
     * @return
     */
    public String getLastRequestStringComponent() {
        List<String> path = Utils.tokenize(this.getReqestString(), "/");
        return path.get(path.size() - 1);

    }

    /**
     * 
     * @return
     */
    public HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        Set<String> keys = _headerContent.keySet();
        for (String key : keys) {
            String value = _headerContent.get(key);
            headers.put(key, value);
        }
        return headers;
    }

    /**
     * 
     * @param key
     * @return
     */
    public String getHeaderValue(String key) {
        if (_headerContent.containsKey(key)) {
            return _headerContent.get(key);
        }
        // This is a hack! In some cases, apache converts the headers to lower case...
        else return _headerContent.getOrDefault(key.toLowerCase(), null);
    }

    /**
     * Returns true, if the connnection is secure. (HTTPS)
     * @return
     */
    public boolean isSecureConnection() {
        return _secureConnection;
    }

    /**
     * 
     * @return
     */
    public String getHost() {
        return extractCalledHostByHeaderValue(getHeaderValue(HTTPHEADER_HOST));
    }

    /**
     * 
     * @return
     */
    public int getPort() {
        String port = extractCalledPortByHeaderValue(getHeaderValue(HTTPHEADER_HOST));
        return Integer.parseInt(port);
    }

    /**
     * @return
     */
    private String extractCalledHostByHeaderValue(String headerValue) {
        StringTokenizer st = new StringTokenizer(headerValue, ":");
        if (st.hasMoreTokens())
            return st.nextToken().trim();
        else
            return null;
    }

    /**
     * Returns the connected Port (Serverside!) by the host headervalue
     * @return the port used serverside extracted from the headevalue
     */
    private String extractCalledPortByHeaderValue(String headerValue) {
        StringTokenizer st = new StringTokenizer(headerValue, ":");
        if (st.hasMoreTokens())
            st.nextToken();
        else
            return null;
        if (st.hasMoreTokens())
            return st.nextToken().trim();
        else {
            if (isSecureConnection())
                return "443";
            else
                return "80";
        }
    }

    /**
     * Returns an InetAddress-Object that holds the IP-Address of the Client
     * @return
     */
    public InetAddress getClient() {
        return _client;
    }

    public void appendSinglePart(BufferedInputStream bis, long length) throws IOException {
        _file = File.createTempFile("tmp", "bin");
        try (FileOutputStream fos = new FileOutputStream(_file)) {

            int maxBuffer = 1024 * 32;
            int buffersize = (length < maxBuffer) ? (int) length : maxBuffer;
            byte[] buffer = new byte[buffersize];
            int len;
            while (length > 0) {
                len = bis.read(buffer);
                fos.write(buffer, 0, len);
                length = length - buffersize;
                buffersize = (length < maxBuffer) ? (int) length : maxBuffer;
            }
            fos.flush();
        }

    }

    /**
     * 
     * @param bis
     * @throws IOException
     */
    public void apendMultipart(BufferedInputStream bis, long length) throws IOException {
        LOG.debug("Adding multipart... " + length + "bytes");
        long l = length;
        long total_l = length;
        
        String boundaryString = getBoundary();
        String boundaryStringEnd = boundaryString+ "--";
        String line = "";
        
        File tmp = File.createTempFile("file", ".tmp");

        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            int maxBuffer = 1024 * 32;
            int buffersize = (length < maxBuffer) ? (int) length : maxBuffer;
            byte[] buffer = new byte[buffersize];
            int len;
            while (length > 0) {
                len = bis.read(buffer);
                fos.write(buffer, 0, len);
                length = length - len;
            }
            fos.flush();
        }

        LOG.debug("File saved. " + tmp.length() + "bytes");

        try (FileInputStream fis = new FileInputStream(tmp)) {

            HashMap<String, String> headers = new HashMap<String, String>();
            RecievedData recievedData = null;
            boolean isHeader = true;
            boolean headerSaved = false;
            boolean isFirstBoundary = true;
            
            UntilNewlineReader unReader = new UntilNewlineReader(fis, 1024);
            
            byte[] data = unReader.read();
            line = new String(data);
            LOG.debug("First line read: " + data.length + "bytes");
            
            long counter = 0;
            // While its not the end of The Multipart
            while (!(line.trim().endsWith(boundaryStringEnd))) {
                // If its a boundary, header follows
                if (line.trim().endsWith(boundaryString)) {
                    // if its the first boundary, where is no pre data.
                    // Theres nothing to save,
                    if (isFirstBoundary)
                        isFirstBoundary = false;
                    // if its not the first boundary, there is a pre data.
                    // save it.
                    else {
                        _recievedData.add(recievedData);
                        recievedData.done();
                    }
                    // Header follows
                    isHeader = true;
                    // New Header is not saved
                    headerSaved = false;
                }
                // If headercontent is expected and there is a plain line,
                // there is no more headercontent.
                else if (((isHeader) && line.trim().equals(""))) {
                    isHeader = false;
                }
                // If headercontent is expected...
                if (isHeader) {
                    // ...split it
                    String[] keyNValue = Utils.splitHeader(line);
                    // If the header has valid key and value...
                    if (keyNValue != null) {
                        // ...save it.
                        headers.put(keyNValue[0].toUpperCase(), keyNValue[1]);
                    }

                }
                // If no Headercontent is expected...
                else {
                    if (!headerSaved) {
                        recievedData = saveHeaders(headers);
                        headerSaved = true;
                    } else {
                        if (recievedData.type() == RecievedData.TYPE_FILE) {
                            ((RecievedFile) recievedData).appendToFile(data);
                        } else if (recievedData.type() == RecievedData.TYPE_TEXT) {
                            ((RecievedText) recievedData).appendText(line);
                        }
                    }
                }

                data = unReader.read();
                counter ++;
                l = l - data.length;
                if (counter%100 == 0){
                    LOG.trace("Read: "+(int)(100.0/total_l*l)+"% "+counter+" lines. "+l+" bytes left.");
                }
                line = new String(data);
            }
            _recievedData.add(recievedData);
            recievedData.done();

            LOG.debug("Received Data added: " + recievedData.fieldName());

        }
        tmp.delete();
    }

    private RecievedData saveHeaders(HashMap<String, String> headers) {
        String dispositionValue = headers.get(RecievedData._HEADER_CONTENT_DISPOSITION);
        StringTokenizer dispPartTokenizer = new StringTokenizer(dispositionValue, ";");
        String fieldName = null;
        String fileName = null;
        while (dispPartTokenizer.hasMoreTokens()) {
            String dispPart = dispPartTokenizer.nextToken().trim();

            if (dispPart.startsWith("name=\"")) {
                fieldName = dispPart.substring(6, dispPart.length() - 1).trim();
            } else if (dispPart.startsWith("filename=\"")) {
                fileName = dispPart.substring(10, dispPart.length() - 1).trim();
            }
        }
        
        // array handling if there is a field ending with []
        
        if (fieldName.endsWith("[]")){
            fieldName = fieldName.substring(0, fieldName.length()-2);
            int pos = -1;
            if (_arrayCounter.containsKey(fieldName)){
                pos = _arrayCounter.get(fieldName);
            }
            pos++;
            _arrayCounter.put(fieldName, pos);
            fieldName = fieldName+"["+pos+"]";
        }
        
        
        RecievedData recievedData;
        if (fileName == null) {
            recievedData = new RecievedText();
        } else {
            recievedData = new RecievedFile();
            ((RecievedFile) recievedData).setFileName(fileName);
        }
        recievedData.setFieldName(fieldName);
        return recievedData;
    }

    /**
     * 
     * @param bfis
     * @param length
     * @throws IOException
     */
    public void apendURLEncoded(BufferedInputStream bfis, long length) throws IOException {
        String sb = new String(load(bfis, length));
        StringTokenizer postTokenizer = new StringTokenizer(sb, "&");
        while (postTokenizer.hasMoreTokens()) {
            String element = postTokenizer.nextToken();
            StringTokenizer elementTokenizer = new StringTokenizer(element, "=");
            String key = null;
            String value = null;
            if (elementTokenizer.hasMoreTokens())
                key = elementTokenizer.nextToken();
            if (elementTokenizer.hasMoreTokens())
                value = elementTokenizer.nextToken();
            if ((key != null) && (value != null)) {
                RecievedText recievedText = new RecievedText();
                recievedText.setFieldName(key);
                recievedText.appendText(value);
                this.addRecievedData(recievedText);
            }
        }
    }

    /**
     * 
     * @param bfis
     * @param charset
     * @param length
     * @throws IOException
     */
    public void apendJSON(BufferedInputStream bfis, String charset, long length) throws IOException {
        String content = new String(load(bfis, length), charset);
        try {
            _json = new JSONObject(content);
        }
        catch (JSONException jsone) {
            try {
                this._jsonArr = new JSONArray(content);
            }
            catch (JSONException ignored) {

            }
        }
    }

    /**
     * 
     * @param bfis
     * @param charset
     * @param length
     * @throws IOException
     */
    public void apendJSONArray(BufferedInputStream bfis, String charset, long length) throws IOException {
        try {
            _jsonArr = new JSONArray(new String(load(bfis, length), charset));
        }
        catch (JSONException ignored) {

        }
    }

    /**
     * 
     * @param bfis
     * @param charset
     * @param length
     * @throws IOException
     */
    public void apendXML(BufferedInputStream bfis, String charset, long length) throws IOException {

        try {
            _xml = Xmlwise.createXml(new String(load(bfis, length), charset));
        }
        catch (XmlParseException xe) {

        }
    }

    private byte[] load(BufferedInputStream bis, long length) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int buffersize = (length < 512) ? (int) length : 512;
            byte[] buffer = new byte[buffersize];
            int len;
            while (length > 0) {
                len = bis.read(buffer);
                bos.write(buffer, 0, len);
                length = length - buffersize;
                buffersize = (length < 512) ? (int) length : 512;
            }
            bos.flush();
            rawData = bos.toByteArray();
            return rawData;
        }
    }

    private void addRecievedData(RecievedData rd) {
        _recievedData.add(rd);
    }

    /**
     * 
     * @return
     */
    public RecievedData[] getRecievedData() {
        RecievedData[] rd = new RecievedData[_recievedData.size()];
        rd = _recievedData.toArray(rd);
        return rd;
    }

    /**
     * 
     * @param field
     * @return
     */
    public RecievedText getRecievedText(String field) {
        for (RecievedData rd : _recievedData) {
            if (rd.fieldName().equals(field)) {
                if (rd instanceof RecievedText)
                    return ((RecievedText) rd);
            }
        }
        return null;
    }

    /**
     * 
     * @param field
     * @return
     */
    public RecievedFile getRecievedFile(String field) {
        for (RecievedData rd : _recievedData) {
            if (rd.fieldName().equals(field)) {
                if (rd instanceof RecievedFile)
                    return ((RecievedFile) rd);
            }
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public boolean isJSONReq() {
        return (this._json != null);
    }

    /**
     * 
     * @return
     */
    public JSONObject getJSONReq() {
        return _json;
    }

    /**
     * 
     * @return
     */
    public boolean isJSONArrReq() {
        return (this._jsonArr != null);
    }

    /**
     * 
     * @return
     */
    public JSONArray getJSONArrReq() {
        return _jsonArr;
    }

    /**
     * 
     * @return
     */
    public boolean isXMLReq() {
        return (this._xml != null);
    }

    /**
     * 
     * @return
     */
    public XmlElement getXMLReq() {
        return _xml;
    }

    /**
     * 
     * @return
     */
    public boolean isFormPostReq() {
        return _recievedData.size() > 0;
    }

    /**
     * 
     * @return
     */
    public boolean isSinglePartReq() {
        return _file != null;
    }

    /**
     *
     * @return
     */
    public byte[] getRawData(){
        return rawData;
    }

    /**
     * 
     * @return
     */
    public String getSinglePartFilename() {
        String headerValue = getHeaderValue(HTTPHEADER_CONTENT_DISPOSITION);
        if (headerValue != null) {
            List<String> pairs = Utils.tokenize(headerValue, ";");
            for (String pair : pairs) {
                List<String> pairList = Utils.tokenize(pair, "=");
                if (pairList.size() == 2) {
                    if (pairList.get(0).equals("filename")) {
                        return pairList.get(1).substring(1, pairList.get(1).length() - 1);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public String getSinglePartMimeType() {
        String headerValue = getHeaderValue(HTTPHEADER_CONTENT_TYPE);
        if (headerValue == null)
            return null;
        List<String> pairs = Utils.tokenize(headerValue, ";");
        if (pairs.isEmpty())
            return null;
        return pairs.get(0);
    }

    /**
     * 
     * @return
     */
    public File getSinglePartFile() {
        return _file;
    }

    /**
     * 
     * @return
     */
    public String getBoundary() {
        return extractBoundaryByHeaderValue(this.getHeaderValue(HTTPHEADER_CONTENT_TYPE));
    }

    /**
     * Extracts the Boundary String by the value of the Content-Type header.
     * @param headerValue The value of the Content-Type header.
     * @return The Boundary String
     */
    private String extractBoundaryByHeaderValue(String headerValue) {
        StringTokenizer st = new StringTokenizer(headerValue, ";");
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (token.startsWith("boundary=")) {
                return token.substring(9).trim();
            }
        }
        return null;
    }

    /**
     * 
     */
    public void cleanup() {
        if (_file != null) {
            if (!_file.delete()) {
                _file.deleteOnExit();
            }
        }
    }
}
