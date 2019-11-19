/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.docs.impl;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author tk
 */
public class TemplateDoc extends WebDoc{
    
    private String _templ;
    private final ArrayList _values = new ArrayList();
    private final ArrayList _placeholders = new ArrayList();
    private byte[] _templRes = new byte[0];
    private String _name;
    private String _placeholderPrefix = "<!--";
    private String _placeholderSuffix = "-->";
    
    
    public TemplateDoc(File file,String prefix,String suffix)throws IOException{
        this(file);
        _placeholderPrefix = prefix;
        _placeholderSuffix = suffix;
    }
    
    public TemplateDoc(File file)throws IOException{
        _name = file.getName();
        FileInputStream fis = new FileInputStream(file);
        ByteBuffer bb = ByteBuffer.allocate((int)file.length());
        int i = 0;
        while ((i=fis.read())!=-1)
            bb.put((byte)i);
        _templ = new String(bb.array());
    }
    
    /**
     * Adds a placeholder and a refering value.
     * Call the method replace() to replace all placeholders by the refering values.
     * @param placeholder
     * @param value
     */
    public void add(String placeholder, String value){
        _values.add(value);
        _placeholders.add(_placeholderPrefix+placeholder+_placeholderSuffix);
        _templRes = new byte[0];
    }
    
    /**
     * Calculates the document. It replaces all placeholders by the values.
     */
    public void replace(){
        String tmplResult = _templ;
        for (int i=0;i<_values.size();i++){
            Pattern pattern = Pattern.compile((String)_placeholders.get(i));
            Matcher matcher = pattern.matcher(tmplResult);
            tmplResult = matcher.replaceAll((String)_values.get(i));
        }
        _templRes=tmplResult.getBytes();

    }

    public void clear(){
        _values.clear();
        _placeholders.clear();
    }

    @Override
    public long size(){
        return (long)(_templRes.length);
    }

    @Override
    public String mime(){
        return "text/html";
    }

    @Override
    public String name(){
        return "_name";
    }

    @Override
    public byte[] byteData(){
        return _templRes;
    }

    @Override
    public InputStream streamData(){
        return null;
    }

    @Override
    public String dataType(){
        return DATA_BYTE;
    }

    @Override
    public void close(){}

}
