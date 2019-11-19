/*
 * OutputStreamDoc.java
 *
 * Created on 23. Mai 2008, 22:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.docs.impl;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author tk
 */
public class InputStreamDoc extends WebDoc{
    
    private final InputStream _is;
    private long _size;
    private String _name;
    private String _mime;

    public InputStreamDoc(InputStream is) {
        _is = is;
    }
    
    /**
     * 
     * @param size of the document
     */
    public void setSize(long size){
        _size = size;
    }

    @Override
    public long size(){
        return _size;
    }

    /**
     * 
     * @param mime 
     */
    public void setMime(String mime){
        _mime = mime;
    }

    @Override
    public String mime(){
        return _mime;
    }

    public void setName(String name){
        _name = name;
    }

    @Override
    public String name(){
        return _name;
    }

    @Override
    public byte[] byteData(){
        return null;
    }

    @Override
    public InputStream streamData(){
        return _is;
    }

    @Override
    public String dataType(){
        return DATA_STREAM;
    }

    @Override
    public void close(){
        try{
            _is.close();
        }catch(IOException e){
            //e.printStackTrace();
        }
    }   
}
