package com.thinksns.sociax.thinksnsbase.exception;

public class ListAreEmptyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ListAreEmptyException() {
		super("暂无新数据");
	}

	public ListAreEmptyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ListAreEmptyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
