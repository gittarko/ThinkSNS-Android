package com.thinksns.sociax.db;

public abstract class SqlHelper {
	public static final int VERSION = 12;
	public static final String DB_NAME = "thinksns";

	public abstract void close();

	public int tranBoolean(boolean value) {
		if (value) {
			return 1;
		} else {
			return 0;
		}
	}
}
