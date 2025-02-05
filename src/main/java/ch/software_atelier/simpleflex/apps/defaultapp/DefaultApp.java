package ch.software_atelier.simpleflex.apps.defaultapp;
import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.impl.ErrorDoc;
import ch.software_atelier.simpleflex.docs.impl.FileDoc;
import ch.software_atelier.simpleflex.docs.impl.FileDocException;
import ch.software_atelier.simpleflex.docs.impl.FolderRedirectorDoc;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * This is the default Web-App.
 * It just returns the requested document.
 * Don't use it for your own implenemtation.
 */
public class DefaultApp implements WebApp{
    private String _path;
    private String _name;
    private SimpleFlexAccesser _sfa;
    static Logger LOG = LogManager.getLogger(DefaultApp.class);

    @Override
    public void start(String name, HashMap<String,Object> config, SimpleFlexAccesser sfa){

        _sfa = sfa;
        String path = (String)config.get("$DOCPATH");
        if (path.endsWith("/"))
            path = path.substring(0,path.length()-1);
        _path = path;
        _name = name;

    }

    public boolean allowUpload(String path){
        File file = new File(_path,path);
        return file.exists();
            //>> may also check for an entry in the Password-File...
    }

    @Override
    public long maxPostingSize(String requestedPath){
        return 1024*10;
    }

    @Override
    public WebDoc process(Request request){
        if (!checkPath(request.getReqestString()))
            return new ErrorDoc("??");
        
        if (request.getMethod().equalsIgnoreCase("get"))
            return handleGET(request);
        else
            return new ErrorDoc("method not allowed");
    }


    private WebDoc handleGET(Request request){
        
        WebDoc webDoc = null;
        try{
            String req = cleanPath(request.getReqestString());
            
            File file = new File(_path,req);
            if (file.isFile()){
                if (file.getName().equals("PW"))
                    return fileNotFound(req);
                        
                
                else
                    return new FileDoc(file);
            }
            else{
                /*
                 * IF THE PATH TO A DIRECTORY ENDS WITH "/",
                 * IT RETURNS THE index-FILE.
                 * IF THE PATH TO THE DIR DOES NOT WITHOUT A "/",
                 * A REDIRECT-DOC TO THE PATH WITH THE "/" IS RETURNED!
                 */
                if (request.getReqestString().endsWith("/")){
                   return handleIndex(request, file);
                }
                else
                     webDoc = new FolderRedirectorDoc(request.getReqestString());
                
            }
        }catch(FileDocException fde){
            return fileNotFound(request.getReqestString());
        }
        return webDoc;
    }
    
    private WebDoc handleIndex(Request req, File file)throws FileDocException{
        File indexFile = new File(file,"index.html");
        return new FileDoc(indexFile);
    }
    
    private WebDoc fileNotFound(String req){
        return new ErrorDoc("404 - File not found");
    }
    
    
    private boolean checkPath(String path){
        return !path.matches(".*\\/\\.\\.\\/.*");
    }

    @Override
    public void quit(){
    }

    private String cleanPath(String path){
        if (_name.isEmpty()){
            return path;
        }
        else{
            return path.substring(_name.length()+1);
        }
    }

}
