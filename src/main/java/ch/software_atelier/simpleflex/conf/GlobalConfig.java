/*
 * GlobalConfig.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf;
import java.io.File;

/**
 * This Class holds all global configuration values
 * @author tk
 */
public class GlobalConfig {
    private int _port = 80;
    private int _sslPort = 443;
    private boolean _useSSL = false;
    private String _sslKeyStore = null;
    private String _sslPassword = null;
    private boolean _useSecurityManager = false;
    private boolean _useFileInterface = false;
    private int _fileInterfaceInterval = 5*1000; //5 sec.
    private File _interfaceFile = new File("./interface");
    
    /**
     * Returns the port for unsecure connections.
     * @return 
     */
    public int port(){
        return _port;
    }
    
    /**
     * Sets the port for unsecure connections.
     * The default value is 80
     * @param port
     */
    public void setPort(int port){
        _port = port;
    }
    
    /**
     * returns the port for secure connections.
     * @return 
     */
    public int sslPort(){
        return _sslPort;
    }
    
    /**
     * Sets the port for secure connections. 
     * The default value is 443
     * @param sslPort
     */
    public void setSSLPort(int sslPort){
        _sslPort = sslPort;
    }
    
    /**
     * returns true if ssl is activated, else false.
     * @return 
     */
    public boolean useSSL(){
        return _useSSL;
    }
    
    /**
     * Activates(true)/deactivates(false) secure connections.
     * The default value is false/deactivated
     * @param useSSL
     */
    public void setUseSSL(boolean useSSL){
        _useSSL = useSSL;
    }
    

    /**
     * returns the path to the keystore for secure connections.
     * For details refere to the manualpages of keytool.
     * @return 
     */
    public String sslKeyStore(){
        return _sslKeyStore;
    }
    
     /**
     * Sets the path to the keystore for secure connections.
     * For details refere to the manualpages of keytool.
     * Default value is null.
     * @param sslKeyStore
     */
    public void setSSLKeyStore(String sslKeyStore){
        _sslKeyStore = sslKeyStore;
    }
    
    /**
     * returns the password for the keystore for secure connections.
     * For details refere to the manualpages of keytool.
     * @return 
     */
    public String sslKeystorePassword(){
        return _sslPassword;
    }
    
     /**
     * Sets the password for the keystore for secure connections.
     * For details refere to the manualpages of keytool.
     * The default value is null.
     * @param sslPassword
     */
    public void setSSLKeyStorePassword(String sslPassword){
        _sslPassword = sslPassword;
    }
    
    /**
     * If the builtin SecurityManager has to be used,
     * enable it with true.
     * @param use
     */
    public void setUseSecurityManager(boolean use){
        _useSecurityManager = use;
    }
    
    /**
     * Returns true, if the builtIn SecuritsManager has to be used.
     * @return 
     */
    public boolean useSecurityManager(){
        return _useSecurityManager;
    }
    
    /**
     * 
     * @param use 
     */
    public void setUseFileInterface(boolean use){
        _useFileInterface = use;
    }
    
    /**
     * 
     * @return 
     */
    public boolean useFileInterface(){
        return _useFileInterface;
    }
    
    /**
     * 
     * @param fileInterfaceInterval 
     */
    public void setFileInterfaceInterval(int fileInterfaceInterval){
        _fileInterfaceInterval = fileInterfaceInterval;
    }
    
    /**
     * 
     * @return 
     */
    public int fileInterfaceInterval(){
        return _fileInterfaceInterval;
    }
    
    /**
     * 
     * @param interfaceFile 
     */
    public void setFileInterfaceFile(File interfaceFile){
        _interfaceFile = interfaceFile;
    }
    
    /**
     * 
     * @return 
     */
    public File fileInterfaceFile(){
        return _interfaceFile;
    }
    
}
