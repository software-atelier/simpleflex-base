/*
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.software_atelier.simpleflex.docs.impl;

/**
 * 
 * @author tk
 *
 * This class simply redirects to the actual Folder,
 * if the request-string does not ends with a "/".
 */
public class FolderRedirectorDoc extends RedirectorDoc{

	/**
	 * Just Needs the Request-String
	 * @param reqStr
	 */
	public FolderRedirectorDoc(String reqStr){
		super(reqStr+"/");
	}
}
