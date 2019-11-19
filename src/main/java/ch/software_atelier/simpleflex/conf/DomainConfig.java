/*
 * DomainConfig.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf;
import java.util.ArrayList;

/**
 * This Class configures a host class
 * @author tk
 */
public class DomainConfig {
    
    private final String _name;
    private final ArrayList _webAppCs = new ArrayList();
    private WebAppConfig _defWebAppConf = null;

    /**
     * 
     * Creates a new instance of DomainConfig
     * 
     * @param name the name of this domain. Example: www.myhost.org
     */
    public DomainConfig(String name) {
        _name = name;
    }
    
    /**
     * Appends a WebAppConfig to this DomainConfig
     * @param webAppC
     */
    public void appendWebApp(WebAppConfig webAppC){
       _webAppCs.add(webAppC);
    }
    
    /**
     * Returns all WebAppConfigs for this DomainConfig
     * @return 
     */
    public WebAppConfig[] webAppConfigs(){
        WebAppConfig[] webAppCs = new WebAppConfig[_webAppCs.size()];
        webAppCs = (WebAppConfig[])_webAppCs.toArray(webAppCs);
        return webAppCs;
    }
    
    /**
     * returns the name of this domain.
     * @return 
     */
    public String name(){
        return _name;
    }
    
    /**
     * 
     * @param wac 
     */
    public void setDefaultWebAppConfig(WebAppConfig wac){
        _defWebAppConf = wac;
    }
    
    /**
     * 
     * @return 
     */
    public WebAppConfig defaultWebApp(){
        return _defWebAppConf;
    }
    
}
