package edu.missouristate.taschedulegenerator.algorithm;

/**
 * Exception that represents no TA being available to assist an activity.
 * 
 * @author Noah Geren
 *
 */
public class NoTAAvailableException extends Exception {

	private static final long serialVersionUID = 194981776967820285L;
	
	/**
	 * Create a new NoTAAvailableException.
	 * 
	 * @param message Used as the exception message.
	 */
	public NoTAAvailableException(String message) {
		super(message);
	}

}
