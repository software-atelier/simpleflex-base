package ch.software_atelier.simpleflex;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.Marker;

class LogMarker {
    static final Marker CONNECTION = MarkerManager.getMarker("SF_CONNECTION");
    static final Marker RESPONSE = MarkerManager.getMarker("SF_RESPONSE").setParents(CONNECTION);
    static final Marker REQUEST = MarkerManager.getMarker("SF_REQUEST").setParents(CONNECTION);

    static Marker responseMarker(String hostname){
        return MarkerManager.getMarker(hostname).setParents(RESPONSE);
    }

    static Marker requestMarker(String hostname){
        return MarkerManager.getMarker(hostname).setParents(REQUEST);
    }
}
