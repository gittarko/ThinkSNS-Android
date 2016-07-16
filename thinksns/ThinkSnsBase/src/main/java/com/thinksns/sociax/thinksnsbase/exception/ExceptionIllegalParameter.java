package com.thinksns.sociax.thinksnsbase.exception;

/**
 * 类说明： 接口参数检查
 * 
 * @author wz
 * @date 2014-10-15
 * @version 1.0
 */
public class ExceptionIllegalParameter extends Exception {
	private static final long serialVersionUID = 1L;

	public ExceptionIllegalParameter() {
		super("接口参数无效，请检查请求参数");
	}

	public ExceptionIllegalParameter(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ExceptionIllegalParameter(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
