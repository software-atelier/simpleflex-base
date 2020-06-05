package ch.software_atelier.simpleflex.docs.impl;
import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.Utils;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Represents a File
 */
public class FileDoc extends WebDoc{
    private File _file;
    private boolean _delete = false;

    /**
     * It simply creates a WebDoc-Object that serves the specified File.
     * @param file the File, that must be sent
     * @throws FileDocException if the File does not exists.
     */
    public FileDoc(File file)throws FileDocException{
        this(file,false);
    }


    /**
     * It simply creates a WebDoc-Object that serves the specified File.
     * @param file the File, that must be sent
     * @param delete delete after dent
     * @throws FileDocException if the File does not exists.
     */
    public FileDoc(File file, boolean delete)throws FileDocException{
            if (!file.isFile()){
                    file = new File(file,"index.html");
                    if (!file.exists())
                            throw new FileDocException(
                                    "The File "+file.getAbsolutePath()+ " does not exists!");
            }
            if (!file.exists())
                    throw new FileDocException(
                            "The File "+file.getAbsolutePath()+ " does not exists!");
            _file = file;
            _delete = delete;
    }

    public static WebDoc fileByReq(Request req, File baseDir, String appName) throws FileDocException{
        String path = req.getReqestString().substring(appName.length()+1);
        File file = new File(baseDir,path);

        if (file.isFile())
            return new FileDoc(file);
        else if (file.isDirectory() && !file.getAbsolutePath().endsWith("/")){
            return new FolderRedirectorDoc(req.getReqestString());
        }
        else{
            file = new File(file,"index.html");
            if (!file.exists())
                        throw new FileDocException(
                                "The File "+file.getAbsolutePath()+ " does not exists!");
            else
                return new FileDoc(file);
        }

    }

    public FileDoc(Request req, File baseDir)throws FileDocException{
        String path = req.getReqestString();
        File file = new File(baseDir,path);

        if (!file.isFile()){
                file = new File(file,"index.html");
                if (!file.exists())
                        throw new FileDocException(
                                "The File "+file.getAbsolutePath()+ " does not exists!");
        }
        if (!file.exists())
                throw new FileDocException(
                        "The File "+file.getAbsolutePath()+ " does not exists!");
        _file = file;
    }

    @Override
    public long size(){
            return _file.length();
    }

    @Override
    public String mime(){
            return Utils.getMimeFromFilePath(_file.getName());
    }

    @Override
    public String name(){
            return _file.getName();
    }

    @Override
    public byte[] byteData(){
            return null;
    }

    @Override
    public InputStream streamData(){
        BufferedInputStream fis = null;
            try{
                fis = new BufferedInputStream(new FileInputStream(_file));
            }catch(FileNotFoundException th){}
            return fis;
    }

    @Override
    public String dataType(){
            return DATA_STREAM;
    }

    @Override
    public void close(){
        if (_delete)
            _file.delete();
    }

}
