package karanika.meallab.exception;

import java.io.IOException;

// Custom exception for catching errors on the MealDb API

public class MealLabException extends IOException {
	
	//Serialization version ID to prevent InvalidClassException
	
	private static final long serialVersionUID = 1L;    
	
	private final int statusCode;
	
//----------------------------Constructor with a message and status code----------------------------
	
	public MealLabException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	    }
	
	// It returns the HTTp status code
	
	public int getStatusCode() {
		return statusCode;
	}
	
	@Override
	public String toString() {
		if (statusCode == 0) {
			return "MealLabException: " + getMessage();
		}
		return "MealLabException: " + getMessage() + " (HTTP " + statusCode + ")";
	}
	

}
