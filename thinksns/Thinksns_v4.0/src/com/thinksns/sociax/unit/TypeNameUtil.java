package com.thinksns.sociax.unit;

import com.thinksns.sociax.android.R;

/**
 * 
 * @author Povol
 * 
 */
public class TypeNameUtil {

	public static int getDomLoadImg(String dType) {

		dType = dType.substring(dType.lastIndexOf('.') + 1, dType.length());

		if (dType.equals("pdf")) {
			return R.drawable.pdf_48;
		} else if (dType.equals("doc") || dType.equals("docx")) {
			return R.drawable.word_48;

		} else if (dType.equals("xls") || dType.equals("xlsx")) {
			return R.drawable.excel_48;
		} else if (dType.equals("ppt") || dType.equals("pptx")) {
			return R.drawable.ppt_48;
		} else if (dType.equals("png")) {
			return R.drawable.png_48;

		} else if (dType.equals("jpg")) {
			return R.drawable.jpg_48;

		} else if (dType.equals("gif")) {
			return R.drawable.gif_48;

		} else if (dType.equals("txt")) {
			return R.drawable.txt_48;
		} else if (dType.equals("zip") || dType.equals("rar")) {
			return R.drawable.zip_48;

		} else {
			return R.drawable.attach;
		}
	}
}
