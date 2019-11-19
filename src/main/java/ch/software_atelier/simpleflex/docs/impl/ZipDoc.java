/*
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.software_atelier.simpleflex.docs.impl;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * This WebDoc serves a few files as one ZIP-File. 
 */
public class ZipDoc extends WebDoc {

    private InputStream _is;
    private String _fileName;
    private File _tmpFile;
    
    /**
     * The Constructor
     * @param files A File[], that holds all files to zip
     * @param fileName The FileName of the ZIP-File
     */
    public ZipDoc(File[] files, String fileName){
        try{
            _tmpFile = File.createTempFile("zip","zip");
            _tmpFile.deleteOnExit();
            try (ZipOutputStream zos = new ZipOutputStream(
                    new FileOutputStream(_tmpFile))) {
                int i = 0;
                while (i<files.length){
                    ZipEntry zipEntry = new ZipEntry(files[i].getName());
                    zipEntry.setSize(files[i].length());
                    zipEntry.setTime(files[i].lastModified());
                    zipEntry.setComment("Ziped by SimpleFlex DataStore!");
                    FileInputStream fis = new FileInputStream(files[i]);
                    zos.putNextEntry(zipEntry);
                    int len;
                    byte[] buf = new byte[2048];
                    while ((len = fis.read(buf))>0){
                        zos.write(buf,0,len);
                    }
                    i++;
                }
                zos.flush();
            }
            _is = new FileInputStream(_tmpFile);
            _fileName = fileName;
        }catch(IOException ioe){}
    }

    @Override
    public long size(){
        return _tmpFile.length();
    }

    @Override
    public String mime(){
        return "application/zip";
    }

    @Override
    public String name(){
        return _fileName;
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
        _tmpFile.delete();
    }

}
