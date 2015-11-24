package uk.ac.ed.epcc.webapp.logging.debug;

public class FatalError extends Error {

	public FatalError() {
		
	}

	public FatalError(String message) {
		super(message);
		
	}

	public FatalError(Throwable cause) {
		super(cause);
	}

	public FatalError(String message, Throwable cause) {
		super(message, cause);
		
	}

}
