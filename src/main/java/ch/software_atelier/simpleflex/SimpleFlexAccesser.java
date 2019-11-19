package ch.software_atelier.simpleflex;
import ch.software_atelier.simpleflex.apps.WebApp;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import ch.software_atelier.simpleflex.interfaces.file.FileInterfaceHandler;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SimpleFlexAccesser {
    
    private final GlobalConfig _gc;
    private HashMap<String,Domain> _domains;
    private final String _version;
    private final Date _startTime;
    private FileInterfaceHandler _fih = null;
    private HTTPConnectionHandler _http = null;
    private HTTPSConnectionHandler _https = null;
    boolean ready = false;

    public SimpleFlexAccesser( String version, Date startTime, GlobalConfig gc) {
        _gc = gc;
        _version = version;
        _startTime = startTime;
    }

    public void setDomains(HashMap<String,Domain> domains){
        _domains = domains;
    }

    void setHTTPConnectionHandler(HTTPConnectionHandler http){
        _http = http;
    }

    void setHTTPSConnectionHandler(HTTPSConnectionHandler https){
        _https = https;
    }

    public void stopListening(){
        if (_http!=null)
            _http.stopListening();
        if (_https!=null)
            _https.stopListening();
    }

    public Date startTime(){
        return _startTime;
    }

    public String version(){
        return _version;
    }

    public void setFileInterfaceHandler(FileInterfaceHandler fih){
        _fih = fih;
    }

    public boolean ready(){
        return ready;
    }

    public void quitWebApps()throws NotReadyException{
        if (!ready)
            throw new NotReadyException();
        
        Collection<Domain> domains = _domains.values();
        for (Domain d:domains){
            List<WebApp> apps = d.apps();
            for (WebApp wapp: apps)
                wapp.quit();
        }
    }

    public void quitFileInterface()throws NotReadyException{
        if (!ready)
            throw new NotReadyException();
        if (_fih != null)
            _fih.stop();
    }

    public String getDomain(WebApp wa)throws NotReadyException{
        if (!ready)
            throw new NotReadyException();
        Collection<Domain> domains = _domains.values();
        for (Domain d:domains){
            String domainName = d.name();
            List<WebApp> apps = d.apps();
            for (WebApp wapp: apps)
                if (wa.equals(wapp))
                    return domainName;
        }
        return null;
    }

    public void shutDown() throws NotReadyException{
        quitWebApps();
        stopListening();
        quitFileInterface();
    }
}
