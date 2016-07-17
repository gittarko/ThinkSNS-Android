package com.thinksns.sociax.t4.android.function;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.model.ModelPhoto;

/**
 * 类说明： 图片列表/单张图片点击查看大图功能，需要输入Url的Array数组，以及当前context
 * 
 * @author wz
 * @date 2014-11-24
 * @version 1.0
 */
public class FunctionPhotoFullScreen extends FunctionSoicax {
	List<ModelPhoto> photoList;

	public FunctionPhotoFullScreen(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		this.context = context;
		photoList = new ArrayList<ModelPhoto>();
	}

	public FunctionPhotoFullScreen(Context context, ArrayList<ModelPhoto> photoList) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.photoList = photoList;
	}

	public void addPhoto(ModelPhoto photo) {
		photoList.add(photo);
	}

	/**
	 * 用户图片array
	 * 
	 * @param photoarray
	 */
	public void setPhotoList(JSONArray photoarray) {
		photoList.clear();
		for (int i = 0; i < photoarray.length(); i++) {
			ModelPhoto photo = new ModelPhoto();
			photo.setId(i);
			try {
				photo.setUrl(photoarray.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			photoList.add(photo);
		}
	}

	/**
	 * 点击Item
	 * 
	 * @param position
	 */
	public void clickAtPhoto(int position) {
		if (photoList.size() < position) {
			return;
		} else {
			Intent i = new Intent(context, ActivityViewPager.class);
			i.putExtra("index", position);
			i.putParcelableArrayListExtra("photolist",(ArrayList<? extends Parcelable>) photoList);
			context.startActivity(i);
		}
	}

	/**
	 * 根据用户信息设置图片列表
	 * 
	 * @param photoarray
	 */
	public void setUserInfoPhotoList(JSONArray photoarray) {
		photoList.clear();
		for (int i = 0; i < photoarray.length(); i++) {
			ModelPhoto p = new ModelPhoto();
			p.setId(i);
			try {
				String url = photoarray.getJSONObject(i).getString("image_url");
				p.setUrl(url);
				p.setMiddleUrl(url);
				p.setOriUrl(url);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			photoList.add(p);
		}

	}

	public void clear() {
		// TODO Auto-generated method stub
		photoList.clear();
	}

	@Override
	protected void initUiHandler() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initActivtyHandler() {
		// TODO Auto-generated method stub
		
	}

}
