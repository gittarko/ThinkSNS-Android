package com.thinksns.sociax.t4.android.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.thinksns.sociax.t4.adapter.AdapterCityList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.erweima.units.SideBar;
import com.thinksns.sociax.t4.android.erweima.units.SideBar.OnTouchingLetterChangedListener;
import com.thinksns.sociax.t4.model.ModelCityInfo;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;

/**
 * 类说明：城市列表
 * 
 * @author ZhiShi
 * @date 2014-9-28
 * @version 1.0
 */
public class FragmentCityList extends FragmentSociax {
	protected static final String TAG = "AreaPerson";
	private int lastFirstVisibleItem = -1;
	private AdapterCityList adapter;
	private ListView listView;
	private SideBar sideBar;//字母列表
	
	private TextView dialog;//字母提醒
	
	private TextView tv_my_location;//我的城市

//	private EditText ed_search;//搜索框

	private LinearLayout ll_firstLetter;//第一个字母所在的layout
	private TextView tv_firstLetter;//当前第一个字母

	private PinyingCompare mPinyingCompare = new PinyingCompare();
	private List<ModelCityInfo> mcityInfos = new ArrayList<ModelCityInfo>();
	private LoadingView loadingview;
	protected ListHandler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Log.d(TAG, "onCreate");
	}
	@Override
	public void initListener() {
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.toLowerCase()
						.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}
			}
		});
	}

	private void filterDate(String str) {
		List<ModelCityInfo> filterDateList = new ArrayList<ModelCityInfo>();
		if (TextUtils.isEmpty(str)) {
			Collections.sort(mcityInfos, mPinyingCompare);
			tv_firstLetter.setText(mcityInfos.get(0).getSortLetters().toUpperCase()
					.substring(0, 1));
			adapter.updateListView(mcityInfos);
		} else {
			filterDateList.clear();
			for (ModelCityInfo cityInfo : mcityInfos) {
				if (cityInfo.getName().indexOf(str) != -1
						|| cityInfo.getSortLetters().startsWith(
								str.toLowerCase().substring(0))
						|| cityInfo.getName_pinyin().startsWith(
								str.toLowerCase().substring(0))) {
					filterDateList.add(cityInfo);
				}
			}
			if (filterDateList.size() > 0) {
				Collections.sort(filterDateList, mPinyingCompare);
				tv_firstLetter.setText(filterDateList.get(0).getSortLetters()
						.toUpperCase().substring(0, 1));
				adapter.updateListView(filterDateList);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getCityListTask();
	}

	void getCityListTask() {
		loadingview.show(sideBar);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.GET_CITY_LIST;
				try {
					msg.obj = ((Thinksns) (getActivity()
							.getApplicationContext())).getUsers().getCityList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	class PinyingCompare implements Comparator<ModelCityInfo> {

		@Override
		public int compare(ModelCityInfo o1, ModelCityInfo o2) {
			if (o1.getSortLetters().equals("@")
					|| o2.getSortLetters().equals("#")) {
				return -1;
			} else if (o1.getSortLetters().equals("#")
					|| o2.getSortLetters().equals("@")) {
				return 1;
			} else {
				return o1.getSortLetters().compareTo(o2.getSortLetters());
			}
		}

	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < mcityInfos.size(); i++) {
			String sortStr = mcityInfos.get(i).getSortLetters();
			char firstChar = sortStr.charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public void setRetainInstance(boolean retain) {
		super.setRetainInstance(retain);
	}

	@Override
	public void initView() {
//		ed_search = (EditText)findViewById(R.id.editText_person_area);
		listView = (ListView) findViewById(R.id.country_lvcountry);
		listView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));

		dialog = (TextView)findViewById(R.id.dialog);
		sideBar = (SideBar)findViewById(R.id.sidrbar);
		ll_firstLetter = (LinearLayout)findViewById(R.id.title_layout);
		tv_firstLetter = (TextView)findViewById(R.id.title_layout_area);
		tv_my_location = (TextView) findViewById(R.id.title_area_local);
		sideBar.setTextView(dialog);
		mHandler = new ListHandler();
		loadingview = (LoadingView)findViewById(LoadingView.ID);
	}

	@Override
	public void initIntentData() {

	}

	@Override
	public void initData() {

	}

	@SuppressLint("HandlerLeak")
	public class ListHandler extends Handler {

		public ListHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.GET_CITY_LIST:
				Map<String, List<ModelCityInfo>> map = (Map<String, List<ModelCityInfo>>) msg.obj;
				try {
					final ModelCityInfo info = map.get("my").get(0);
					// caoligai 修改定位城市不可见
					/*tv_my_location.setText(info.getName());
					tv_my_location
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(getActivity(),ActivitySearchUser.class);
									intent.putExtra("type",StaticInApp.FINDPEOPLE_CITY);
									intent.putExtra("city_id", info.getId());
									intent.putExtra("title", info.getName());
									getActivity().startActivity(intent);
								}
							});*/

				} catch (Exception e) {
					e.printStackTrace();
				}
				map.remove("my");
				for (Entry<String, List<ModelCityInfo>> key : map.entrySet()) {
					mcityInfos.addAll(key.getValue());
				}
				Collections.sort(mcityInfos, mPinyingCompare);
				adapter = new AdapterCityList(getActivity(), mcityInfos, 0);
				listView.setAdapter(adapter);
				listView.setOnScrollListener(new OnScrollListener() {

					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {

					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						if(mcityInfos.size() == 0)
							return;
						if (mcityInfos.get(firstVisibleItem).getSortLetters()
								.length() > 0
								&& firstVisibleItem >= 0) {//
							int section = mcityInfos.get(firstVisibleItem).getSortLetters().charAt(0);
							int nextSection = mcityInfos.get(firstVisibleItem + 1).getSortLetters().charAt(0);
							int nextSecPosition = getPositionForSection(+nextSection);
							
							if (firstVisibleItem != lastFirstVisibleItem) {
								MarginLayoutParams params = (MarginLayoutParams) ll_firstLetter
										.getLayoutParams();
								params.topMargin = 0;
								ll_firstLetter.setLayoutParams(params);
								String title_string = mcityInfos.get(
										getPositionForSection(section))
										.getSortLetters();
								//
								if (title_string.length() > 0)
									tv_firstLetter.setText(title_string.toUpperCase()
											.subSequence(0, 1));
							}
							if (nextSecPosition == firstVisibleItem + 1) {
								View childView = view.getChildAt(0);
								if (childView != null) {
									int titleHeight = ll_firstLetter.getHeight();
									int bottom = childView.getBottom();
									MarginLayoutParams params = (MarginLayoutParams) ll_firstLetter
											.getLayoutParams();
									if (bottom < titleHeight) {
										float pushedDistance = bottom
												- titleHeight;
										params.topMargin = (int) pushedDistance;
										ll_firstLetter.setLayoutParams(params);
									} else {
										if (params.topMargin != 0) {
											params.topMargin = 0;
											ll_firstLetter.setLayoutParams(params);
										}
									}
								}
							}
							lastFirstVisibleItem = firstVisibleItem;
						}
					}
				});
				loadingview.hide(sideBar);
				break;
			}
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_city_list;
	}
}
