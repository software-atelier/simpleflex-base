/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.software_atelier.simpleflex.conf;

import ch.software_atelier.simpleflex.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author tk
 */
public class JSONConfigGenerator implements ConfigGenerator {
    
    static Logger LOG = LogManager.getLogger(JSONConfigGenerator.class);

    private GlobalConfig _globalConfig = new GlobalConfig();
    private List<DomainConfig> _domainConfigs = new ArrayList();
    
    /**
     * 
     * @param jsonFile 
     */
    public JSONConfigGenerator(File jsonFile){
        try{
            JSONObject obj = Utils.file2JSON(jsonFile);
            loadGlobalConfig(obj);
            loadDomains(obj);
            
        }catch(IOException | JSONException e){
            LOG.error("Error in configuration",e);
        }
    }
    
    
    private void loadGlobalConfig(JSONObject obj)throws JSONException{
    
        // Port
        int port = obj.optInt("port", 8080);
        _globalConfig.setPort(port);
        
        // FileInterface
        if (obj.optBoolean("file_interface", false)){
            _globalConfig.setUseFileInterface(true);
            _globalConfig.setFileInterfaceFile(
                    new File(obj.optString("file_interface_path","./fileInterface"))
            );
            _globalConfig.setFileInterfaceInterval(obj.optInt("file_interface_interval",60000));
        }
    }
    
    private void loadDomains(JSONObject obj)throws JSONException{
        JSONArray arr = obj.optJSONArray("domains");
        
        if (arr==null){
            System.err.println("No domains specified...");
            return;
        }
        
        // Domains
        for (int i=0;i<arr.length();i++){
            JSONObject jDomain = arr.optJSONObject(i);
            if (jDomain != null){
                // Name
                String name = jDomain.optString("name");
                if (name.length()>0){
                    DomainConfig dc = new DomainConfig(name);
                    
                    // DefaultWebApp
                    JSONObject jdefaultWebApp = jDomain.optJSONObject("default_app");
                    WebAppConfig dwac = loadWebAppConfig(jdefaultWebApp);
                    if (dwac!=null)
                        dc.setDefaultWebAppConfig(dwac);
                    
                    // Apps
                    JSONArray apps = jDomain.optJSONArray("apps");
                    if (apps!=null){
                        for (int ii=0;ii<apps.length();ii++){
                            JSONObject japp = apps.optJSONObject(ii);
                            if (japp!=null){
                                WebAppConfig wac = loadWebAppConfig(japp);
                                if (wac!=null){
                                    dc.appendWebApp(wac);
                                }
                            }
                        }
                    }
                    
                    _domainConfigs.add(dc);
                }
            }
        }
    }
    
    
    private WebAppConfig loadWebAppConfig(JSONObject obj){
        String cp = obj.optString("classpath");
        String name = obj.optString("name");
        if (cp.length()>0){
            WebAppConfig wac = new WebAppConfig(cp, name);
            JSONObject config = obj.optJSONObject("config");
            if (config!=null){
                this.loadConfigToHashMap(config, wac.config());
            }
            return wac;
        }
        return null;
    }
    
    private void loadConfigToHashMap(JSONObject config, HashMap<String,Object> ht){
        Set<String> keys = config.keySet();
        for (String key:keys){
            ht.put(key, config.opt(key));
        }
    }

    @Override
    public GlobalConfig globalConfig() {
        return _globalConfig;
    }

    @Override
    public List<DomainConfig> domainConfigs() {
        return _domainConfigs;
    }
    
}
