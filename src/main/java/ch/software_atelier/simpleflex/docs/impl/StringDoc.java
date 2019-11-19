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
 *  Nov 28, 2011 - 9:32:06 PM - StringDoc.java
 */
package ch.software_atelier.simpleflex.docs.impl;

import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.InputStream;

public class StringDoc extends WebDoc {
    
    private final String _data;
    private final String _mime;
    private final String _name;

    private StringDoc(String data, String mime, String name){
        _data = data;
        _mime = mime;
        _name = name;
    }

    public static WebDoc text(String data){
        return new StringDoc(data,"text/plain","text.txt");
    }

    public static WebDoc html(String data){
        return new StringDoc(data,"text/html","text.html");
    }

    public static WebDoc htmlContent(String data){
        data = "<html><body>"+data+"</body></html>";
        return new StringDoc(data,"text/html","text.html");
    }

    @Override
    public byte[] byteData() {
        return _data.getBytes();
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
        return _data.getBytes().length;
    }

    @Override
    public InputStream streamData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
