package com.thinksns.tschat.unit;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于SociaxUI的工具集合类
 * 
 * @author Povol
 * 
 */
public class SociaxUIUtils {

	private static final String TAG = "SociaxUIUtils";

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}
