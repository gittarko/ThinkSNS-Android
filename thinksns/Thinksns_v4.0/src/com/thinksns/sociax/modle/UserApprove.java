package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 类说明：用户认证标签
 * wz
 */
public class UserApprove extends SociaxItem {
	/**
	 * 认证列表
	 */
	private List<String> approves;

	public UserApprove() {
	}
	/**
	 * 将用户组标签 jsonobject转换成对应的认证标签列表
	 * @param jsonObject user JSONObject
	 */
	public UserApprove(JSONObject jsonObject) {
		approves = new ArrayList<String>();
		try {
			if ((!jsonObject.has("user_group"))||jsonObject.getString("user_group").equals("[]"))
				return;
			if (jsonObject.get("user_group") instanceof JSONArray) {
				JSONArray ja = jsonObject.getJSONArray("user_group");
				for (int i = 0; i < ja.length(); i++) {
					approves.add(ja.getString(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}
	/**
	 * 获取认证列表
	 * @return List<String> 认证列表
	 */
	public List<String> getApprove() {
		return approves;
	}
	/**
	 * 设置认证列表
	 * @param approves
	 */
	public void setApprove(List<String> approves) {
		this.approves = approves;
	}

}
