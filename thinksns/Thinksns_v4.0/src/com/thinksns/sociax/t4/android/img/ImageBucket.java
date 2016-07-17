package com.thinksns.sociax.t4.android.img;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个目录的相册对象
 * 
 * @author Administrator
 * 
 */
public class ImageBucket  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4763936456427107255L;
	public int count = 0;
	public String bucketName;
	public ArrayList<ImageItem> imageList;

}
