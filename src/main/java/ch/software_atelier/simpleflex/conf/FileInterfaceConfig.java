/*
 * FileInterfaceConfig.java
 *
 * Created on 17. Dezember 2007, 19:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf;

/**
 *
 * @author tk
 */
public class FileInterfaceConfig {
    private int _interval = 1000;
    private String _interfaceFile = "interface";
    
    /**
     * 
     * @param interval 
     */
    public void setInterval(int interval){
        _interval = interval;
    }
    
    /**
     * 
     * @return 
     */
    public int interval(){
        return _interval;
    }
    
    /**
     * 
     * @param path 
     */
    public void setInterfaceFile(String path){
        _interfaceFile = path;
    }
    
    /**
     * 
     * @return 
     */
    public String interfaceFile(){
        return _interfaceFile;
    }
}
