package ch.software_atelier.simpleflex.interfaces.file;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import java.util.HashMap;

public interface FileInterface {

    public boolean process(SimpleFlexAccesser sfa, HashMap<String,Object> config);
}
