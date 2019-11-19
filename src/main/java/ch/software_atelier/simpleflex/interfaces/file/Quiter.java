package ch.software_atelier.simpleflex.interfaces.file;
import ch.software_atelier.simpleflex.SimpleFlexAccesser;
import ch.software_atelier.simpleflex.NotReadyException;
import java.util.HashMap;

public class Quiter implements FileInterface{

    @Override
    public boolean process(SimpleFlexAccesser sfa, HashMap<String,Object> config){
        try{
            if (sfa.ready()){
                sfa.quitWebApps();
                sfa.stopListening();
                sfa.quitFileInterface();
                return true;
            }
            else
                return false;
            
        }catch(NotReadyException nre){
            return false;
        }
    }
}
