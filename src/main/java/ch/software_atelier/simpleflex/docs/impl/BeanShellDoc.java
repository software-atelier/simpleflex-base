package ch.software_atelier.simpleflex.docs.impl;
import java.util.ArrayList;
import bsh.Interpreter;
import bsh.EvalError;
import ch.software_atelier.simpleflex.Request;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.docs.HeaderField;
import ch.software_atelier.simpleflex.docs.WebDoc;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BeanShellDoc extends WebDoc{
    private static File _src;
    private static final String _BSH_SEPERATOR_START = "<!bsh";
    private static final String _BSH_SEPERATOR_END = "!>";
    private final Interpreter _interpr = new Interpreter();
    private String _mime = "text/html";
    private String _name = "";
    private final ArrayList _codes = new ArrayList();
    private final ArrayList _staticContent;
    private byte[] _data;

    public BeanShellDoc(File  file) throws BeanShellDocException{
    this._staticContent = new ArrayList();
        _name = file.getName();
        _src = file;
        genParts(file);
    }

    public void genDoc(Request request, SimpleFlexAccesser sfa){
        try{
            int httpCode = getHttpCode().code;
            String httpMsg = getHttpCode().message;
            _interpr.set("sf_httpCode",httpCode);
            _interpr.set("sf_httpMsg",httpMsg);
            _interpr.set("sf_headerKeys",new ArrayList());
            _interpr.set("sf_headerValues",new ArrayList());
            _interpr.set("sf_src",_src);
            _interpr.set("sf_sfa", sfa);
            _interpr.set("sf_request", request);
            _interpr.set("sf_mime", _mime);
            _interpr.eval("sf_returnValue = new StringBuffer();\n" +
                    "public void out(String value){\n" +
                    "sf_returnValue.append(value+\"\\n\");\n" +
                    "}\n");
            _interpr.eval("public void header(String key, String value){" +
                    "sf_headerKeys.add(key);" +
                    "sf_headerValues.add(value);" +
                    "}");
            _interpr.eval("public void code(int code, String msg){" +
                    "sf_httpCode=code;" +
                    "sf_httpMsg=msg;" +
                    "}");
            _interpr.eval("public void mime(String mime){" +
                    "sf_mime=mime;" +
                    "}");
            StringBuffer doc = new StringBuffer();
            doc.append((String)(_staticContent.get(0)));

            for (int i=0;i<_codes.size();i++){
                doc.append(
                    exec((String)(_codes.get(i))));
                doc.append((String)(_staticContent.get(i+1)));
            }

            httpCode = (Integer)_interpr.get("sf_httpCode");
            httpMsg = (String)_interpr.get("sf_httpMsg");
            setHTTPCode(httpCode, httpMsg);
            _mime = (String)_interpr.get("sf_mime");
            ArrayList headerKeys = (ArrayList)_interpr.get("sf_headerKeys");
            ArrayList headerValues = (ArrayList)_interpr.get("sf_headerValues");
            for (int i=0;i<headerKeys.size();i++){
                this.getHeaders().add(new HeaderField((String)headerKeys.get(i),
                        (String)headerValues.get(i)));
            }
            _data = doc.toString().getBytes();
        }catch(EvalError ee){
            _data = new ErrorDoc(ee.getMessage()).byteData();
        }catch(Exception e){
            _data = new ErrorDoc(e.getMessage()).byteData();
        }
    }

    private String exec(String code){

        StringBuilder sb = new StringBuilder();
        try{

            _interpr.eval(code);
            Object ret = _interpr.get("sf_returnValue");
            _interpr.eval("sf_returnValue = new StringBuffer();");
            return ret.toString();

        }catch(Throwable ioe){
            return "Exception: "+ioe.getMessage();
        }
    }

    private void genParts(File file)throws BeanShellDocException{
        try{
            FileInputStream fis = new FileInputStream(file);
            ByteBuffer bb = ByteBuffer.allocate((int)file.length());
            int i;
            while ((i=fis.read())!=-1)
                bb.put((byte)i);
            String docAsString = new String(bb.array());

            String[] splitedByStartExpression = docAsString.split(
                _BSH_SEPERATOR_START);
            _staticContent.add(splitedByStartExpression[0]);

            for (i=1;i<splitedByStartExpression.length;i++){
                String[] splited = splitedByStartExpression[i].split(
                    _BSH_SEPERATOR_END);
                if (splited.length!=2)
                    throw new BeanShellDocException(new Exception(),
                        BeanShellDocException.EXCEPTION_INVALIDE_PART);
                else{

                    _staticContent.add(splited[1]);
                   // Interpreter interp = testBscCode(splited[0],_staticContent.size());
                    _codes.add(splited[0]);
                }
            }
        }catch(IOException ioe){
            throw new BeanShellDocException(ioe,
                    BeanShellDocException.EXCEPTION_FILE_IO);
        }

    }

    private Interpreter testBscCode(String code, int pos)throws BeanShellDocException{
        Interpreter i = new Interpreter();
        try{

            Object o = i.eval(code);

        }catch(bsh.EvalError bsee){
            throw new BeanShellDocException(bsee,pos);
        }
        return i;
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
    public void close(){

    }
}
