/*
 * WebAppConfig.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf;
import java.util.HashMap;

/**
 * This Class configures a class that implements the WebApp-interface,
 * @author tk
 */
public class WebAppConfig {

    private final String _classPath;
    private final String _name;
    private final HashMap<String,Object> _config = new HashMap<>();
    
    /**
     * 
     * Creates a new instance of WebAppConfig 
     * 
     * @param classPath the classpath to the class that implements the interface WebApp
     * @param name the name of the webApp. Under this name the Webapp is callable. Example:
     * If the name is myApp, the Webapp is accessable over the URL http://my.host/myApp
     */
    public WebAppConfig(String classPath, String name) {
        _classPath = classPath;
        _name = name;
    }
    
    /**
     * Returns the HashMap that holds the configuration-fields
     * @return 
     */
    public HashMap<String,Object> config(){
        return _config;
    }
    
    /**
     * returns the webApp name.
     * Under this name the Webapp is callable. Example:
     * If the name is myApp, the Webapp is accessable over the URL http://my.host/myApp
     * @return 
     */
    public String name(){
        return _name;
    }
    
    /**
     * Returns the classpath to the WebApp class.
     * @return 
     */
    public String classPath(){
        return _classPath;
    }
    
}
