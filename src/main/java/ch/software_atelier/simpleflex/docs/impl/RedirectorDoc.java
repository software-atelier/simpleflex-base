/*
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.software_atelier.simpleflex.docs.impl;

import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.InputStream;

/**
 * This WebDoc serves a html-file that redirects to the given url.
 */
public class RedirectorDoc extends WebDoc{
	
    private final String _url;
    private static final String _docContent1 =
                    "<meta http-equiv=\"refresh\" content=\"0; URL=";
    private static final String _docContent2 =
                    "\">";

    /**
     * @param url The URL to redirect to.
     */
    public RedirectorDoc(String url){
            _url = url;
    }

    @Override
    public long size(){
            return _url.length() + _docContent1.length() + _docContent2.length();
    }

    @Override
    public String mime(){
            return "text/html";
    }

    @Override
    public String name(){
            return "redirect.htm";
    }

    @Override
    public byte[] byteData(){
            return (_docContent1+_url+_docContent2).getBytes();
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
