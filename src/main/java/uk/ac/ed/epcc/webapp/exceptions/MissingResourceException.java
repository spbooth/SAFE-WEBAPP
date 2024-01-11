package uk.ac.ed.epcc.webapp.exceptions;

import java.io.IOException;

public class MissingResourceException extends IOException {

	public MissingResourceException() {
		
	}

	public MissingResourceException(String message) {
		super(message);
		
	}

	public MissingResourceException(Throwable cause) {
		super(cause);
		
	}

	public MissingResourceException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
