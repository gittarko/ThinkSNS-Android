package com.thinksns.sociax.t4.model;

import android.text.TextUtils;

import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 类说明： 草稿箱
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-26
 */
public class ModelDraft extends SociaxItem {
    /**
     * 数据库自增的id
     */
    int id = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 微博内容
     */
    private String content;

    /**
     * 标题，常用于帖子的标题
     */
    private String title;

    /**
     * 是否有图片，默认为false，若为true，则imageList不为空
     */
    private boolean hasImage = false;
    /**
     * 是否有视频，默认为false，若为true，则videoPath不能为空
     */
    private boolean hasVideo = false;
    /**
     * 草稿编辑时间
     */
    private String ctime;
    /**
     * 图片地址列表集合
     */
    private ArrayList<String> imageList = new ArrayList<String>();
    /**
     * 视频地址
     */
    private String videoPath = "";
    /**
     * 草稿是否已经发布
     */
    private boolean isDraftSend = false;
    //转发微博或帖子时的ID
    private int feed_id;
    //频道ID
    private int channel_id;
    //频道名称
    private String channel_name;

    //草稿箱类型
    private int type;
    //带地理位置的微博或频道
    private String latitude, longitude;
    private String address;

    public String getTitle() {
        if(TextUtils.isEmpty(title))
            return "";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDraftSend() {
        return isDraftSend;
    }

    public void setDraftSend(boolean isDraftSend) {
        this.isDraftSend = isDraftSend;
    }

    public String getContent() {
        if(TextUtils.isEmpty(content))
            return "";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        if(this.imageList.size() > 0) {
            this.imageList.clear();
        }

        this.imageList = imageList;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getLatitude() {
        if(latitude == null)
            return "0";
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        if(latitude == null)
            return "0";
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(int feed_id) {
        this.feed_id = feed_id;
    }

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public ModelDraft() {
    }

    @Override
    public boolean checkValid() {
        return false;
    }

    @Override
    public String getUserface() {
        return null;
    }

    public String getImageListToString() {
        // TODO Auto-generated method stub
        String str = "";
        String middle = "";
        if (getImageList() != null) {
            for (int i = 0; i < getImageList().size(); i++) {
                middle += getImageList().get(i) + ",";
            }
            if (middle.contains(","))
                middle = middle.substring(0, middle.lastIndexOf(","));
        }
        str = middle;
        return str;
    }

    public void setImageList(String string) {
        String[] adds = string.split(",");
        this.imageList = new ArrayList<String>();
        try {
            for (int i = 0; i < adds.length; i++) {
                imageList.add(adds[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof ModelDraft))
            return false;
        return ((ModelDraft)o).getId() == this.getId();
    }
}
