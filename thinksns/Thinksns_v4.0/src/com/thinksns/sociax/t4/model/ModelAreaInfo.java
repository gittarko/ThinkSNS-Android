package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.t4.android.function.FunctionPingYing;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 类说明： 城市的信息
 *
 * @author ZhiShi
 * @version 1.0
 * @date 2014-9-28
 */
@SuppressWarnings("serial")
public class ModelAreaInfo extends ModelCityInfo implements Serializable {

    private String pid;

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    private String area_id;

    public ModelAreaInfo(JSONObject data) {
        try {
            if (data.has("area_id"))
                this.setArea_id(data.getString("area_id"));
            String city_name = data.getString("title");
            this.setName(city_name);
            this.setSortLetters(FunctionPingYing.sortFirstLetters(city_name.replaceAll("　", "")));
            this.setName_pinyin(FunctionPingYing.getPingYingString(city_name.replaceAll("　", "")));
            if (data.has("pid"))
                setPid(data.getString("pid"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

}
