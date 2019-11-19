package ch.software_atelier.simpleflex.docs.impl;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.InputStream;
/**
 * This is a simple WebDoc-Class for sending a byte-Array.
 * It is a document-container-class that holds your generated data.
 */
public class ByteDoc extends WebDoc{

    private final byte[] _data;
    private final String _mime;
    private final String _name;

    /**
     * @param data The data, that must be sent
     * @param name The Name of the file
     * @param mime The mimeType of the File
     */
    public ByteDoc(byte[] data, String name, String mime){
            _data = new byte[data.length];
            for (int i=0;i<data.length;i++)
               _data[i]=data[i]; 

            _mime = mime;
            _name = name;
    }

    @Override
    public long size(){
        return _data.length;
    }

    @Override
    public String mime(){
            return _mime;
    }

    @Override
    public String name(){
            return _name;
    }

    @Override
    public byte[] byteData(){
            return _data;
    }

    @Override
    public InputStream streamData(){
            return null;
    }

    @Override
    public String dataType(){
            return DATA_BYTE;
    }

    @Override
    public void close(){}
        
}
