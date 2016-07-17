package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONObject;


/**
 * 类说明： 任务抽象类
 * 
 * @author wz
 * @date 2014-11-25
 * @version 1.0
 */
public class ModelTask extends SociaxItem {
	String task_id, task_name, step_desc,// 任务描述
			reward,// 任务获得奖励
			status,// 任务完成状态0-未完成，1-完成待领取积分，2-已完成并且已经领取积分
			progress_rate,// 进度
			img;// 任务图标url
	private boolean isFirst = false;// 标记是否第一个任务
	String task_type, task_level;
	String task_type_receive;//该分类的任务奖励是否已经领取
	public String getTask_type_receive() {
		return task_type_receive;
	}

	public void setTask_type_receive(String task_type_receive) {
		this.task_type_receive = task_type_receive;
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

	private String task_title = "";// 任务所属类型

	public String getTask_Title() {
		return task_title;
	}

	public void setTask_Title(String task_title) {
		this.task_title = task_title;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public ModelTask(JSONObject data) {
		try {
			if (data.has("task_id")) {
				this.setTask_id(data.getString("task_id"));
			}
			if (data.has("task_name")) {
				this.setTask_name(data.getString("task_name"));
			}
			if (data.has("step_desc")) {
				this.setStep_desc(data.getString("step_desc"));
			}
			if (data.has("reward")) {
				this.setReward(data.getString("reward"));
			}
			if (data.has("progress_rate")) {
				this.setProgress_rate(data.getString("progress_rate"));
			}
			if (data.has("status")) {
				this.setStatus(data.getString("status"));
			}
			if (data.has("img")) {
				this.setImg(data.getString("img"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ModelTask() {
		// TODO Auto-generated constructor stub
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public String getStep_desc() {
		return step_desc;
	}

	public void setStep_desc(String step_desc) {
		this.step_desc = step_desc;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	/**
	 * 
	 * @return 1-待领取积分 0-待完成 2-已完成
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProgress_rate() {
		return progress_rate;
	}

	public void setProgress_rate(String progress_rate) {
		this.progress_rate = progress_rate;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 是否列表分类的最后一个
	 */
	boolean isLast=false;
	
	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		// TODO Auto-generated method stub
		this.isLast=isLast;
	}

	String taskTypeStaus;//任务所在任务类型的状态0是未完成，1是已完成但是未领取奖励，2是已领取奖励
	public String getTaskTypeStaus() {
		return taskTypeStaus;
	}

	public void setTaskTypeStaus(String taskTypeStaus) {
		this.taskTypeStaus = taskTypeStaus;
	}
	
}
