package com.thinksns.sociax.t4.android.fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.model.ModelChannel;
import com.thinksns.sociax.thinksnsbase.base.BaseListPresenter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

/**
 * 类说明： 我关注的频道列表
 * 
 * @author wz
 * @date 2014-12-11
 * @version 1.0
 */
public class FragmentFollowChannelList extends FragmentChannelList{

	@Override
	protected void initPresenter() {
		mPresenter = new BaseListPresenter<ModelChannel>(getActivity(), this) {
			@Override
			public ListData<ModelChannel> parseList(String result) {
				try {
					JSONArray response = new JSONArray(result);
					int length = response.length();
					ListData<ModelChannel> list = new ListData<ModelChannel>();
					for (int i = 0; i < length; i++) {
						ModelChannel c;
						try {
							c = new ModelChannel(response.getJSONObject(i));
							c.setIs_follow(1);
							list.add(c);
						} catch (DataInvalidException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
					return list;
				}catch(JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected ListData<ModelChannel> readList(Serializable seri) {
				return (ListData<ModelChannel>)seri;
			}

			@Override
			public String getCachePrefix() {
				return "channel_list";
			}

			@Override
			public void loadNetData() {
				new Api.ChannelApi().getUserChannel(getPageSize(), getMaxId(), mHandler);
			}
		};

		mPresenter.setCacheKey("user_follow_channel");
	}
}
