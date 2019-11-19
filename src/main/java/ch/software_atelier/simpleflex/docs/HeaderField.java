package ch.software_atelier.simpleflex.docs;

public class HeaderField {
    private String _name = null;
    private String _value = null;

    public HeaderField(String name, String value) {
        _name = name;
        _value = value;
    }

    public HeaderField(String name){
        _name = name;
        _value = "null";
    }

    public String name(){
        return _name;
    }

    public String value(){
        return _value;
    }

    public void setValue(String value){
        _value = value;
    }
    
}
