package com.thinksns.sociax.t4.exception;

import org.apache.http.HttpException;

/**
 * 验证失败
 * 
 * @author wz
 * 
 */
public class VerifyErrorException extends HttpException {
	private static final long serialVersionUID = 1L;

	public VerifyErrorException() {
		// TODO Auto-generated constructor stub
	}

	public VerifyErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public VerifyErrorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
