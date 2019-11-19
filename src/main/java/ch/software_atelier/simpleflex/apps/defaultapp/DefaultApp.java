package ch.software_atelier.simpleflex.apps.defaultapp;
import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.docs.impl.ErrorDoc;
import ch.software_atelier.simpleflex.docs.impl.FileDoc;
import ch.software_atelier.simpleflex.docs.impl.FileDocException;
import ch.software_atelier.simpleflex.docs.impl.FolderRedirectorDoc;
import ch.software_atelier.simpleflex.docs.WebDoc;
import ch.software_atelier.simpleflex.docs.impl.BeanShellDoc;
import ch.software_atelier.simpleflex.docs.impl.BeanShellDocException;
import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;

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
    private boolean _bsh;
    static Logger LOG = LogManager.getLogger(DefaultApp.class);

    @Override
    public void start(String name, HashMap<String,Object> config, SimpleFlexAccesser sfa){

        _sfa = sfa;
        String path = (String)config.get("$DOCPATH");
        if (path.endsWith("/"))
            path = path.substring(0,path.length()-1);
        _path = path;
        _name = name;
        
        Object useBsh = config.get("$BEANSHELL");
        if (useBsh instanceof Boolean){
            _bsh = (Boolean)useBsh;
        }
        else{
            _bsh = useBsh.toString().equalsIgnoreCase("on");
        }

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

        //BeanShell...
        if (this._bsh){
            String path = new StringTokenizer(
                    request.getReqestString(),"?").nextToken();
            if (path.endsWith(".bsh"))
                return handleBeanShell(request,cleanPath(path));
        }
        
        if (request.getMethod().equalsIgnoreCase("get"))
            return handleGET(request);
        else
            return new ErrorDoc("method not allowed");
    }
    
    private WebDoc handleBeanShell(Request request, String path){
        try{
            File beanShellFile = new File(_path,path);
            if (!beanShellFile.exists())
                return fileNotFound(path);
            BeanShellDoc bsd = new BeanShellDoc(beanShellFile);
            bsd.genDoc(request, _sfa);
            return bsd;
        
        }catch(BeanShellDocException bsde){
            if (bsde.getPartPos()>=0){
                return new ErrorDoc("Error in Part "+bsde.getPartPos()
                        +"\n"+bsde.getMessage());
            }
            else if (bsde.getPartPos()==bsde.EXCEPTION_FILE_IO){
                return new ErrorDoc("Something wrong with the requested File.\n" +
                        "Check access!!");
            }
            else if (bsde.getPartPos()==bsde.EXCEPTION_INVALIDE_PART){
                return new ErrorDoc("check the Start and end statements in the requested File!!");
            }
            else
                return new ErrorDoc("This Doc is never served!! ;)");
        }catch(Exception e){
            LOG.error("",e);
            return new ErrorDoc(e.getMessage());
        }
    }

    private WebDoc handleGET(Request request){
        
        WebDoc webDoc = null;
        try{
            //String path2 = "/";
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
        File indexFile = new File(file,"index.bsh");
        if (indexFile.exists()){
            return handleBeanShell(req, req.getReqestString()+"index.bsh");
        }
        
        indexFile = new File(file,"index.html");
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
        if (_name.length()==0){
            return path;
        }
        else{
            return path.substring(_name.length()+1,path.length());
        }
    }

}
