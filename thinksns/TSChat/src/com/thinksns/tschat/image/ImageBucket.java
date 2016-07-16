package com.thinksns.tschat.image;

import java.io.Serializable;
import java.util.ArrayList;

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
