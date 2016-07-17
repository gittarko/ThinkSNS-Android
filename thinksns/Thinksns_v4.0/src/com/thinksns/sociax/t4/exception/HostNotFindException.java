package com.thinksns.sociax.t4.exception;

import org.apache.http.HttpException;

public class HostNotFindException extends HttpException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HostNotFindException() {
		// TODO Auto-generated constructor stub
	}

	public HostNotFindException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public HostNotFindException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
