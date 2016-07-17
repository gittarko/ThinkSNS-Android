package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Task extends TaskCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 11L;
	private int cateId;
	private String cateName;

	private int taskId;
	private String taskTitle;

	private String ctime;
	private String deadline;
	private String formatDeadLine;

	private int done_uid;
	private int from_uid;
	private int joiner_uid;
	private String joiner_email;

	private String desc;
	private int is_star;
	private int is_over;

	private String type;

	private String overdue = null;

	private int position; // 标记在list中的位置

	// private TextView flagTitle;
	// private Object flagStar;
	// private Object flagDo;
	// private TaskCateHolder flagView;

	public Task() {
	}

	public Task(String taskName, int taskCateId) {
		this.setTaskTitle(taskName);
		this.setCateId(taskCateId);
	}

	public Task(JSONObject data) throws JSONException {
		this.setCateId(data.getInt("category_id"));
		this.setCateName(data.getString("categoryTitle"));
		this.setTaskId(data.getInt("task_id"));
		this.setTaskTitle(data.getString("title"));
		this.setCtime(data.getString("ctime"));
		this.setDeadline(data.getString("deadline"));
		this.setDone_uid(data.getInt("done_uid"));
		this.setFrom_uid(data.getInt("from_uid"));
		this.setJoiner_uid(data.getInt("joiner_uid"));
		this.setDesc(data.getString("desc"));
		this.setIs_star(data.getInt("is_star"));
		this.setIs_over(data.getInt("is_over"));
		this.setJoiner_email(data.getString("joiner_email"));
		if (data.has("type")) {
			overdue = data.getString("type");
		}
		setFormatDeadLine(getDeadline());
	}

	public int getCateId() {
		return cateId;
	}

	public void setCateId(int cateId) {
		this.cateId = cateId;
	}

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getFormatDeadLine() {
		return formatDeadLine;
	}

	public void setFormatDeadLine(String formatDeadLine) {
		this.formatDeadLine = TimeHelper.getStandardTimeWithYeay(Long
				.valueOf(formatDeadLine));
	}

	public int getDone_uid() {
		return done_uid;
	}

	public void setDone_uid(int done_uid) {
		this.done_uid = done_uid;
	}

	public int getFrom_uid() {
		return from_uid;
	}

	public void setFrom_uid(int from_uid) {
		this.from_uid = from_uid;
	}

	public int getJoiner_uid() {
		return joiner_uid;
	}

	public void setJoiner_uid(int joiner_uid) {
		this.joiner_uid = joiner_uid;
	}

	public String getJoiner_email() {
		return joiner_email;
	}

	public void setJoiner_email(String joiner_email) {
		this.joiner_email = joiner_email;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getIs_star() {
		return is_star;
	}

	public void setIs_star(int is_star) {
		this.is_star = is_star;
	}

	public int getIs_over() {
		return is_over;
	}

	public void setIs_over(int is_over) {
		this.is_over = is_over;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getOverdue() {
		return overdue;
	}

	public void setOverdue(String overdue) {
		this.overdue = overdue;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * public TaskCateHolder getFlagView() { return flagView; }
	 * 
	 * public void setFlagView( TaskCateHolder tcHolder) { this.flagView =
	 * tcHolder; }
	 * 
	 * 
	 * public TextView getFlagTitle() { return flagTitle; }
	 * 
	 * public void setFlagTitle(TextView flagTitle) { this.flagTitle =
	 * flagTitle; }
	 * 
	 * public Object getFlagStar() { return flagStar; }
	 * 
	 * public void setFlagStar(Object flagStar) { this.flagStar = flagStar; }
	 * 
	 * public Object getFlagDo() { return flagDo; }
	 * 
	 * public void setFlagDo(Object flagDo) { this.flagDo = flagDo; }
	 */
	@Override
	public String toString() {
		return "Task [cateId=" + cateId + ", cateName=" + cateName
				+ ", taskId=" + taskId + ", taskTitle=" + taskTitle
				+ ", ctime=" + ctime + ", deadline=" + deadline
				+ ", formatDeadLine=" + formatDeadLine + ", done_uid="
				+ done_uid + ", from_uid=" + from_uid + ", joiner_uid="
				+ joiner_uid + ", joiner_email=" + joiner_email + ", desc="
				+ desc + ", is_star=" + is_star + ", is_over=" + is_over
				+ ", type=" + type + "]";
	}

}
