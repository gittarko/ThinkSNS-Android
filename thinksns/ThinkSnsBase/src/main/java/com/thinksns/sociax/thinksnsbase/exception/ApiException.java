package com.thinksns.sociax.thinksnsbase.exception;

/**
 * 数据为空
 * 
 */
public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;
	private String messge = "";

	public ApiException() {
		super("暂时没有更多数据");
	}

	public ApiException(String message) {
		super(message);
		this.messge = message;
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getExceptionMessage() {
		String json = "{\"status\":0, \"msg\":\"" + messge + "\"}";
		return json;
	}

}
