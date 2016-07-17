package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 类说明：
 * 
 * @author Administrator
 * @date 2014-11-9
 * @version 1.0
 */
public class ModelUserTagandVerify extends SociaxItem {
	String title;
	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	ArrayList<Child> child;

	public ArrayList<Child> getChild() {
		return child;
	}

	public void setChild(ArrayList<Child> child) {
		this.child = child;
	}

	public ModelUserTagandVerify(JSONObject data) {
		try {
			if (data.has("verify_id"))
				this.setId(data.getString("verify_id"));
			if (data.has("title"))
				this.setTitle(data.getString("title"));
			child = new ArrayList<ModelUserTagandVerify.Child>();
			for (int i = 0; i < data.getJSONArray("child").length(); i++) {
				child.add(new Child(data.getJSONArray("child").getJSONObject(i)));

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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

	public class Child {
		String id, title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		Child(JSONObject data) {
			try {
				if (data.has("id")) {
					this.setId(data.getString("id"));
				} else if (data.has("verify_id")) {
					this.setId(data.getString("verify_id"));
				}
				this.setTitle(data.getString("title"));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

}
