package ch.software_atelier.simpleflex;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import java.io.IOException;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Opens a socket for HTTP-Communication.
 */
public class HTTPConnectionHandler extends ConnectionHandler{

    static Logger LOG = LogManager.getLogger(HTTPSConnectionHandler.class);
    
    /**
     * 
     * @param gc
     * @param webAppHandler 
     */
    public HTTPConnectionHandler(GlobalConfig gc,
                                 WebAppHandler webAppHandler){
        super(gc,webAppHandler);
        LOG.info("HTTP-PORT: "+gc.port());
    }

    /**
     * 
     * @param gc
     * @return 
     */
    @Override
    public ServerSocket getServerSocket(GlobalConfig gc) {
        ServerSocket ssocket = null;
        try{
            ssocket = new ServerSocket(gc.port(),9999);
            ssocket.setSoTimeout(1000);
        }catch(IOException e){
            LOG.error("Can't start socket on port "+gc.port(),e);
        }
        return ssocket;
    }
}
