/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.interfaces.file;
import java.io.*;
import java.util.ArrayList;

public class FileInterfaceWriter {
    private final File _dst;
    private final ArrayList _commands = new ArrayList();
    private final long _timeout = 2000;
    private int _reason = 0;
    
    
    private final String _newline="\n";
    
    private static final String _STARTTAG="<Interface>";
    private static final String _ENDTAG="</Interface>";
    private static final String _KEY_CLASSPATH = "CLASSPATH";
    
    /**
     * returned by reason() if the timeout between writing and reading by the Server-Interface is expired.
     */
    public static int ERRORCODE_TIMEOUT_EXPIERD = 1;
    
    /**
     * returned by reason() if the file was already existing on calling execute().
     */
    public static int ERRORCODE_FILE_ALREADY_EXISTS = 2;
    
    /**
     * returned by reason() if 
     */
    public static int ERRORCODE_IOEXCEPTION = 3;
    
    /**
     * returned by reason() if everything was OK
     */
    public static int ERRORCODE_EVERYTHING_OK = 0;
    
    /**
     * Creates a New Instance of FileInterfaceWriter. <br>
     * This class is used to simplify the write-process of <br>
     * standard interface commands.
     * 
     * @param dst Where to white the File. It's the same Path as
     * specified in the main-config of simpleflex.
     */
    public FileInterfaceWriter(File dst){
        _dst = dst;
    }
    
    /**
     * Adds a Command that is needed to execute over FileInterface.
     * @param classPath The classpath to the needed fileinterface
     * @param args The arguments that 
     */
    public void addCommand(String classPath, String[][] args){
        StringBuilder sb = new StringBuilder();
        sb.append(_STARTTAG);
        sb.append(_newline);
        sb.append(_KEY_CLASSPATH);
        sb.append("=");
        sb.append(classPath);
        sb.append(_newline);
        if (args.length>0){
            for (int i=0;i<args[0].length;i++){
                sb.append(args[0][i]);
                sb.append("=");
                sb.append(args[1][i]);
                sb.append(_newline);
            }
        }
        sb.append(_ENDTAG);
        this._commands.add((sb.toString()));
    }
    
    /**
     * Writes the fileinterface file.
     * It waits until the file is deleted (which means,
     * That the File was defently readed by the Server) 
     * or till the timeout expires.
     * But there is no warrenty, that all tasks are processed!
     * To be shore, ask the logfile...
     * 
     * @return true, if the File was successful written and deleted.<br>
     * On false, errorReason() gives more details.
     */
    public boolean execute(){
        try{
            if (_dst.exists()){
                _reason = ERRORCODE_FILE_ALREADY_EXISTS;
                return false;
            }
            
            try (FileOutputStream fos = new FileOutputStream(_dst)) {
                for (int i=0;i<_commands.size();i++){
                    fos.write(((String)_commands.get(i)).getBytes());
                    fos.write(_newline.getBytes());
                }
                fos.flush();
            }
            long t=0;
            while(t<_timeout){
                if (!_dst.exists()){
                    _reason = ERRORCODE_EVERYTHING_OK;
                    return true;
                }
                try{Thread.sleep(50);}catch(InterruptedException ie){}
                t=t+50;
            }
            return false;
        }catch(IOException ioe){
            _reason = ERRORCODE_IOEXCEPTION;
            return false;
        }
    }
    
    /**
     *Returns the reason of returning false in calling execute() the last time.
     * @return 
     */
    public int errorReason(){
        return _reason;
    }
    
}


