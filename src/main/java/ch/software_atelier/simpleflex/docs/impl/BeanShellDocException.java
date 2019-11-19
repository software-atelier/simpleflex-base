package ch.software_atelier.simpleflex.docs.impl;

public class BeanShellDocException extends Exception{
    
    public static int EXCEPTION_FILE_IO = -10;
    public static int EXCEPTION_INVALIDE_PART = -20;
    
    private int _partPos = -1;
    
    BeanShellDocException(Throwable th, int partPos){
        _partPos = partPos;
    }
    
    /**
     * Returns the PartPosition that has thrown this Exception.
     * Example: if it returns 3 there is somthing wrong in the
     * third BeanShell part.
     * @return
     */
    public int getPartPos(){
        return _partPos;
    }
}
