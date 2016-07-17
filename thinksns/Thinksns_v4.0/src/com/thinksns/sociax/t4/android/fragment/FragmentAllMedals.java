package com.thinksns.sociax.t4.android.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.thinksns.sociax.t4.adapter.AdapterMedal;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.task.ActivityDialogMedal;
import com.thinksns.sociax.t4.model.ModelMedals;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/**
 * 类说明：全部勋章
 * 
 * @author Zoey
 * @date 2015年9月7日
 * @version 1.0
 */
public class FragmentAllMedals extends FragmentSociax {

	private GridView gv_all_medals;
	private AdapterMedal adapterMedal;
	private MedalHandler mHandler=new MedalHandler();
	private ArrayList<ModelMedals> medList=null;
	private LoadingView loadingView;

	
	public void getAllMedals(){
		
		loadingView.show(gv_all_medals);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				Message msg = new Message();
				msg.what = StaticInApp.GET_ALL_MEDALS;
				try {
					msg.obj = ((Thinksns) (getActivity().getApplicationContext())).getMedalApi().getAllMedals();
				} catch (Exception e) {
					e.printStackTrace();
					loadingView.hide(gv_all_medals);
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
			case StaticInApp.GET_ALL_MEDALS:
				try {
					if (msg.obj == null)
						break;
					String medalJson=(msg.obj).toString();
					JSONArray array=new JSONArray(medalJson);
					medList=new ArrayList<ModelMedals>();
					
					for (int i = 0; i < array.length(); i++) {
						
						JSONObject object=array.getJSONObject(i);
						try {
							ModelMedals medals=new ModelMedals(object);
							medList.add(medals);
						} catch (DataInvalidException e) {
							e.printStackTrace();
						}
					}
					
					if (medList!=null && medList.size() > 0) {
						adapterMedal = new AdapterMedal(getActivity(), medList);
						gv_all_medals.setAdapter(adapterMedal);
						getDefaultView().setVisibility(View.GONE);
					}else {
						getDefaultView().setVisibility(View.VISIBLE);
					}
					
					gv_all_medals.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
							
							Intent intent=new Intent(getActivity(),ActivityDialogMedal.class);
							intent.putExtra("show", medList.get(position).getShow());
							startActivity(intent);
						}
					});
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				loadingView.hide(gv_all_medals);
				
				break;
			}
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_all_medals;
	}

	@Override
	public void initView() {
		gv_all_medals=(GridView)findViewById(R.id.gv_all_medals);
		gv_all_medals.setVerticalScrollBarEnabled(false); //设置滑动条垂直不显示  
		loadingView= (LoadingView)findViewById(LoadingView.ID);
	}

	@Override
	public void initIntentData() {

	}

	@Override
	public void initListener() {

	}

	@Override
	public void initData() {
		getAllMedals();
	}

	@Override
	public View getDefaultView() {
		return findViewById(R.id.default_nomedal_bg);
	}
}
