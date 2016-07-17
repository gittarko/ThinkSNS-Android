package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterMedal;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.task.ActivityDialogMedal;
import com.thinksns.sociax.t4.android.task.ActivityMedalPavilion;
import com.thinksns.sociax.t4.model.ModelMedals;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 类说明：   我的勋章
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年9月7日
 */
public class FragmentMyMedal extends FragmentSociax {

    private GridView gv_my_medal;
    private AdapterMedal adapterMedal;
    private MedalHandler mHandler = new MedalHandler();
    private static ArrayList<ModelMedals> medList = null;
    private LoadingView loadingView;

    public void getMyMedal() {

        loadingView.show(gv_my_medal);
        final int uid = ((ActivityMedalPavilion) getActivity()).getUid();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = new Message();
                msg.what = StaticInApp.GET_MY_MEDAL;
                try {

                    if (uid == Thinksns.getMy().getUid()) {
                        msg.obj = ((Thinksns) (getActivity().getApplicationContext())).getMedalApi().getMyMedal(0);
                    } else {
                        msg.obj = ((Thinksns) (getActivity().getApplicationContext())).getMedalApi().getMyMedal(uid);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loadingView.hide(gv_my_medal);
                }
                mHandler.sendMessage(msg);
            }
        }).start();
    }


    @SuppressLint("HandlerLeak")
    public class MedalHandler extends Handler {

        public MedalHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticInApp.GET_MY_MEDAL:

                    try {
                        if (msg.obj == null) {
                            return;
                        }
                        String medalJson = (msg.obj).toString();
                        JSONArray array = new JSONArray(medalJson);
                        medList = new ArrayList<ModelMedals>();

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject object = array.getJSONObject(i);
                            try {
                                ModelMedals medals = new ModelMedals(object);
                                medList.add(medals);
                            } catch (DataInvalidException e) {
                                e.printStackTrace();
                            }
                        }

                        if (medList != null && medList.size() > 0) {
                            adapterMedal = new AdapterMedal(getActivity(), medList);
                            gv_my_medal.setAdapter(adapterMedal);
                            getDefaultView().setVisibility(View.GONE);
                        } else {
                            getDefaultView().setVisibility(View.VISIBLE);
                        }

                        gv_my_medal.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Intent intent = new Intent(getActivity(), ActivityDialogMedal.class);
                                intent.putExtra("show", medList.get(position).getShow());
                                startActivity(intent);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    loadingView.hide(gv_my_medal);

                    break;
            }
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_my_medal;
    }

    @Override
    public void initView() {
        gv_my_medal = (GridView) findViewById(R.id.gv_my_medal);
        gv_my_medal.setVerticalScrollBarEnabled(false); //设置滑动条垂直不显示
        loadingView = (LoadingView) findViewById(LoadingView.ID);
    }

    @Override
    public View getDefaultView() {
        return findViewById(R.id.default_nomedal_bg);
    }

    @Override
    public void initIntentData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        getMyMedal();
    }
}
