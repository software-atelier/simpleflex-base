package ch.software_atelier.simpleflex;
import java.util.StringTokenizer;
import ch.software_atelier.simpleflex.apps.WebApp;
import java.util.HashMap;

public class WebAppHandler {
    
    private final HashMap<String,Domain> _domains;

    public WebAppHandler(HashMap<String,Domain> domains) {
        _domains = domains;
    }

    public WebApp getWebApp(Request request){
        String req = request.getReqestString();
        req = req.substring(1);
        StringTokenizer st = new StringTokenizer(req,"/");
        String key = null;
        if (st.hasMoreTokens())
            key = st.nextToken();

        String calledHost = request.getHost();
        
        Domain d = (_domains.get(calledHost));
        if (d==null){
            Domain dDef = (_domains.get("DEFAULT"));
            if (dDef == null)
                return null;
            else 
                d = dDef;
        }
        
        if (key == null)
            key = "";

        return d.getWebApp(key);
    }
}
