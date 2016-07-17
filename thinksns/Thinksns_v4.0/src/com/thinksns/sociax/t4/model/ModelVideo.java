package com.thinksns.sociax.t4.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;


/**
 * 类说明： 视频信息类
 * 
 * @author lhz
 * @date 2014-9-10
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ModelVideo extends SociaxItem implements Serializable {
	private int id;// 视频id
	private String videoImgUrl;// 预览图
	private String videoPart;// 列表预览路径
	private String videoDetail;// 视频详情
	private int videoWidth;// 宽
	private int videoHeight;// 高
	private String feed_id;//对应的微博id
	private String host;//视频来自网站,
	private int video_id=0;//如果有值，则是站内视频，没有字段则表示网络视频

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public ModelVideo(JSONObject jo) throws JSONException {
		if(jo == null) {
			Log.v("weiboString", "json is null");
			return;
		}
		
		if (jo.has("feed_id")) {
			this.setFeed_id(jo.getString("feed_id"));
		}
		if (jo.has("video_id")) {
			setId(jo.getInt("video_id"));
		}
		if (jo.has("flashimg")) {
			setVideoImgUrl(jo.getString("flashimg"));
		}
		if (jo.has("flashvar")) {
			setVideoDetail(jo.getString("flashvar"));
		}
		//拍的视频
		if (jo.has("flashvar_part")) {
			setVideoPart(jo.getString("flashvar_part"));
		}
		//第三方网站的视频
		else if(jo.has("source")){
			setVideoPart(jo.getString("source"));
		}
		//视频来源
		if(jo.has("host")){
			this.setHost(jo.getString("host"));
		}
		if (jo.has("flash_width")) {
			setVideoWidth(jo.getInt("flash_width"));
		}
		if (jo.has("flash_height")) {
			setVideoHeight(jo.getInt("flash_height"));
		}

		if (jo.has("video_id")) {
			setVideo_id(jo.getInt("video_id"));
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVideoImgUrl() {
		return videoImgUrl;
	}

	public void setVideoImgUrl(String videoImgUrl) {
		this.videoImgUrl = videoImgUrl;
	}

	public String getVideoPart() {
		return videoPart;
	}

	public void setVideoPart(String videoPart) {
		this.videoPart = videoPart;
	}

	public String getVideoDetail() {
		return videoDetail;
	}

	public void setVideoDetail(String videoDetail) {
		this.videoDetail = videoDetail;
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}
	public String getFeed_id() {
		return feed_id;
	}

	public void setFeed_id(String feed_id) {
		this.feed_id = feed_id;
	}

	public int getVideo_id() {
		return video_id;
	}

	public void setVideo_id(int video_id) {
		this.video_id = video_id;
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}

}
