package ch.software_atelier.simpleflex.docs.impl;

/**
 * This Exception is used in the class FileDoc and is thrown if the given File does not exist.
 */
public class FileDocException extends Exception {
	public FileDocException(String message){
		super(message);
	}
}
