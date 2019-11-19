package ch.software_atelier.simpleflex.interfaces.file;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import java.util.HashMap;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestInterface implements FileInterface{
    
    static Logger LOG = LogManager.getLogger(TestInterface.class);

    @Override
    public boolean process(SimpleFlexAccesser sfa, HashMap<String,Object> config){
        Set<String> keys = config.keySet();
        for (String key:keys){
            LOG.info(key+"="+config.get(key));
        }
        String proc = (String)config.get("PROC");
        
        if (proc==null)
            return false;
        else
            return (proc.equalsIgnoreCase("TRUE"));
    }
    
}
