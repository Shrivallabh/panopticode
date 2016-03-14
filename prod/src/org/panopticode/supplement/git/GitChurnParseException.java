package org.panopticode.supplement.git;

public class GitChurnParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6697162840113865751L;

	public GitChurnParseException() {
		super();
	}

	public GitChurnParseException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GitChurnParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public GitChurnParseException(String message) {
		super(message);
	}

	public GitChurnParseException(Throwable cause) {
		super(cause);
	}

	
}
