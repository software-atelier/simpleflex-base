package ch.software_atelier.simpleflex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author tk
 */
public class UntilNewlineReader {

    private final InputStream _is;
    private final byte[] _buffer;
    private int _inBufferStart = -1;
    private int _inBufferSize = -1;
    
    public UntilNewlineReader(InputStream is, int buffersize){
        _is = is;
        _buffer = new byte[buffersize];
    }
    
    public byte[] read()throws IOException{
        try(ByteArrayOutputStream os = new ByteArrayOutputStream();){
            
            if (_inBufferStart>=0 && _inBufferSize >= 0){
                if( writeUntilNewline(os, _inBufferStart, _inBufferSize) ){
                    // found a newline in buffer
                    return os.toByteArray();
                }
                else{
                    _inBufferStart = -1;
                    _inBufferSize = -1;
                }
            }
            
            
            int length = 0;
            while ( (length=_is.read(_buffer))!=-1){
                if( writeUntilNewline(os, 0, length) ){
                    // found a newline in buffer
                    return os.toByteArray();
                }
            }
            
            return os.toByteArray();
        }
    }
    
    private boolean writeUntilNewline(OutputStream os, int from, int size) throws IOException{
        if (size==0){
            return false;
        }
        int i;
        boolean newlineFound = false;
        for (i=from;i<from+size;i++){
            if ((char)_buffer[i]=='\n'){
                newlineFound = true;
                _inBufferStart = i+1;
                _inBufferSize = from + size -_inBufferStart;
                break;
            }
        }
        int to = i-from+1;
        if (!newlineFound)
            to = size;
        os.write(_buffer, from, to);
        
        return newlineFound;
    }

}
