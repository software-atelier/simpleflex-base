package ch.software_atelier.simpleflex;

import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.impl.ErrorDoc;
import ch.software_atelier.simpleflex.docs.HeaderField;
import ch.software_atelier.simpleflex.docs.WebDoc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This Class finds out the WebApp and starts it. Don't use it to implement WebApps!
 */
public class RequestHandler extends Thread {
    static Logger LOG = LogManager.getLogger(RequestHandler.class);

    private final Socket _socket;

    private final WebAppHandler _webAppHandler;

    private PrintWriter _writer;

    private OutputStream _os;

    private BufferedInputStream _is;

    public RequestHandler(WebAppHandler webAppHandler, Socket socket, boolean secure) {
        _webAppHandler = webAppHandler;
        _socket = socket;
    }

    /**
     * Switches the WebApp
     */
    @Override
    public void run() {
        try {
            setupStreams();

            Request request;
            // The first Line is the request.
            // Form: GET|POST /something.html HTTP/1.0
            String req = Utils.readUntilNewLine(_is, true);

            FirstLine fl = tokenizeFirstLine(req);

            if (fl.valid()) {
                if (fl.method.equals(Request.METHOD_OPTIONS)) {
                    sendOptionsHeader();
                    flushAndCloseSocket();
                    return;
                }
                request = fillRequest(fl);
            } else {
                sendHeaderNotSupported();
                flushAndCloseSocket();
                return;

            }

            try (CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(request.getHeaders())) {
                ctc.putAll(request.getHeaders());
                String hostname = request.getHost();
                LOG.debug(LogMarker.requestMarker(hostname), request.getMethod()+" "+request.getReqestString());
                // Switch now the WebApp
                switchNow(request);
                if (request.getRecievedData().length > 0)
                    deleteTempFiles(request);
                flushAndCloseSocket();
            }
        } catch (Throwable th) {
            LOG.error("While reading the Request", th);
        }
    }

    private void setupStreams() throws IOException {
        _is = new BufferedInputStream(_socket.getInputStream());
        _os = new BufferedOutputStream(_socket.getOutputStream());
        _writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_os)));
    }

    private void flushAndCloseSocket() throws IOException {
        if (!_socket.isClosed()) {
            _socket.getOutputStream().flush();
            _socket.close();
        }
    }

    private void deleteTempFiles(Request request) {
        RecievedData[] recievedData = request.getRecievedData();
        int i = 0;
        while (i < recievedData.length) {
            if (recievedData[i].type() == RecievedData.TYPE_FILE)
                ((RecievedFile) recievedData[i]).deleteTmpFile();
            i++;
        }
    }

    private FirstLine tokenizeFirstLine(String req) {
        FirstLine fl = new FirstLine();
        StringTokenizer st = new StringTokenizer(req);
        if (st.hasMoreTokens())
            fl.method = st.nextToken().trim();
        if (st.hasMoreTokens())
            fl.requestString = st.nextToken().trim();
        if (st.hasMoreTokens())
            fl.protocoll = st.nextToken().trim();

        return fl;
    }

    private Request fillRequest(FirstLine fl) throws Throwable {

        List<String> headers = readHeaders();
        Request request = new Request();
        request.setMethod(fl.method);
        request.setRequestString(fl.requestString);
        request.setProtocoll(fl.protocoll);
        request.setClient(_socket.getInetAddress());
        // headers
        for (String headerLine : headers) {
            request.addHeaderLine(headerLine);
        }

        String contentLength = request.getHeaderValue(Request.HTTPHEADER_CONTENT_LENGTH);
        boolean hasContent = contentLength != null && Integer.parseInt(contentLength) > 0;

        if (hasContent) {
            if (checkUpload(request))
                fillUpPost(request, contentLength);
            else {
                String domainname = request.getHost();
                sendDoc(new ErrorDoc("data segment too large"), domainname);
                _socket.close();
            }
        }

        return request;
    }

    private List<String> readHeaders() throws IOException {
        ArrayList<String> headers = new ArrayList<>();
        String headerline = Utils.readUntilNewLine(_is, true);
        while (headerline != null && !headerline.isEmpty()) {
            headers.add(headerline);
            headerline = Utils.readUntilNewLine(_is, true);
        }
        return headers;
    }

    private boolean checkUpload(Request request) {
        String plainreq = request.getReqestString();

        WebApp wa = _webAppHandler.getWebApp(request);
        if (wa == null)
            return false;

        String postingSizeStr = request.getHeaderValue(Request.HTTPHEADER_CONTENT_LENGTH);
        if (postingSizeStr == null)
            return false;

        long actual = new Long(postingSizeStr);
        long max = wa.maxPostingSize(plainreq);

        if (max == WebApp.UNLIMITED_UPLOAD)
            return true;
        return (actual <= max);

    }

    private void fillUpPost(Request request, String lengthString) throws IOException {
        lengthString = lengthString.trim();
        long length = Utils.parseLong(lengthString);

        String contentType = request.getHeaderValue(Request.HTTPHEADER_CONTENT_TYPE);

        String charset = "UTF-8";

        StringTokenizer st = new StringTokenizer(contentType, ";");
        contentType = st.nextToken();
        if (st.hasMoreElements()) {
            String tmpCharSet = st.nextToken();
            st = new StringTokenizer(tmpCharSet, "=");
            if (st.hasMoreElements())
                st.nextToken();
            if (st.hasMoreElements())
                charset = st.nextToken();
        }

        if (contentType.equals(Request.CONTENT_TYPE_APPLICATION)) {
            request.apendURLEncoded(_is, length);
        } else if (contentType.startsWith(Request.CONTENT_TYPE_MULTIPART)) {
            request.apendMultipart(_is, length);
        } else if (contentType.startsWith(Request.CONTENT_TYPE_XML)) {
            request.apendXML(_is, charset, length);
        } else if (contentType.equalsIgnoreCase(Request.CONTENT_TYPE_JSON)) {
            request.apendJSON(_is, charset, length);
        } else if (contentType.equalsIgnoreCase(Request.CONTENT_TYPE_JSON_PATCH)) {
            request.apendJSONArray(_is, charset, length);
        } else {
            request.appendSinglePart(_is, length);
        }
    }

    /**
     * Switchs the WebApp
     *
     * @param request
     */
    private void switchNow(Request request) {
        WebApp app = _webAppHandler.getWebApp(request);
        String domainname = request.getHost();
        if (app == null) {
            sendDoc(new ErrorDoc("unknown domainname: " + domainname), domainname);
            LOG.info(LogMarker.CONNECTION, "unknown domainName: " + domainname);
            return;
        }

        try {
            try (WebDoc doc = app.process(request)) {
                sendDoc(doc, domainname);
                LOG.debug(LogMarker.responseMarker(domainname), request.getReqestString() + " " + doc.size() + "bytes");
                request.cleanup();
            }
        } catch (Throwable th) {
            LOG.error(LogMarker.CONNECTION, "Error in the WebApp " + app, th);
            sendDoc(new ErrorDoc("Error in the WebApp " + app + "\n" + th), domainname);
        }
    }

    private void sendDoc(WebDoc doc, String domainname) {
        try {
            sendHeader(doc);
            if (doc.dataType().equals(WebDoc.DATA_BYTE)) {
                _os.write(doc.byteData());

            } else {
                BufferedInputStream is = new BufferedInputStream(doc.streamData());

                byte[] buffer = new byte[4 * 1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    _os.write(buffer, 0, len);
                }


            }
            _os.flush();
        } catch (Throwable th) {
            LOG.error(LogMarker.CONNECTION, "while sending Data to the Browser", th);
        }
    }

    /**
     * Sends the header
     *
     * @param doc The WebDoc, that must be sent
     */
    private void sendHeader(WebDoc doc) {

        _writer.println("HTTP/1.1 " + doc.getHttpCode().code + " " + doc.getHttpCode().message);
        _writer.println("Connection: Close");
        _writer.println("Access-Control-Allow-Origin: *");
        _writer.println("Access-Control-Allow-Methods: POST, GET, DELETE, PUT, PATCH, HEAD, OPTIONS");
        _writer.println("Access-Control-Allow-Headers: X-PINGOTHER, Content-Type, Authorization, Test");
        _writer.println("Server: " + SimpleFlexBase.SERVER_VERS);
        _writer.println("Date: " + Utils.getHTTPDateHeaderValue(new Date()));
        _writer.println("Content-Type: " + doc.mime());
        _writer.println("Content-length: " + (doc.size()));

        for (HeaderField header : doc.getHeaders()) {
            _writer.println(header.name() + " " + header.value());
        }

        _writer.println("");
        _writer.flush();
    }

    private void sendHeaderNotSupported() {
        _writer.println("HTTP/1.1 501 Not Implemented");
        _writer.println();
        _writer.flush();
    }

    private void sendOptionsHeader() {
        LOG.debug("Sending OPTION Response");
        _writer.println("HTTP/1.1 200 OK");
        _writer.println("Connection: Close");
        _writer.println("Server: " + SimpleFlexBase.SERVER_VERS);
        _writer.println("Date: " + Utils.getHTTPDateHeaderValue(new Date()));
        _writer.println("Content-Type: application/json");
        _writer.println("Access-Control-Allow-Origin: *");
        _writer.println("Access-Control-Allow-Methods: POST, GET, DELETE, PUT, PATCH, HEAD, OPTIONS");
        _writer.println("Access-Control-Allow-Headers: X-PINGOTHER, Content-Type, Authorization, Test");
        _writer.println("Content-length: 0");

        _writer.println();
        _writer.flush();
    }

    private static class FirstLine {
        public String method = null;

        public String requestString = null;

        public String protocoll = null;

        public boolean valid() {
            boolean valid = ((method != null) && (requestString != null) && (protocoll != null));
            if (valid)
                valid = (method.equals(Request.METHOD_POST) || method.equals(Request.METHOD_GET)
                        || method.equals(Request.METHOD_OPTIONS) || method.equals(Request.METHOD_DELETE)
                        || method.equals(Request.METHOD_PATCH) || method.equals(Request.METHOD_PUT));
            return valid;
        }
    }

}
