package com.thinksns.sociax.t4.android.img;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一个图片对象
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable, Parcelable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
	
	public ImageItem() {
		// TODO Auto-generated constructor stub
	}
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(imageId);
		dest.writeString(thumbnailPath);
		dest.writeString(imagePath);
		dest.writeInt((isSelected==true) ? 1 : 0);
	}
	
	public ImageItem(Parcel source) {
		// TODO Auto-generated constructor stub
		this.imageId = source.readString();
		this.thumbnailPath = source.readString();
		this.imagePath = source.readString();
		this.isSelected = (source.readInt() == 1) ? true : false;
		
	}
	
	public static final Parcelable.Creator<ImageItem> CREATOR = new Creator<ImageItem>() {

		@Override
		public ImageItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ImageItem(source);
		}

		@Override
		public ImageItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ImageItem[size];
		}
	};
}
