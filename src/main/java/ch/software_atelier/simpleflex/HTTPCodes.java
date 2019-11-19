package ch.software_atelier.simpleflex;

import java.util.HashMap;

/**
 * Source: https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
 * @author tk
 */
public class HTTPCodes {
    private static final HashMap<Integer,String> HTTP_CODES = new HashMap<>();
    static{
        HTTP_CODES.put(100,"Continue");
        HTTP_CODES.put(101,"Switching Protocols");
        HTTP_CODES.put(102,"Processing");
        HTTP_CODES.put(103,"Early Hints");
        HTTP_CODES.put(200,"OK");
        HTTP_CODES.put(201,"Created");
        HTTP_CODES.put(202,"Accepted");
        HTTP_CODES.put(203,"Non-Authoritative Information");
        HTTP_CODES.put(204,"No Content");
        HTTP_CODES.put(205,"Reset Content");
        HTTP_CODES.put(206,"Partial Content");
        HTTP_CODES.put(207,"Multi-Status");
        HTTP_CODES.put(208,"Already Reported");
        HTTP_CODES.put(226,"IM Used");
        HTTP_CODES.put(300,"Multiple Choices");
        HTTP_CODES.put(301,"Moved Permanently");
        HTTP_CODES.put(302,"Found");
        HTTP_CODES.put(303,"See Other");
        HTTP_CODES.put(304,"Not Modified");
        HTTP_CODES.put(305,"Use Proxy");
        HTTP_CODES.put(306,"Switch Proxy");
        HTTP_CODES.put(307,"Temporary Redirect");
        HTTP_CODES.put(308,"Permanent Redirect");
        HTTP_CODES.put(400,"Bad Request");
        HTTP_CODES.put(401,"Unauthorized");
        HTTP_CODES.put(402,"Payment Required");
        HTTP_CODES.put(403,"Forbidden");
        HTTP_CODES.put(404,"Not Found");
        HTTP_CODES.put(405,"Method Not Allowed");
        HTTP_CODES.put(406,"Not Acceptable");
        HTTP_CODES.put(407,"Proxy Authentication Required");
        HTTP_CODES.put(408,"Request Timeout");
        HTTP_CODES.put(409,"Conflict");
        HTTP_CODES.put(410,"Gone");
        HTTP_CODES.put(411,"Length Required");
        HTTP_CODES.put(412,"Precondition Failed");
        HTTP_CODES.put(413,"Payload Too Large");
        HTTP_CODES.put(414,"URI Too Long");
        HTTP_CODES.put(415,"Unsupported Media Type");
        HTTP_CODES.put(416,"Range Not Satisfiable");
        HTTP_CODES.put(417,"Expectation Failed");
        HTTP_CODES.put(418,"I'm a teapot");
        HTTP_CODES.put(421,"Misdirected Request");
        HTTP_CODES.put(422,"Unprocessable Entity");
        HTTP_CODES.put(423,"Locked");
        HTTP_CODES.put(424,"Failed Dependency");
        HTTP_CODES.put(426,"Upgrade Required");
        HTTP_CODES.put(428,"Precondition Required");
        HTTP_CODES.put(429,"Too Many Requests");
        HTTP_CODES.put(431,"Request Header Fields Too Large");
        HTTP_CODES.put(451,"Unavailable For Legal Reasons");
        HTTP_CODES.put(500,"Internal Server Error");
        HTTP_CODES.put(501,"Not Implemented");
        HTTP_CODES.put(502,"Bad Gateway");
        HTTP_CODES.put(503,"Service Unavailable");
        HTTP_CODES.put(504,"Gateway Timeout");
        HTTP_CODES.put(505,"HTTP Version Not Supported");
        HTTP_CODES.put(506,"Variant Also Negotiates");
        HTTP_CODES.put(507,"Insufficient Storage");
        HTTP_CODES.put(508,"Loop Detected");
        HTTP_CODES.put(510,"Not Extended");
        HTTP_CODES.put(511,"Network Authentication Required");
        // Unofficial codes
        HTTP_CODES.put(103,"Checkpoint");
        //HTTP_CODES.put(420,"Method Failure");
        HTTP_CODES.put(420,"Enhance Your Calm");
        HTTP_CODES.put(450,"Blocked by Windows Parental Controls");
        HTTP_CODES.put(498,"Invalid Token");
        HTTP_CODES.put(499,"Token Required");
        HTTP_CODES.put(509,"Bandwidth Limit Exceeded");
        HTTP_CODES.put(530,"Site is frozen");
        HTTP_CODES.put(598,"Network read timeout error");
        // Internet Information Services
        HTTP_CODES.put(440,"Login Time-out");
        HTTP_CODES.put(449,"Retry With");
        HTTP_CODES.put(451,"Redirect");
        // nginx
        HTTP_CODES.put(444,"No Response");
        HTTP_CODES.put(495,"SSL Certificate Error");
        HTTP_CODES.put(496,"SSL Certificate Required");
        HTTP_CODES.put(497,"HTTP Request Sent to HTTPS Port");
        HTTP_CODES.put(499,"Client Closed Request");
        // Cloudflare
        HTTP_CODES.put(520,"Unknown Error");
        HTTP_CODES.put(521,"Web Server Is Down");
        HTTP_CODES.put(522,"Connection Timed Out");
        HTTP_CODES.put(523,"Origin Is Unreachable");
        HTTP_CODES.put(524,"A Timeout Occurred");
        HTTP_CODES.put(525,"SSL Handshake Failed");
        HTTP_CODES.put(526,"Invalid SSL Certificate");
        HTTP_CODES.put(527,"Railgun Error");
    }
    
    /**
     * 
     * @param code
     * @return 
     */
    public static String getMsg(int code){
        return HTTP_CODES.get(code);
    }
}
