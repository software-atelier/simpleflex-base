package ch.software_atelier.simpleflex;

import ch.software_atelier.simpleflex.conf.GlobalConfig;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ConnectionHandler extends Thread{
    static Logger LOG = LogManager.getLogger(ConnectionHandler.class);

    private final GlobalConfig _gc;
    private final WebAppHandler _webAppHandler;
    private boolean _listening = false;
    private boolean _isListening = false;

    public ConnectionHandler(GlobalConfig gc, 
            WebAppHandler webAppHandler){
        _gc = gc;
        _webAppHandler = webAppHandler;
    }

    public void stopListening(){
        _listening = false;
        while (_isListening)
            try{Thread.sleep(20);}catch(InterruptedException ie){}
    }
    
    @Override
    public void run(){
        _listening = true;
        _isListening = true;
        try{
            ServerSocket ssocket = getServerSocket(_gc);
            if (ssocket == null){
                _listening=false;
                _isListening = false;
            }
            
            while (_listening){
                try{
                    Socket socket = ssocket.accept();
                    RequestHandler requestHandler = new RequestHandler(_webAppHandler, socket, false);
                    requestHandler.start();
                }catch(SocketTimeoutException ie){
                    //ignore!
                }
            }
            if (_isListening && ssocket != null){
                ssocket.close();
                while(!ssocket.isClosed())
                    try{Thread.sleep(100);}catch(InterruptedException ie){}
                _isListening = false;
            }
            
        }catch(IOException ioe){
            LOG.error("",ioe);
        }
    }
    
    public abstract ServerSocket getServerSocket(GlobalConfig gc);
    
}
