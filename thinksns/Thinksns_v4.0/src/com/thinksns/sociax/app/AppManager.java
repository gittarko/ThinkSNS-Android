package com.thinksns.sociax.app;

import java.util.ArrayList;
import java.util.List;

public class AppManager {

	private List<AppInfo> appList;

	public AppManager() {
		// TODO Auto-generated constructor stub
		appList = new ArrayList<AppInfo>();
	}

	public void addApp(AppInfo app) {

		appList.add(app);
	}

	public void removeApp(AppInfo app) {
		appList.remove(app);
	}

	public List<AppInfo> getAppList() {
		return appList;
	}

}
