package com.thinksns.sociax.api;

import com.thinksns.sociax.modle.Task;
import com.thinksns.sociax.modle.TaskCategory;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

public interface ApiTask {

	public static final String MOD_NAME = "Task";

	
	// 获取所有任务列表
	public static final String GET_ALL_COLLEAGUE = "get_task_category";
	public static final String CREATE_TASK_CATEGORY = "create_task_category";
	public static final String DESTROY_TASK_CATEGORY = "destroy_task_category";
	public static final String EDIT_TASK_CATEGORY = "edit_task_category";
	public static final String SHARE_TASK_CATEGORY = "share_task_category";
	public static final String CANCEL_SHARE_TASK_CATEGORY = "cancel_share_task_category";

	public static final String GET_TASK_BY_CATEGORY = "get_task_by_category";

	public static final String CREATE_TASK = "create_task";
	public static final String EDIT_TASK = "edit_task";
	public static final String DESTROY_TASK = "destroy_task";

	public static final String STARRED_TASK = "starred_task";
	public static final String CANCEL_STARRED_TASK = "cancel_starred_task";

	public static final String FINISHED_TASK = "finished_task";
	public static final String CANCEL_FINISHED_TASK = "cancel_finished_task";

	public static final String SEARCH_TASK = "search_task";
	public static final String GET_TASK_BY_TYPE = "get_task_by_type";
	public static final String GET_TASK_NOTIFY = "get_task_notify";

	public static final String GET_SHARE_USERS = "get_share_users";

	public static final String TASK_LIST = "task_list";
	public static final String TASK_COMPLETE = "complete_step";

	/***************t4以下*******************/
	/**
	 * 每日任务
	 */
	public static final String DAILY_TASK = "daily";
	ListData<SociaxItem> getDailyTask() throws ApiException;
	/**
	 * 主线任务
	 */
	public static final String MAIN_TASK = "mainLine";
	ListData<SociaxItem> getMainTask() throws ApiException;
	/**
	 * 副本任务
	 */
	public static final String COPY_TASK = "custom";
	ListData<SociaxItem> getCopyTask() throws ApiException;
	
	/*****************t4以上********************/
	/**
	 * 返回任务分类列表
	 */
	ListData<SociaxItem> getTaskCategoryList() throws ApiException;

	ListData<SociaxItem> getTaskListByCategory(TaskCategory tCategory)
			throws ApiException;

	ListData<SociaxItem> getTaskByType(TaskCategory tCategory)
			throws ApiException;

	/**
	 * 返回任务的提醒数
	 */
	String getTaskNotify() throws ApiException;

	ListData<SociaxItem> getShareUser(int ctaeId) throws ApiException;

	ListData<SociaxItem> getTaskBySearchKey(String key) throws ApiException;

	boolean createTaskCate(TaskCategory tCategory) throws ApiException;

	boolean destroyTaskCate(TaskCategory tCategory) throws ApiException;

	boolean eidtTaskCate(TaskCategory tCategory) throws ApiException;

	boolean shareTaskCate(TaskCategory tCategory) throws ApiException;

	boolean delShareTaskCate(TaskCategory tCategory) throws ApiException;

	boolean createTask(Task task) throws ApiException;

	boolean editTask(Task task) throws ApiException;

	boolean destroyTask(Task task) throws ApiException;

	boolean starTask(Task task) throws ApiException;

	boolean unStarTask(Task task) throws ApiException;

	boolean doTask(Task task) throws ApiException;

	boolean unDoTask(Task task) throws ApiException;

	/**
	 * 获取任务列表
	 * 
	 * @return
	 * @throws ApiException
	 */
	ListData<SociaxItem> getTaskList() throws ApiException;

	/**
	 * 完成任务，获取奖励
	 * 
	 * @param task_id
	 * @param task_type
	 * @param task_level
	 * @return
	 * @throws ApiException
	 */
	Object completeTask(String task_id, String task_type, String task_level)
			throws ApiException;

}
