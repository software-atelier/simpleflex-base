package ch.software_atelier.simpleflex;
import ch.software_atelier.simpleflex.apps.WebApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Domain {
    
    private final HashMap<String, WebApp> _webApps;
    private final String _name;
    private WebApp _defaultWebApp;

    public Domain(String name, HashMap<String,WebApp> webApps) {
        _name = name;
        _webApps = webApps;
    }

    public WebApp getDefaultWebApp(){
        return _defaultWebApp;
    }

    public void setDefaultWebApp(WebApp defaultWebApp){
        _defaultWebApp = defaultWebApp;
    }

    public WebApp getWebApp(String name){
        if (name==null)
            return _defaultWebApp;
        WebApp webApp = _webApps.get(name);
        if (webApp == null)
            return _defaultWebApp;
        else
            return webApp;
    }

    public String name(){
        return _name;
    }

    public List<WebApp> apps(){
        List<WebApp> webapps = new ArrayList<>();
        webapps.add(this._defaultWebApp);
        webapps.addAll(_webApps.values());
        return webapps;
    }
    
}
