/*
 * ConfigElement.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf.text;

import java.util.ArrayList;

/**
 *
 * @author tk
 */
public class ConfigElement {
    private final String _elemName;
    private final ArrayList _keys = new ArrayList();
    private final ArrayList _values = new ArrayList();
    
    /** 
     * Creates a new instance of ConfigElement
     * @param elemName 
     */
    public ConfigElement(String elemName) {
        _elemName = elemName;
    }
    
    /**
     * Appends a key and a refered value to this ConfigElement-Object
     * @param key The key to append
     * @param value The value to append
     * @throws ConfigElementException if key or value is null.
     */
    public void appendValue(String key, String value)throws ConfigElementException{
        if ((key==null)||(value==null))
            throw new ConfigElementException(key,value);
        _keys.add(key);
        _values.add(value);
    }
    
    /**
     * Returns the values refered to the given key.
     * @param key
     * @return the values refered to the key.
     */
    public String[] getValuesByKey(String key){
        ArrayList valuesAL = new ArrayList();
        int i = 0;
        while(i<_keys.size()){
            if (((String)(_keys.get(i))).equals(key))
                valuesAL.add(_values.get(i));
            i++;
        }
        String[] values = new String[valuesAL.size()];
        values = (String[])valuesAL.toArray(values);
        return values;
    }
    
    /**
     * Returns a table of keys and values by a Regular expression applyed to the key.
     * String[0][x] are keys and String[1][x] are values.
     * @param regexOfKey The regular expressin that has to be tested to the key
     * @return A String-Table that holds the matched keys and the refered values.
     */
    public String[][] getValuesAndKeysByRegexOfKey(String regexOfKey){
        ArrayList keys = new ArrayList();
        ArrayList values = new ArrayList();
        int i = 0;
        while(i<_keys.size()){
            if (((String)(_keys.get(i))).matches(regexOfKey)){
                keys.add(_keys.get(i));
                values.add(_values.get(i));
            }
            i++;
        }
        String[][] table = new String[2][keys.size()];
        i = 0;
        while (i<keys.size()){
            table[0][i] = (String)keys.get(i);
            table[1][i] = (String)values.get(i);
            i++;
        }
        return table;
    }
    
    /**
     * Returns the Name of this element.
     * @return the Name of this element as a String
     */
    public String elementName(){
        return _elemName;
    }
}
