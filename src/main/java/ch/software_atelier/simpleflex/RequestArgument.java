package ch.software_atelier.simpleflex;

public class RequestArgument {
    
    private final String _key;
    private final String _value;

    public RequestArgument(String key, String value) {
        _key = key;
        _value = value;
    }

    public String key(){
        return _key;
    }

    public String value(){
        return _value;
    }
            
}
