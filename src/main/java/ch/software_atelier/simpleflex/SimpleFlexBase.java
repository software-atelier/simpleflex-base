package ch.software_atelier.simpleflex;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.conf.ConfigGenerator;
import ch.software_atelier.simpleflex.conf.DomainConfig;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import ch.software_atelier.simpleflex.conf.JSONConfigGenerator;
import ch.software_atelier.simpleflex.conf.WebAppConfig;
import ch.software_atelier.simpleflex.conf.text.SimpleFlexConfigGenerator;
import ch.software_atelier.simpleflex.interfaces.file.FileInterfaceHandler;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This is the Main-Class of the SmpleFles base Application.
 */
public class SimpleFlexBase{
    
    static Logger LOG = LogManager.getLogger(SimpleFlexBase.class);
    
    /** The Version-String of this Server */
    public static final String SERVER_VERS = "SimpleFlex base V2.1";
    private final HashMap<String,Domain> _domains;
    /** The GlobalConfiguratin-Object */
    private final GlobalConfig _globalConfig;
    /** The Starttime of this Server */
    public final Date START_TIME = new Date();
    
    private SimpleFlexAccesser _simpleFlexAccesser = null;

    private final WebAppHandler _webAppHandler;

    public SimpleFlexAccesser sfa(){
        return _simpleFlexAccesser;
    }

    public SimpleFlexBase(GlobalConfig gc, DomainConfig dc){
        this._domains = new HashMap<>();
        logBanner();
        _simpleFlexAccesser = new SimpleFlexAccesser(SERVER_VERS,START_TIME,gc);
        _globalConfig = gc;
        loadLibs();
        Domain domain = instanciateDomain(dc);
        _domains.put(dc.name(),domain);
        _webAppHandler = new WebAppHandler(_domains);
        _simpleFlexAccesser.setDomains(_domains);
        _simpleFlexAccesser.ready = true;
    }

    public SimpleFlexBase(GlobalConfig gc,
            List<DomainConfig> dcs){
        this._domains = new HashMap<>();
        logBanner();
        _simpleFlexAccesser = new SimpleFlexAccesser(SERVER_VERS,START_TIME,gc);
        
        _globalConfig = gc;
        loadLibs();
        
        //Adds the domains
        for (DomainConfig dc : dcs){
            Domain domain = instanciateDomain(dc);
            if (domain!=null)
                _domains.put(dc.name(),domain);
        }
        
        if (gc.useFileInterface()){
            FileInterfaceHandler fih = new FileInterfaceHandler(
                    gc.fileInterfaceFile(),
                    gc.fileInterfaceInterval(),
                    _simpleFlexAccesser);
            fih.start();
            _simpleFlexAccesser.setFileInterfaceHandler(fih);
        }
        
        _webAppHandler = new WebAppHandler(_domains);
        _simpleFlexAccesser.setDomains(_domains);
        _simpleFlexAccesser.ready = true;
    }
    
    private void logBanner(){
        LOG.info(" __ _                 _       __ _           ");
        LOG.info("/ _(_)_ __ ___  _ __ | | ___ / _| | _____  __");
        LOG.info("\\ \\| | '_ ` _ \\| '_ \\| |/ _ \\ |_| |/ _ \\ \\/ /");
        LOG.info("_\\ \\ | | | | | | |_) | |  __/  _| |  __/>  < ");
        LOG.info("\\__/_|_| |_| |_| .__/|_|\\___|_| |_|\\___/_/\\_\\");
        LOG.info("               |_|                           ");
        LOG.info(SERVER_VERS);
    }
    
    /**
     * Serves the given App as default App on localhost
     * @param classPath
     * @param config
     * @param port
     * @return
     */
    public static SimpleFlexAccesser serveOnLocalhost(String classPath, HashMap<String,Object> config, int port){
        return serveOnLocalhost(classPath, config, port, "");
    }
    
    public static SimpleFlexAccesser serveOnLocalhost(String classPath, HashMap<String,Object> config, int port, String path){
        return serveOnHost("localhost",classPath,config,port,path);
    }

    public static SimpleFlexAccesser serveOnHost(String host, String classPath, HashMap<String,Object> config, int port, String path){

        GlobalConfig gc = new GlobalConfig();
        gc.setFileInterfaceFile(new File("interface"));
        gc.setFileInterfaceInterval(1000);
        List<DomainConfig> dcs = new ArrayList();

        gc.setPort(port);
        DomainConfig dc = new DomainConfig(host);
        Set<String> keys = config.keySet();

        WebAppConfig wac = new WebAppConfig(classPath, "app");
        for (String key:keys){
            wac.config().put(key, config.get(key));
        }
        if (path.isEmpty()){
            dc.setDefaultWebAppConfig(wac);
        }
        else{
            dc.appendWebApp(wac);
        }
        dcs.add(dc);

        SimpleFlexBase sfb = new SimpleFlexBase(gc, dcs);
        sfb.start();
        return sfb.getSimpleFlexAccesser();
    }
    
    public void start(){
        HTTPConnectionHandler http = new HTTPConnectionHandler(_globalConfig, _webAppHandler);
        http.start();
        _simpleFlexAccesser.setHTTPConnectionHandler(http);

        if (_globalConfig.useSSL()){
            HTTPSConnectionHandler https = new HTTPSConnectionHandler(_globalConfig, _webAppHandler);
            https.start();
            _simpleFlexAccesser.setHTTPSConnectionHandler(https);
        }
        LOG.info("SimpleFlex base started");
    }
    
    private void loadLibs(){
        
        File pluginsFolder = new File("sf.plugins");
        if (pluginsFolder.exists() && pluginsFolder.isDirectory()){
            File[] files = pluginsFolder.listFiles();
            
            for (File f:files){
                if (f.isFile() && !f.isHidden()){
                    try{
                        LOG.info("loading "+f.getPath());
                        ClassPathLoader.addFile(f.getPath());
                    }catch(IOException ioe){
                        LOG.error("faild to load Library "+f.getPath()+":"+ioe.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * The Main-Method
     * @param args
     */
    public static void main(String[] args){
        
        
        if (args.length>0){
            if (args[0].equalsIgnoreCase("SIMPLE")){
                simple();
            }
            else
                terminal();
        }
        else
            terminal();
    }

    private static void simple(){
        //GlobalConfig gc, StartupOutputNotifiable son, DomainConfig dc
        GlobalConfig gc = new GlobalConfig();
        gc.setPort(8080);
        DomainConfig dc = new DomainConfig("localhost");

        WebAppConfig wac = new WebAppConfig("ch.hom3.simpleflex.apps.defaultapp.DefaultApp","DEFAULT");
        wac.config().put("$DOCPATH", "shares/localhost");
        wac.config().put("$BEANSHELL", "off");
        wac.config().put("$LUA", "off");
        dc.setDefaultWebAppConfig(wac);

        SimpleFlexBase sfb = new SimpleFlexBase(gc,dc);
        sfb.start();
    }

    private static void terminal(){
        
        
        ConfigGenerator gc = null;
        
        
        File jsonConfig = new File("config.json");
        File mainConfig = new File("simpleflex.conf");

        
        if (jsonConfig.exists() && jsonConfig.isFile()){
            gc = new JSONConfigGenerator(jsonConfig);
        }
        else if (mainConfig.exists() && mainConfig.isFile()){
            gc = new SimpleFlexConfigGenerator(mainConfig);
        }
        else{
            System.out.println("There is no configuration-file!");
            System.exit(1);
        }
        
        new SimpleFlexBase(
            gc.globalConfig(),
            gc.domainConfigs()
        ).start();
    }

    /**
     * instanciates a WebApp by a WebAppConfig
     */
    private WebApp instanciateWebApp(WebAppConfig wac, String domainName){
        try{
            Class waClass = Class.forName(wac.classPath());
            Object waObject = waClass.newInstance();
            WebApp wa = (WebApp)waObject;
            return wa;
        }catch(Throwable th){
            LOG.error("can't instanciate Webapp "+wac.classPath()+" on "+domainName+"/"+wac.name()+": "+th.toString());
            return null;
        }
    }
    
    
    /**
     * Instanciates a Domain-Object by a DomainConfig
     */
    private Domain instanciateDomain(DomainConfig dc){
        HashMap<String,WebApp> webAppsHash = new HashMap<>();
        WebAppConfig[] webAppConfigs = dc.webAppConfigs();
        
        for(int i=0;i<webAppConfigs.length;i++){
            WebApp wa = instanciateWebApp(webAppConfigs[i],dc.name());
            if (wa != null){
                LOG.info("Adding "+webAppConfigs[i].name()+" to "+dc.name());
                webAppsHash.put(webAppConfigs[i].name(),wa);
                wa.start(webAppConfigs[i].name(),webAppConfigs[i].config(),_simpleFlexAccesser);
            }
            
        }
        
        if (dc.defaultWebApp() == null){
            LOG.error("No DefaultWebApp specified for "+dc.name()+".");
            LOG.error("Ignoring this host...");
            return null;
        }
        WebApp defaultWebApp = instanciateWebApp(dc.defaultWebApp(),dc.name());

        defaultWebApp.start("",dc.defaultWebApp().config(),_simpleFlexAccesser);
        
        Domain domain = new Domain(dc.name(), webAppsHash);
        domain.setDefaultWebApp(defaultWebApp);        

        return domain;
    }
    
    public SimpleFlexAccesser getSimpleFlexAccesser(){
        return _simpleFlexAccesser;
    }
}
