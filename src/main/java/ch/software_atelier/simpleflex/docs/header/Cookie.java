/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.software_atelier.simpleflex.docs.header;

import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.docs.WebDoc;

import java.util.HashMap;

/**
 * TODO check this!
 * @author tk
 */
public class Cookie extends CookieWrapperDoc{
    
    /**
     * 
     * @param webDoc
     * @param cookie 
     */
    public Cookie(WebDoc webDoc, String cookie){
        super (webDoc);
        this.addCookieAttr("value", cookie);
    }
    
    /**
     * 
     * @param req
     * @return 
     */
    public static String getCookie(Request req){
        String cookie = req.getHeaderValue("COOKIE");
        if (cookie==null)
            return "";
        HashMap<String,String> cookies = CookieWrapperDoc.parseCookie(req);
        if (cookies.containsKey("value"))
            return cookies.get("value");
        else return "";
    }
    
}
