package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明：资讯分类
 * Created by Zoey on 2016-04-27.
 */
public class ModelInformationCate extends SociaxItem {

    private int id;
    private String name;
    private int isDel;
    private int rank;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public ModelInformationCate(JSONObject data) throws DataInvalidException {
        super(data);
        try {
            if (data.has("id")) this.setId(data.getInt("id"));
            if (data.has("name")) this.setName(data.getString("name"));
            if (data.has("isDel")) this.setIsDel(data.getInt("isDel"));
            if (data.has("rank")) this.setRank(data.getInt("rank"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ModelInformationCate() {
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
