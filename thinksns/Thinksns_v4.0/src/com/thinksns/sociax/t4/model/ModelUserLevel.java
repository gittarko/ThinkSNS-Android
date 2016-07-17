package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 类说明： 等级信息
 * 
 * @author wz
 * @date 2015-1-24
 * @version 1.0
 */
public class ModelUserLevel extends SociaxItem {
	/**
	 * 等级
	 */
	int level;
	/**
	 * 等级名称
	 */
	String name;
	/**
	 * 等级图片名称，根据这个名称可以获取本地资源
	 */
	String imagename;
	/**
	 * 等级经验起始
	 */
	String start;
	/**
	 * 等级经验结束
	 */
	String end;
	/**
	 * 等级类型
	 */
	String level_type;
	/**
	 * 下一个等级需要的经验
	 */
	String nextNeed;
	/**
	 * 下一个等级名称
	 */
	String nextName;
	/**
	 * 服务端等级图片地址
	 */
	String src;

	public ModelUserLevel(JSONObject data) {
		try {
			if (data.has("level")) {
				this.setLevel(data.getInt("level"));
			}
			if (data.has("name")) {
				this.setName(data.getString("name"));
			}
			if (data.has("image")) {
				this.setImagename(data.getString("image"));
			}
			if (data.has("start")) {
				this.setStart(data.getString("start"));
			}
			if (data.has("end")) {
				this.setEnd(data.getString("end"));
			}
			if (data.has("level_type")) {
				this.setLevel_type(data.getString("level_type"));
			}
			if (data.has("nextNeed")) {
				this.setNextNeed(data.getString("nextNeed"));
			}
			if (data.has("nextName")) {
				this.setNextName(data.getString("nextName"));
			}
			if (data.has("src")) {
				this.setSrc(data.getString("src"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取等级
	 * 
	 * @return int 1-10级
	 */
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 获取等级名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取等级对应的本地资源的图片名称
	 * 
	 * @return
	 */
	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * 等级类型
	 */
	public String getLevel_type() {
		return level_type;
	}

	public void setLevel_type(String level_type) {
		this.level_type = level_type;
	}

	public String getNextNeed() {
		return nextNeed;
	}

	public void setNextNeed(String nextNeed) {
		this.nextNeed = nextNeed;
	}

	public String getNextName() {
		return nextName;
	}

	public void setNextName(String nextName) {
		this.nextName = nextName;
	}

	/**
	 * 等级对应图片在服务器端的地址
	 * 
	 * @return
	 */
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
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
