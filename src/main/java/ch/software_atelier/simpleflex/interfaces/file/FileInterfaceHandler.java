package ch.software_atelier.simpleflex.interfaces.file;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.conf.text.ConfigElement;
import ch.software_atelier.simpleflex.conf.text.ConfigFileIO;
import ch.software_atelier.simpleflex.conf.text.SyntaxErrorNotifiable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileInterfaceHandler {
    private final File _interfaceFile;
    private final int _interval;
    private boolean _running = false;
    private final InterfaceFileListener _interfceFileListener;
    private final SimpleFlexAccesser _sfa;
    
    private static final String CLASSPATH = "CLASSPATH";
    
    static Logger LOG = LogManager.getLogger(FileInterfaceHandler.class);

    public FileInterfaceHandler(File interfaceFile, int interval, SimpleFlexAccesser sfa) {
        _sfa = sfa;
        _interfceFileListener = new InterfaceFileListener();
        _interfaceFile = interfaceFile;
        _interval = interval;
        if (_interfaceFile.exists())
            _interfaceFile.delete();
        
        
    }

    public void start(){
        if (!_running){
            _running = true;
            _interfceFileListener.start();
        }
    }

    public void stop(){
        _running = false;
    }
    
    private class InterfaceFileListener extends Thread implements SyntaxErrorNotifiable{
        private boolean _syntaxError = false;
        
        public void run(){
            while (_running){
                if (_interfaceFile.exists()){
                    ConfigElement[] configs = readFile();
                    if (!_syntaxError){
                        if (configs!=null){
                            for (int i=0;i<configs.length;i++){
                                String[] classnames = configs[i].getValuesByKey(CLASSPATH);
                                if (classnames.length == 1){
                                    FileInterface fi = 
                                            fileInterfaceByClassname(classnames[0]);
                                    if (fi!=null){
                                        String[][] configStrs = configs[i].getValuesAndKeysByRegexOfKey(".*");
                                        HashMap hash = new HashMap();
                                        for (int ii=0; ii<configStrs[0].length; ii++){
                                            hash.put(configStrs[0][ii],configStrs[1][ii]);
                                        }
                                        boolean proc = fi.process(_sfa,hash);
                                        if (!proc){
                                            onProcFalse();
                                        }
                                    }
                                    else
                                        onInvalideClasspath();
                                    
                                }
                                else
                                    onInvalideClasspath();
                            }
                        }
                        else
                            onSyntaxError();
                    }
                    else 
                        onSyntaxError();
                    
                    _interfaceFile.delete();
                    _syntaxError = false;
                }
                   
                try{Thread.sleep(_interval);}catch(InterruptedException ie){}
            }
        }
        
        /**
         * Returns the Config-Elements. If something is wrong, it returns null.
         */
        private ConfigElement[] readFile(){
            try{
                ConfigFileIO cfIO = new ConfigFileIO(_interfaceFile,this);
                cfIO.read();
                if (!_syntaxError)
                    return cfIO.configElements();
                else
                    return null;
            }catch(IOException ioe){
                _syntaxError = true;
                return null;
            }
            
        }
        
        private FileInterface fileInterfaceByClassname(String classname){
            try{
                Class fiClass = Class.forName(classname);
                Object fiObject = fiClass.newInstance();
                FileInterface fi = (FileInterface)fiObject;
                return fi;
            }catch(Throwable th){
                LOG.error("Error on instanciate a FileInterface. Please check classpath.",th);
                return null;
                
            }
        }
        
        public void syntaxError(String message, boolean fatal){
            _syntaxError = true;
            LOG.error(message);
        }
        
        private void onSyntaxError(){  
            LOG.error("SyntaxError on reding on FileInterface");
        }
        
        private void onProcFalse(){
            LOG.error("FileInterface PlugIn not processing failed");
        }
    
        private void onInvalideClasspath(){
            LOG.error("Invalid CLASSPATH-Configuration");
        }
        
        private void onProcSucceeded(String classp){
            LOG.info("process() sucessful > "+classp);
        }
        
        private void inProcUnSucceeded(String classp){
            LOG.info("process() failed > "+classp);
        }
    }
}
