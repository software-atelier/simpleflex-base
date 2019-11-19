/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.software_atelier.simpleflex.docs.impl;

import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import xmlwise.XmlElement;

/**
 *
 * @author tk
 */
public class XMLDoc extends WebDoc{
    private final String _data;
    private final String _mime;
    private final String _name;

    private XMLDoc(String data, String mime, String name){
        _data = data;
        _mime = mime;
        _name = name;
    }

    public static XMLDoc xml(XmlElement data){
        return new XMLDoc(data.toXml(),"application/xml","text.xml");
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
