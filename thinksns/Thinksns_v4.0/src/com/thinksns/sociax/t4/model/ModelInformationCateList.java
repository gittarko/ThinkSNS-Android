package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明：资讯列表
 * Created by Zoey on 2016-04-27.
 */
public class ModelInformationCateList extends SociaxItem {

    private int id;
    private int cid;
    private String subject;
    private String abstracts;
    private int author;
    private String ctime;
    private int hits;
    private String url;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ModelInformationCateList(JSONObject data) throws DataInvalidException {
        super(data);
        try {
            if (data.has("id")) this.setId(data.getInt("id"));
            if (data.has("cid")) this.setCid(data.getInt("cid"));
            if (data.has("subject")) this.setSubject(data.getString("subject"));
            if (data.has("abstract")) this.setAbstracts(data.getString("abstract"));
            if (data.has("author")) this.setAuthor(data.getInt("author"));
            if (data.has("ctime")) this.setCtime(data.getString("ctime"));
            if (data.has("hits")) this.setHits(data.getInt("hits"));
            if (data.has("url")) this.setUrl(data.getString("url"));
            if (data.has("image")) this.setImage(data.getString("image"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public ModelInformationCateList() {
        super();
    }

    @Override
    public boolean checkValid() {
        return false;
    }

    @Override
    public String getUserface() {
        return null;
    }
}
