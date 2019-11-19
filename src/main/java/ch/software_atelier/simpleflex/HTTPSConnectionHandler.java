package ch.software_atelier.simpleflex;
import ch.software_atelier.simpleflex.conf.GlobalConfig;
import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ssl.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Opens a socket for HTTPS-Communication.
 */
public class HTTPSConnectionHandler extends ConnectionHandler{

    static Logger LOG = LogManager.getLogger(HTTPSConnectionHandler.class);
    
    /**
     * 
     * @param gc
     * @param webAppHandler 
     */
    public HTTPSConnectionHandler(GlobalConfig gc,
                                  WebAppHandler webAppHandler){
        super(gc,webAppHandler);

        LOG.info("HTTPS-PORT: "+gc.sslPort());

        System.getProperties().put("javax.net.ssl.keyStore",
            gc.sslKeyStore());
        System.getProperties().put("javax.net.ssl.keyStorePassword",
            gc.sslKeystorePassword());
    }
    
    /**
     * 
     * @param gc
     * @return 
     */
    @Override
    public ServerSocket getServerSocket(GlobalConfig gc){
        SSLServerSocket ssocket = null;
        try{
            SSLServerSocketFactory sslSF = 
                (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            ssocket = (SSLServerSocket)sslSF.createServerSocket(gc.sslPort());
            ssocket.setSoTimeout(1000);
        }catch(IOException e){

            LOG.error("Can't start socket on port "+gc.sslPort(), e);

        }
        return ssocket;
    }
    
}
