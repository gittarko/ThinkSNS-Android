package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterAreaList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.erweima.units.SideBar;
import com.thinksns.sociax.t4.android.erweima.units.SideBar.OnTouchingLetterChangedListener;
import com.thinksns.sociax.t4.android.findpeople.ActivityFindPeopleDetails;
import com.thinksns.sociax.t4.android.user.ActivityEditLocationInfo;
import com.thinksns.sociax.t4.model.ModelAreaInfo;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;

import java.util.*;
import java.util.Map.Entry;

/**
 * 地区列表，需要pid进行加载数据
 */
public class FragmentAreaList extends FragmentSociax {
    protected static final String TAG = "FragmentAreaList";
    private static final String ARGS_ABBR_TYPE = "args_abbr_type";
    private static final String ARGS_PID = "args_pid";

    private int abbrType;
    private String pid;

    private int lastFirstVisibleItem = -1;
    private AdapterAreaList adapter;
    private TextView dialog;
    private ListView listView;
    private TextView tv_firth_letter;// 当前最上面显示的字母
    private SideBar sideBar;// 边框
    private LinearLayout ll_firth_letter, ll_my_location;
    private PinyingCompare mPinyingCompare = new PinyingCompare();
    private List<ModelAreaInfo> mcityInfos = new ArrayList<ModelAreaInfo>();
    private LoadingView loadingview;
    protected ListHandler mHandler;

    /**
     * 获取FragmentAreaList实例
     *
     * @param type
     * @param pid
     * @return
     */
    public static FragmentAreaList newInstance(int type, String pid) {
        FragmentAreaList fragment = new FragmentAreaList();

        Bundle args = new Bundle();
        args.putInt(ARGS_ABBR_TYPE, type);
        args.putString(ARGS_PID, pid);
        fragment.setArguments(args);

        return fragment;
    }

    public FragmentAreaList() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // 获取参数
        pid = getArguments().getString(ARGS_PID);
        abbrType = getArguments().getInt(ARGS_ABBR_TYPE);
    }

    @Override
    public void initListener() {
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.toLowerCase().charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
        /**
         * 地区点击事件
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() instanceof ActivityEditLocationInfo) {
                    ActivityEditLocationInfo activity = (ActivityEditLocationInfo) getActivity();
                    ModelAreaInfo info = (ModelAreaInfo) adapter.getItem(position);
                    activity.loadNextAbbr(abbrType, info.getArea_id(), info.getName());
                }else if (getActivity() instanceof ActivityFindPeopleDetails){
                    ActivityFindPeopleDetails activity = (ActivityFindPeopleDetails) getActivity();
                    ModelAreaInfo info = (ModelAreaInfo) adapter.getItem(position);
                    activity.loadNextAbbr(abbrType, info.getArea_id(), info.getName());
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mcityInfos == null || mcityInfos.size() == 0) { // 防止重复请求数据
            getCityListTask();
        }
    }


    /**
     * 从服务器获取地区列表
     */
    private void getCityListTask() {
        loadingview.show(sideBar);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = StaticInApp.GET_AREA_LIST;
                try {
                    msg.obj = ((Thinksns) (getActivity()
                            .getApplicationContext())).getUsers().getArea(pid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    class PinyingCompare implements Comparator<ModelAreaInfo> {

        @Override
        public int compare(ModelAreaInfo o1, ModelAreaInfo o2) {
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
    public void initView() {
        listView = (ListView) findViewById(R.id.country_lvcountry);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        ll_firth_letter = (LinearLayout) findViewById(R.id.title_layout);
        tv_firth_letter = (TextView) findViewById(R.id.title_layout_area);
        ll_my_location = (LinearLayout) findViewById(R.id.title_layout_local);
        ll_my_location.setVisibility(View.GONE);
        sideBar.setTextView(dialog);
        mHandler = new ListHandler();
        loadingview = (LoadingView) findViewById(LoadingView.ID);
    }


    @Override
    public void initIntentData() {
        String id = getActivity().getIntent().getStringExtra("pid");
        if (id != null) {
            this.pid = id;
        }
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
                case StaticInApp.GET_AREA_LIST:
                    if (msg.obj != null && msg.obj.toString().equals("{}")) {
                        if (getActivity() instanceof ActivityEditLocationInfo) {
                            ActivityEditLocationInfo activity = (ActivityEditLocationInfo) getActivity();
//                            activity.loadNextAbbr(abbrType, "");
                            activity.resultAbbrData();
                        }
                    }
                    Map<String, List<ModelAreaInfo>> map = (Map<String, List<ModelAreaInfo>>) msg.obj;
                    for (Entry<String, List<ModelAreaInfo>> key : map.entrySet()) {
                        mcityInfos.addAll(key.getValue());
                    }
                    Collections.sort(mcityInfos, mPinyingCompare);
                    adapter = new AdapterAreaList(getActivity(), mcityInfos, 1);
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
                            if (mcityInfos != null && mcityInfos.size() > 0
                                    && firstVisibleItem + 1 < mcityInfos.size())
                                if (mcityInfos.get(firstVisibleItem)
                                        .getSortLetters().length() > 0
                                        && firstVisibleItem >= 0) {

                                    int section = mcityInfos.get(firstVisibleItem)
                                            .getSortLetters().charAt(0);
                                    int nextSection = mcityInfos
                                            .get(firstVisibleItem + 1)
                                            .getSortLetters().charAt(0);
                                    int nextSecPosition = getPositionForSection(+nextSection);
                                    if (firstVisibleItem != lastFirstVisibleItem) {
                                        MarginLayoutParams params = (MarginLayoutParams) ll_firth_letter
                                                .getLayoutParams();
                                        params.topMargin = 0;
                                        ll_firth_letter.setLayoutParams(params);
                                        String title_string = mcityInfos.get(
                                                getPositionForSection(section))
                                                .getSortLetters();
                                        //
                                        if (title_string.length() > 0)
                                            tv_firth_letter.setText(title_string
                                                    .toUpperCase()
                                                    .subSequence(0, 1));
                                    }
                                    if (nextSecPosition == firstVisibleItem + 1) {
                                        View childView = view.getChildAt(0);
                                        if (childView != null) {
                                            int titleHeight = ll_firth_letter
                                                    .getHeight();
                                            int bottom = childView.getBottom();
                                            MarginLayoutParams params = (MarginLayoutParams) ll_firth_letter
                                                    .getLayoutParams();
                                            if (bottom < titleHeight) {
                                                float pushedDistance = bottom
                                                        - titleHeight;
                                                params.topMargin = (int) pushedDistance;
                                                ll_firth_letter
                                                        .setLayoutParams(params);
                                            } else {
                                                if (params.topMargin != 0) {
                                                    params.topMargin = 0;
                                                    ll_firth_letter
                                                            .setLayoutParams(params);
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
