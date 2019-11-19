package ch.software_atelier.simpleflex.apps;
import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.docs.WebDoc;

import java.util.HashMap;

public interface WebApp {
	
    public static final long UNLIMITED_UPLOAD = -1;
    public static final long NO_UPLOAD = 0;
    
    /**
     * This Method must process the Request from a WebBrowser
     * @param request An Object that holds all data of the request
     * @return A WebDoc that holds the Document to serve
     */
    public WebDoc process(Request request);

    /**
     * This Method can config this Object. It is called after initialisation before the
     * method process() is called the first time.
     * @param name The name of this webapp. If the WebApp is accessable over www.myserver.net/myApp, The name is myApp.
     * @param config The Configuration of this WebApp. The keys are the same like in the host's Config-File but without the $'s.
     * @param sfa An Accesser to internal Simpleflex functionality
     **/
    public void start(String name, HashMap<String,Object> config, SimpleFlexAccesser sfa);

    /**
     * This Method must return the limitation for data-posting in bytes.
     * Nedative values are meaning no limits.
     * @param requestedPath The path trying to send data to.
     * @return The maximum of Data send to.
     */
    public long maxPostingSize(String requestedPath);

    /**
     * Will be called before terminating the Server
     *
     */
    public void quit();
}
