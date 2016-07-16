package com.thinksns.sociax.thinksnsbase.exception;

/**
 * 参数错误
 * 
 * @author wz
 * 
 */
public class DataInvalidException extends Exception {
	private static final long serialVersionUID = 1L;

	public DataInvalidException() {
		super("数据无效");
	}

	public DataInvalidException(String message) {
		super(message);
	}

	public DataInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

}
