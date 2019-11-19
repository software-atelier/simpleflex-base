/*
 * ConfigFileIO.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf.text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author tk
 */
public class ConfigFileIO {
    private final ArrayList _configElements = new ArrayList();
    private final File _configFile;
    private final SyntaxErrorNotifiable _syntaxErrorNotifiable;
    
    /**
     * Creates a new instance of ConfigFileIO
     * @param configFile
     * @param sen
     */
    public ConfigFileIO(File configFile, SyntaxErrorNotifiable sen) {
        _configFile = configFile;
        _syntaxErrorNotifiable = sen; 
    }
    
    /**
     * 
     * @param ce 
     */
    public void apendConfigElement(ConfigElement ce){
        _configElements.add(ce);
    }
    
    /**
     * 
     * @return 
     */
    public ConfigElement[] configElements(){
        ConfigElement[] elements = new ConfigElement[_configElements.size()];
        elements = (ConfigElement[])(_configElements.toArray(elements));
        return elements;
    }
    
    /**
     * 
     * @throws IOException 
     */
    public void read() throws IOException{
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                new FileInputStream(_configFile))); 

        String line = "";
        while((line = br.readLine())!=null){
            line = line.trim();
            if (line.length()>2){
                if (line.startsWith("<")&&line.endsWith(">")){
                    String elementName = line.substring(1,line.length()-1);
                    apendConfigElement(readInConfigElement(elementName,br));
                }
                else{ /* ignore, outside of an Element. Maybe comment ;)*/}
            }
            else{ /* ignore, outside of an Element. Maybe plane CR ;)*/}
        }
    }
    
    private ConfigElement readInConfigElement(String elemName, BufferedReader br)
        throws IOException{
        
        ConfigElement ce = new ConfigElement(elemName);

        String line;
        while ((line = br.readLine())!=null){
            line = line.trim();
            if (line.equals("</"+elemName+">"))
                return ce;
            else if (!line.equals("")){
                String[] keyNValue = tokenizeKeyValue(line);
                if ((keyNValue[0]!=null)&&(keyNValue[1]!=null)){
                    if ((!keyNValue[0].equals(""))&&(!keyNValue[1].equals(""))){
                        try{
                            ce.appendValue(keyNValue[0],keyNValue[1]);
                        }catch(ConfigElementException cee){
                            //cant't throw!
                            //already testet ot null!
                        }
                    }
                    else
                        _syntaxErrorNotifiable.syntaxError("Invalide key/value-pair: "+line+" - ignored",false);
                }
                else
                    _syntaxErrorNotifiable.syntaxError("Invalide key/value-pair: "+line+" - ignored",false);
            }
        }
        _syntaxErrorNotifiable.syntaxError("Unclosed element! - "+line,true);
        return null;
    }
    
    private String[] tokenizeKeyValue(String line){
        String[] keyNValue = new String[]{null,null};
        StringTokenizer st = new StringTokenizer(line,"=");
        if (st.hasMoreTokens())
            keyNValue[0] = st.nextToken();
        if (st.hasMoreTokens())
            keyNValue[1] = st.nextToken();
        if (st.hasMoreTokens()){
            _syntaxErrorNotifiable.syntaxError("Invalide key/value-pair: "+line+" - ignored",false);
            return new String[]{null,null};
        }
        return keyNValue;    
    }
    
    /**
     * 
     * @throws IOException 
     */
    public void write() throws IOException{
        try(
                BufferedOutputStream bfos = new BufferedOutputStream(
                new FileOutputStream(_configFile));
            ){
            int i = 0;
            while (i<_configElements.size()){
                writeConfigElement((ConfigElement)_configElements.get(i),bfos);
                i++;
            }
        }
    }
    
    private void writeConfigElement(ConfigElement ce, BufferedOutputStream bos) throws IOException{
        bos.write(("<"+ce.elementName()+">\n").getBytes());
        
        //get all keys and values
        String[][] keyValueList = ce.getValuesAndKeysByRegexOfKey(".*");
        int i = 0;
        while (i<keyValueList[0].length){
            bos.write((keyValueList[0][i]+"="+keyValueList[1][i]+"\n").getBytes());
            i++;
        }
        bos.write(("</"+ce.elementName()+">\n").getBytes());
    }
}
