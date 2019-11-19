/*
 * SimpleFlexConfigGenerator.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf.text;

import ch.software_atelier.simpleflex.conf.ConfigGenerator;
import ch.software_atelier.simpleflex.conf.DomainConfig;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import ch.software_atelier.simpleflex.conf.WebAppConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * this Class generates the config objects that are used to configure SimpleFlex
 * @author tk
 */
public class SimpleFlexConfigGenerator implements SyntaxErrorNotifiable, ConfigGenerator{
    private static final String GC_DEFLOGPATH = "DEFLOGPATH";
    private static final String GC_PORT = "PORT";
    private static final String GC_SSLPORT = "SSLPORT";
    private static final String GC_SSLKEYSTORE = "SSLKEYSTORE";
    private static final String GC_SSLKEYSTOREPASSWORD = "SSLKEYSTOREPASSWORD";
    private static final String GC_USESSL = "USESSL";
    private static final String GC_USESECUTITYMANAGER = "SECURITYMANAGER";
    private static final String GC_USEFILEINTERFACE = "FILEINTERFACE";
    
    private static final String DOMAIN_NAME = "NAME";
    private static final String DOMAIN_DOCPATH = "DOCPATH";
    private static final String DOMAIN_CONFIG = "CONFIG";
    
    private static final String LOG_CLASSPATH = "CLASSPATH";
    
    private static final String WEBAPP_CLASSPATH = "CLASSPATH";
    private static final String WEBAPP_NAME = "NAME";
    
    
    private static final String FILEINTERFACE_FILE = "FILE";
    private static final String FILEINTERFACE_INTERVAL = "INTERVAL";
    
    private static final String ELEM_WEBAPP = "WebApp";
    private static final String ELEM_DEFWEBAPP = "DefaultWebApp";
    private static final String ELEM_DOMAIN = "Domain";
    private static final String ELEM_GLOBAL = "Global";
    private static final String ELEM_FILEINTERFACE = "FileInterface";
    private static final String ELEM_LIB = "Library";
    
    private GlobalConfig _globalConfig = new GlobalConfig();
    private ArrayList _domainConfigs = new ArrayList();
    
    /** 
     * Creates a new instance of SimpleFlexConfigGenerator
     * @param mainconfig The file that holds the main configuration
     */
    public SimpleFlexConfigGenerator(File mainconfig) {
        try{
            ConfigFileIO mainConfigIO = new ConfigFileIO(mainconfig,this);
            mainConfigIO.read();
            ConfigElement[] cElems = mainConfigIO.configElements();
            
            int i = 0;
            while (i<cElems.length){
                
                if (cElems[i].elementName().equals(ELEM_GLOBAL)){
                    handleGlobalConfig(cElems[i]);
                }
                else if (cElems[i].elementName().equals(ELEM_DOMAIN)){
                    handleDomainConfig(cElems[i]);
                }
                else if (cElems[i].elementName().equals(ELEM_FILEINTERFACE)){
                    this.handleFileInterfaceConfig(cElems[i]);
                }
                else{
                    syntaxError("Unsupported Element: <"+cElems[i].elementName()+">", false);
                }
                i++;
            }
                   
        }catch(IOException ioe){
            syntaxError("Error on reading the main config-files: "+ioe.toString(),true);
        }
    }
    
    /**
     * Returns a filled ClobalConfig that is used to configure SimpleFlex
     */
    @Override
    public GlobalConfig globalConfig(){
        return _globalConfig;
    }
    
    /**
     * Returns an array of DomainConfig-Objects who are used to configure SimpleFlex
     */
    @Override
    public List<DomainConfig> domainConfigs(){
        return _domainConfigs;
    }
    
    /**
     * Implementation of the SyntaxErrorNotifiable-interface.
     * Called on Syntax-Errors in the configuration file.
     * @param message description if the syntac error
     * @param fatal quits the application if it is true.
     */
    @Override
    public void syntaxError(String message, boolean fatal){
        if (fatal){
            System.out.println("Fatal error in config:");
            System.out.println(message);
            System.exit(1);
        }
        else{
            System.out.println("Error in config:");
            System.out.println(message);
            System.out.println("SimpleFlex may not work as expected");
        }
    }
    
    /**
     * Apends FileInterface-Configuration to the GlobalConfig
     */
    private void handleFileInterfaceConfig(ConfigElement fileInterfaceConfig){
        String[] interval = fileInterfaceConfig.getValuesByKey(FILEINTERFACE_INTERVAL);
        String[] file = fileInterfaceConfig.getValuesByKey(FILEINTERFACE_FILE);
        if (interval.length==1){
            try{
                int interv = Integer.parseInt(interval[0]);
                _globalConfig.setFileInterfaceInterval(interv);
            }catch(NumberFormatException nfe){
                syntaxError(FILEINTERFACE_INTERVAL+"="+interval[0]+" > invalide value",false);
            }
        }
        else if (interval.length>1){
            syntaxError("More Then one "+FILEINTERFACE_INTERVAL+" key/value-pairs. All ignored!", false);
        }
        
        if (file.length==1){
            File interfaceFile = checkFile(file[0]);
            if (file!=null){
                interfaceFile.delete();
                _globalConfig.setFileInterfaceFile(interfaceFile);
            }   
        }
        else if (file.length>1)
            syntaxError("More Then one "+FILEINTERFACE_FILE+" key/value-pairs. All ignored!", false);
        
    }
    
    
    /**
     * Fills the GlobalConfig object by a ConfigElement
     */
    private void handleGlobalConfig(ConfigElement globalConfigElem){
        
        String[] port = globalConfigElem.getValuesByKey(GC_PORT);
        String[] sslPort = globalConfigElem.getValuesByKey(GC_SSLPORT);
        String[] useSSL = globalConfigElem.getValuesByKey(GC_USESSL);
        String[] sslKeyStore = globalConfigElem.getValuesByKey(GC_SSLKEYSTORE);
        String[] sslPassword = globalConfigElem.getValuesByKey(GC_SSLKEYSTOREPASSWORD);
        String[] useSecManager = globalConfigElem.getValuesByKey(GC_USESECUTITYMANAGER);
        String[] useFileInterface = globalConfigElem.getValuesByKey(GC_USEFILEINTERFACE);
        
        //ADDING PORT
        if (checkForValidCount(port,GC_PORT)){
            int portI = positiveIntByString(port[0]);
            if (portI != -1)
                _globalConfig.setPort(portI);
        }
        
        //ADDING SSLPORT
        if (checkForValidCount(sslPort,GC_SSLPORT)){
            int sslportI = positiveIntByString(sslPort[0]);
            if (sslportI != -1)
                _globalConfig.setSSLPort(sslportI);
        }        
        
        //ADDING USESSL
        if (checkForValidCount(useSSL,GC_USESSL)){
            _globalConfig.setUseSSL(useSSL[0].equalsIgnoreCase("yes"));
        }
        
        if (checkForValidCount(useFileInterface,GC_USEFILEINTERFACE)){
            _globalConfig.setUseFileInterface(useFileInterface[0].equalsIgnoreCase("yes"));
        }
        
        //ADDING SSLKEYSTORE
        if (checkForValidCount(sslKeyStore, GC_SSLKEYSTORE)){
            _globalConfig.setSSLKeyStore(sslKeyStore[0]);
        }
        
        //ADDING SSLKEYSTOREPASSWORD
        if (checkForValidCount(sslPassword, GC_SSLKEYSTOREPASSWORD)){
            _globalConfig.setSSLKeyStorePassword(sslPassword[0]);
        }
        
        //ADDING USESECUTITYMANAGER
        if (checkForValidCount(useSecManager,GC_USESECUTITYMANAGER)){
            _globalConfig.setUseSecurityManager(useSecManager[0].equalsIgnoreCase("YES"));
        }
        
    }
    
    
    private WebAppConfig handleWebAppConfig(ConfigElement webAppConfigElem){
        String[] classPath = webAppConfigElem.getValuesByKey(WEBAPP_CLASSPATH);
        String[] name = webAppConfigElem.getValuesByKey(WEBAPP_NAME);
        if (checkForValidCount(classPath,WEBAPP_CLASSPATH)&&checkForValidCount(name,WEBAPP_NAME)){
            WebAppConfig waC = new WebAppConfig(classPath[0], name[0]);
            String[][] waCList = webAppConfigElem.getValuesAndKeysByRegexOfKey("^\\$.*");
            int i = 0;
            while(i<waCList[0].length){
                waC.config().put(waCList[0][i],waCList[1][i]);
                i++;
            }
            return waC;
                
        }
        else
            return null;
    }
    
    /**
     * Fills a DomainConfig element and adds it to the DomainConfig list
     */
    private void handleDomainConfig(ConfigElement domainConfigElem){
        DomainConfig domainConfig;
        String[] nameA = domainConfigElem.getValuesByKey(DOMAIN_NAME);
        String[] configA = domainConfigElem.getValuesByKey(DOMAIN_CONFIG);
        String name = null;
        File config = null;
        
        if (checkForValidCount(nameA,DOMAIN_NAME)){
            name = nameA[0];
        }
        
        if (checkForValidCount(configA,DOMAIN_CONFIG)){
            if ((config=checkFile(configA[0]))==null)
                syntaxError("no valid file on "+DOMAIN_CONFIG, false);
        }
        
        if ((name!=null)||(config !=null)){
            domainConfig = new DomainConfig(name);
            ConfigFileIO configFileIO = new ConfigFileIO(config,this);
            try{
                configFileIO.read();
                ConfigElement[] configElements = configFileIO.configElements();
                int i = 0;
                while (i<configElements.length){
                    if (configElements[i].elementName().equals(ELEM_WEBAPP)){
                        WebAppConfig wac = handleWebAppConfig(configElements[i]);
                        if (wac !=null)
                            domainConfig.appendWebApp(wac);
                        else
                            syntaxError("cant read WebApp-configuration of domain "+name+" - WebApp ignored", false);
                    }
                    else if (configElements[i].elementName().equals(ELEM_DEFWEBAPP)){
                        WebAppConfig wac = handleWebAppConfig(configElements[i]);
                        if (wac !=null)
                            domainConfig.setDefaultWebAppConfig(wac);
                        else
                            syntaxError("cant read Default WebApp-configuration of domain "+name+" - WebApp ignored", false);
                    }
                    
                    i++;
                }
                _domainConfigs.add(domainConfig);
            }catch(IOException ioe){
                 syntaxError("cant read configuration of host "+name+" - host ignored", false);
            }
        }
        else
            syntaxError("no valid <Host> element! - ignored", false);
        
    }
    
    /**
     * This method calls the method syntaxError(..) with a generated message that holds
     * the given key. It calls the named method with a fatalError as false.
     */
    private void invalideKeyValue(String key){
        syntaxError("invalide: "+key+", using default value", false);
    }
    
    /**
     * Creates a File-Object by a String.
     * Returns null if the file is not accessable, no file or no valid string.
     */
    private File checkFile(String fileStr){
        try{
            File file = new File(fileStr);
            if (file.exists()){
                if ((file.canWrite())&&(file.canRead())&&(file.isFile()))
                    return file;
                    else
                        return null;
            }
            else if (file.createNewFile()){
                return file;
            }
            else
                return null;
            
        }catch(IOException ioe){
           return null;
        }
    }
    
    
    
    /**
     * Returns the given String, representing a decimal number greater than 0, as an
     * int. If the given String is no valid integer or smaller than 1, it returns -1.
     */
    private int positiveIntByString(String integer){
        try{
            Integer i = new Integer(integer);
            if (i.intValue()>0)
                return i.intValue();
            else
                return -1;
        }catch(NumberFormatException nfe){
            return -1;
        }
    }
    
    /**
     * This Method checks the given String array for a length of 1.
     * If it is not 1, it returns false
     * If there are more than one Strings in the array, it
     * calls the method invalidekeyValue(...).
     */
    private boolean checkForValidCount(String[] values, String key){
        if (values.length==1)
            return true;
        else if (values.length==0)
            return false;
        else{
            invalideKeyValue(key);
            return false;
        }     
    }
}
