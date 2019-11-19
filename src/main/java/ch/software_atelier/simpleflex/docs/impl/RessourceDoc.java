/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.software_atelier.simpleflex.docs.impl;

import ch.software_atelier.simpleflex.Utils;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

/**
 *
 * Serves a file from the ressources
 */
public class RessourceDoc extends WebDoc{
    
    private byte[] _data = new byte[0];
    private String _name = "";
    private String _mime = "";

    public RessourceDoc(String path){
        try{
            _data = ressource2ByteArray(path);
            
            StringTokenizer st = new StringTokenizer(path, "/");
            while (st.hasMoreTokens()){
                _name = st.nextToken();
            }
            String ext = "";
            st = new StringTokenizer(_name, ".");
            while (st.hasMoreTokens()){
                ext = st.nextToken();
            }
            _mime = Utils.getMime(ext);
        }catch(IOException e){
            _data = e.getMessage().getBytes();
            _mime = "text/plain";
            _name = "error.txt";
        }
    }
    
    private byte[] ressource2ByteArray(String path) throws IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        ByteArrayOutputStream out;
        try (InputStream in = classLoader.getResourceAsStream(path)) {
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int r = in.read(buffer);
                if (r == -1) break;
                out.write(buffer, 0, r);
            }
        }
        return out.toByteArray();
    }

    @Override
    public long size() {
        return _data.length;
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
    public byte[] byteData() {
        return _data;
    }

    @Override
    public InputStream streamData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String dataType() {
        return WebDoc.DATA_BYTE;
    }

    @Override
    public void close() {
    }

}
