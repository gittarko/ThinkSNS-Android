package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 类说明： 任务类型
 * 
 * @author wz
 * @date 2014-11-25
 * @version 1.0
 */
public class ModelTaskType extends SociaxItem {
	String title, task_type, task_level;
	List<ModelTask> task_List;
	String receive,
	status;//该任务类型的状态，0是未完成，1是已完成但是未领取奖励，2是已领取奖励

	public String getReceive() {
		return receive;
	}

	public void setReceive(String receive) {
		this.receive = receive;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ModelTaskType(JSONObject data) {
		try {
			if (data.has("title")) {
				this.setTitle(data.getString("title"));
			}
			if (data.has("task_type")) {
				this.setTask_type(data.getString("task_type"));
			}
			if (data.has("task_level")) {
				this.setTask_level(data.getString("task_level"));
			}
			if(data.has("receive")){
				this.setReceive(data.getString("receive"));
			}
			if(data.has("status")){
				this.setStatus(data.getString("status"));
			}
			if (data.has("list")) {
				JSONArray list = data.getJSONArray("list");
				List<ModelTask> temList = new ArrayList<ModelTask>();
				for (int i = 0; i < list.length(); i++) {
					ModelTask taski = new ModelTask(list.getJSONObject(i));
					taski.setTask_Title(getTitle());
					taski.setTask_level(getTask_level());
					taski.setTask_type(getTask_type());
					taski.setTaskTypeStaus(getStatus());
					taski.setTask_type_receive(getReceive());
					temList.add(taski);
				}
				this.setTask_List(temList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTask_type() {
		return task_type;
	}

	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}

	public String getTask_level() {
		return task_level;
	}

	public void setTask_level(String task_level) {
		this.task_level = task_level;
	}

	/**
	 * 获取该类任务的任务列表
	 * 
	 * @return
	 */
	public List<ModelTask> getTask_List() {
		return task_List;
	}

	public void setTask_List(List<ModelTask> task_List) {
		this.task_List = task_List;
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
