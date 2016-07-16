package com.thinksns.sociax.thinksnsbase.exception;

public class UserDataInvalidException extends DataInvalidException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserDataInvalidException() {
	}

	public UserDataInvalidException(String message) {
		super(message);
	}

	public UserDataInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

}
