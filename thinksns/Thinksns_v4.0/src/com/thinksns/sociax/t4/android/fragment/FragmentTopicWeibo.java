package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterWeiboAll;
import com.thinksns.sociax.t4.android.presenter.WeiboListListPresenter;
import com.thinksns.sociax.t4.android.topic.ActivityTopicWeibo;
import com.thinksns.sociax.t4.exception.WeiboDataInvalidException;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/** 
 * 类说明： 某个话题内的微博列表，需要传入String topic_name
 * @version 1.0
 */
public class FragmentTopicWeibo extends  FragmentWeiboListViewNew{
	//手指最小滑动距离
	private static final int DEFAULT_SLIP_DISTANCE = 50;

	private int topic_id;
	private String  topic_name="";
	private String topic_count;
	private String topic_count_reg = "该话题共有%s条相关分享";

	private ActivityTopicWeibo topicActivity;

	private ImageView img_topic;
	private TextView tv_topic_des;
	private View header;

	public static FragmentTopicWeibo newInstance(int id, String name, String count) {
		FragmentTopicWeibo fragmentTopicWeibo = new FragmentTopicWeibo();
		Bundle bundle = new Bundle();
		bundle.putInt("id", id);
		bundle.putString("count", count);
		bundle.putString("name", name);
		fragmentTopicWeibo.setArguments(bundle);

		return fragmentTopicWeibo;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityTopicWeibo) {
			topicActivity = (ActivityTopicWeibo)activity;
		}

		//标明此微博列表不属于首页
		isInHome = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			topic_name = getArguments().getString("name");
			topic_count = getArguments().getString("count");
			topic_id = getArguments().getInt("topic_id", 0);
		}
	}

	@Override
	protected void initListViewAttrs() {
		super.initListViewAttrs();
		mListView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));
		//添加头部
		header = getActivity().getLayoutInflater().inflate(R.layout.header_topic_list, null);
		header.setVisibility(View.GONE);
		img_topic = (ImageView)header.findViewById(R.id.img_topic);
		tv_topic_des = (TextView)header.findViewById(R.id.tv_topic_des);
		mListView.addHeaderView(header);
	}

	@Override
	protected void initListener() {
		//设置列表的触摸事件
		mListView.setOnTouchListener(new View.OnTouchListener() {

			//手势上下滑动距离
			float distance, lastY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mListView.getFirstVisiblePosition() <= 1)
					return false;

				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						lastY = event.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						float currentY = event.getY();
						if(lastY != 0) {
							distance += (currentY - lastY);
						}
						//记录当前坐标
						lastY = currentY;
						break;
					case MotionEvent.ACTION_UP:
						if(distance > DEFAULT_SLIP_DISTANCE) {
							//上滑
							topicActivity.toggleCreateBtn(true);
						}else if(distance < -DEFAULT_SLIP_DISTANCE) {
							//下滑
							topicActivity.toggleCreateBtn(false);
						}

						lastY = 0;
						distance = 0;
						break;
				}
				return false;
			}
		});
	}

	@Override
	public void initData() {
		tv_topic_des.setText(String.format(topic_count_reg, topic_count));
	}

	@Override
	protected void initPresenter() {
		mPresenter = new WeiboListListPresenter(getActivity(), this, this) {
			@Override
			public ListData<ModelWeibo> parseList(String result) {
				ListData<ModelWeibo> returnlist = new ListData<ModelWeibo>();
				try {
					JSONObject json = new JSONObject(result);
					if(json.has("detail")) {
						JSONObject detail = json.getJSONObject("detail");
						responseTopicDetails(detail);
					}
					JSONArray data = json.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						ModelWeibo weibo = new ModelWeibo(data.getJSONObject(i));
						returnlist.add(weibo);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (WeiboDataInvalidException e) {
					e.printStackTrace();
				}

				return returnlist;
			}

			@Override
			protected ListData<ModelWeibo> readList(Serializable seri) {
				return super.readList(seri);
			}

			@Override
			public String getCachePrefix() {
				return super.getCachePrefix();
			}

			@Override
			public void loadNetData() {
				new Api.WeiboApi().getTopicWeibo(topic_name, getPageSize(), getMaxId(), mHandler);
			}

		};

		//设置缓存目录
		String cache = "topic_weibo_details_";
		if(topic_id != 0) {
			cache += topic_id;
		}else {
			cache += topic_name;
		}

		mPresenter.setCacheKey(cache);
	}

	public void responseTopicDetails(final JSONObject details) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try{
					String pic = details.getString("pic");
					int count = details.getInt("count");
					if(!TextUtils.isEmpty(pic)) {
						img_topic.setVisibility(View.VISIBLE);
						Glide.with(getActivity()).load(pic)
								.crossFade().placeholder(R.drawable.default_image_small)
								.into(img_topic);
					}else {
						img_topic.setVisibility(View.GONE);
					}
					tv_topic_des.setText(String.format(topic_count_reg, count));
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected ListBaseAdapter<ModelWeibo> getListAdapter() {
		return new AdapterWeiboAll(getActivity(), this, mListView);
	}


	@Override
	public void onTabClickListener() {

	}

	@Override
	public void onLoadDataSuccess(ListData<ModelWeibo> data) {
		super.onLoadDataSuccess(data);
		header.setVisibility(View.VISIBLE);
	}

	@Override
	public void onCommentWeibo(ModelWeibo weibo, ModelComment comment) {
		super.onCommentWeibo(weibo, comment);
		if(topicActivity != null) {
			topicActivity.toggleCreateBtn(false);
		}
	}

	@Override
	protected void resetComentUI() {
		super.resetComentUI();
		if(topicActivity != null) {
			topicActivity.toggleCreateBtn(true);
		}
	}
}
