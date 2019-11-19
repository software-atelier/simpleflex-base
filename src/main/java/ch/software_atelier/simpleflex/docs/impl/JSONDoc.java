/*
 *      __                   _____    _____       __      __  _
 *     / /_  ____  ____ ___ |__  /   / ___/____  / /_  __/ /_(_)___  ____  _____
 *    / __ \/ __ \/ __ `__ \ /_ <    \__ \/ __ \/ / / / / __/ / __ \/ __ \/ ___/
 *   / / / / /_/ / / / / / /__/ /   ___/ / /_/ / / /_/ / /_/ / /_/ / / / (__  )
 *  /_/ /_/\____/_/ /_/ /_/____/   /____/\____/_/\__,_/\__/_/\____/_/ /_/____/
 * 
 *                                                          Tobias Kamber
 *                                                          info[at]hom3.ch
 *                                                          http://www.hom3.ch
 *  Nov 28, 2011 - 9:41:37 PM - JSONDoc.java
 */
package ch.software_atelier.simpleflex.docs.impl;

import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONDoc extends WebDoc{
    static Logger LOG = LogManager.getLogger(JSONDoc.class);

    private final String _data;
    private final String _mime;
    private final String _name;
    
    private JSONDoc(String data, String mime, String name){
        _data = data;
        _mime = mime;
        _name = name;
    }
    
    public static JSONDoc json(JSONObject data){
        if (data.has("#array")){
            try{
                return new JSONDoc(data.get("#array").toString(),"application/octet-stream; charset=utf-8","text.json");
            }catch(JSONException e){
                LOG.error("failed to parse JSONARRAY: "+data.get("#array").toString(),e);
                return new JSONDoc("[]","application/octet-stream; charset=utf-8","text.json");
            }
        }
        return new JSONDoc(data.toString(),"application/octet-stream; charset=utf-8","text.json");
    }
    
    public static JSONDoc json(JSONArray data){
        return new JSONDoc(data.toString(),"application/octet-stream; charset=utf-8","text.json");
    }

    @Override
    public byte[] byteData() {
        try{
        return _data.getBytes("UTF-8");
        }catch(UnsupportedEncodingException e){
            return new byte[0];
        }
    }

    @Override
    public void close() {
        
    }

    @Override
    public String dataType() {
        return WebDoc.DATA_BYTE;
    }

    @Override
    public String mime() {
        return _mime;
    }

    @Override
    public String name() {
        return _name;
    }

    @Override
    public long size() {
        try{
        return _data.getBytes("UTF-8").length;
        }catch(UnsupportedEncodingException e){
            return 0;
        }
    }

    @Override
    public InputStream streamData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
