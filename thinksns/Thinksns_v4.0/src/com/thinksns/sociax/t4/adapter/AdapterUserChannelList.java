package com.thinksns.sociax.t4.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.api.ApiChannel;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.widget.roundimageview.RoundedImageView;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelChannel;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明： 用户关注的频道列表
 *
 * @author wz
 * @version 1.0
 * @date 2014-12-15
 */
public class AdapterUserChannelList extends AdapterSociaxList {

    private Thinksns application;

    public AdapterUserChannelList(ThinksnsAbscractActivity context,
                                  ListData<SociaxItem> list) {
        super(context, list);
        application = (Thinksns) context.getApplicationContext();
    }

    public AdapterUserChannelList(FragmentSociax fragment,
                                  ListData<SociaxItem> list) {
        super(fragment, list);
        application = (Thinksns) context.getApplicationContext();
    }

    @Override
    public int getItemViewType(int position) {
        if(list == null || list.size() == 0) {
            if (adapterState == AdapterSociaxList.NO_MORE_DATA) {
                return 0;
            }else if(adapterState == AdapterSociaxList.STATE_LOADING) {
                return 2;
            }
        }
        return 1;

    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        if(list.size() == 0) {

            if(adapterState == AdapterSociaxList.NO_MORE_DATA) {
                //正在加载或加载结束
                return 1;
            }else if(adapterState == AdapterSociaxList.STATE_LOADING) {
                return 1;
            }
            return 0;
        }else {
            return list.size();
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        HolderSociax holder = null;
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            if (type == 1) {
                holder = new HolderSociax();
                convertView = inflater
                        .inflate(R.layout.listitem_user_channel, null);
                holder.img_channel_icon = (RoundedImageView) convertView
                        .findViewById(R.id.img_channel_icon);
                holder.tv_channel_name = (TextView) convertView
                        .findViewById(R.id.tv_channel_name);
                holder.tv_channel_des = (TextView) convertView
                        .findViewById(R.id.tv_channel_des);
            } else if (type == 0) {
                convertView = inflater.inflate(R.layout.default_like_bg, null);
                holder = new HolderSociax();
            } else if (type == 2) {
                convertView = inflater.inflate(R.layout.loading, null);
                PullToRefreshListView listView = getPullRefreshView();
                int width = listView.getWidth();
                int height = listView.getHeight() - 100;
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
                convertView.setLayoutParams(params);
            }
            convertView.setTag(R.id.tag_viewholder, holder);
        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        if (type == 1) {
            convertView.setTag(R.id.tag_channel, getItem(position));

//		ImageLoader.getInstance().displayImage(getItem(position)
//				.getUserChannelImageUrl(),holder.img_channel_icon, Thinksns.getOptions());

//		application.displayImage(getItem(position)
//				.getUserChannelImageUrl(),holder.img_channel_icon);

            Glide.with(context).load(getItem(position).getUserChannelImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(holder.img_channel_icon);

            holder.tv_channel_name.setText(getItem(position).getcName());
            holder.tv_channel_des.setText("已产生"
                    + (getItem(position).getCount().equals("") ? "0" : getItem(
                    position).getCount()) + "条内容");
        }
        return convertView;
    }

    @Override
    public int getMaxid() {
        return getLast() == null ? 0 : ((ModelChannel) getLast()).getId();
    }

    @Override
    public ModelChannel getItem(int position) {
        return (ModelChannel) this.list.get(position);
    }

    @Override
    public ListData<SociaxItem> refreshHeader(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        Log.i("channel", "user----refreshHeader");
        return refreshNew(PAGE_COUNT);
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        Log.i("channel", "user----refreshFooter");
        return getApi().getUserChannel(PAGE_COUNT, getMaxid(), httpListener);
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return getApi().getUserChannel(PAGE_COUNT, 0, httpListener);
    }

    ApiChannel getApi() {
        return thread.getApp().getChannelApi();
    }
}
