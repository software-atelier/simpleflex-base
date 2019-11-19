/*
 * SyntaxErrorNotifiable.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ch.software_atelier.simpleflex.conf.text;

/**
 * This Class is used to notifie a class that implements this interface about
 * a syntax error in the config file.
 * @author tk
 */
public interface SyntaxErrorNotifiable {
    
    /**
     * Notifies the implementing class about a Syntax-Error in the config file.
     * @param message A message that describes the syntax error
     * @param fatal
     */
    public void syntaxError(String message, boolean fatal);
    
}
