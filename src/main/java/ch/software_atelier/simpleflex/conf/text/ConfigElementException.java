/*
 * ConfigElementException.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf.text;

/**
 *
 * @author tk
 */
public class ConfigElementException extends Exception{
    private final String _key;
    private final String _value;
    
    /** 
     * Creates a new instance of ConfigElementException 
     * @param key
     * @param value
     */
    public ConfigElementException(String key, String value) {
        _key = key;
        _value = value;
    }
    
    public String key(){
        return _key;
    }
    
    public String value(){
        return _value;
    }
    
}
