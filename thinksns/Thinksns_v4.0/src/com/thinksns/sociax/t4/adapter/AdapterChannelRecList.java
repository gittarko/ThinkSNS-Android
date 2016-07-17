package com.thinksns.sociax.t4.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.AppendChannelList;
import com.thinksns.sociax.t4.android.fragment.FragmentRecommendChannel;
import com.thinksns.sociax.t4.android.fragment.FragmentRecommendFriend;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.widget.roundimageview.RoundedImageView;
import com.thinksns.sociax.t4.component.GlideRoundTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;

import com.thinksns.sociax.t4.model.ModelChannel;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明： 频道列表 默认获取所有频道
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-16
 */
public class AdapterChannelRecList extends AdapterSociaxList {

    @Override
    public ModelChannel getFirst() {
        return (ModelChannel) super.getFirst();
    }

    @Override
    public ModelChannel getLast() {
        return (ModelChannel) super.getLast();
    }

    /**
     * 获取最后一条的id
     *
     * @return
     */
    public int getMaxid() {
        if (getLast() == null)
            return 0;
        else
            return getLast().getId();
    }

    @Override
    public ModelChannel getItem(int position) {
        return (ModelChannel) list.get(position);
    }

    /**
     * 从Activity生成的频道adapter
     *
     * @param context
     * @param list
     */
    public AdapterChannelRecList(ThinksnsAbscractActivity context,
                                 ListData<SociaxItem> list) {
        super(context, list);
    }

    /**
     * 从fragment生成的频道adapter
     *
     * @param fragment
     * @param list
     */
    public AdapterChannelRecList(FragmentSociax fragment, ListData<SociaxItem> list) {
        super(fragment, list);
        for (int i = 0; i < list.size(); i++) {
            Log.i("channel", "AdapterChannelList" + list.get(i).toString() + "");
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderSociax holder = null;
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            holder = new HolderSociax();
            convertView = inflater.inflate(R.layout.listitem_rcd_user, null);
            holder.tv_user_photo = (ImageView) convertView.findViewById(R.id.image_photo);
            holder.tv_user_name = (TextView) convertView
                    .findViewById(R.id.unnames);
            holder.rl_rcd_item = (RelativeLayout) convertView.findViewById(R.id.rl_rcd_item);
            holder.iv_chonsed = (ImageView) convertView.findViewById(R.id.iv_chonsed);
            holder.iv_chonsed.setTag(false);
            convertView.setTag(R.id.tag_viewholder, holder);
        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        convertView.setTag(R.id.tag_channel, getItem(position));
        Glide.with(context).load(getItem(position).getUserChannelImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideRoundTransform(context))
                .crossFade()
                .into(holder.tv_user_photo);

        holder.tv_user_name.setText(getItem(position).getcName());

        if (getItem(position).getIs_follow() == 0) {
            holder.iv_chonsed.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unchonsed));
        } else {
            holder.iv_chonsed.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chonsed));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelChannel channel = getItem(position);
                channel.setIs_follow(channel.getIs_follow() == 0  ? 1 : 0);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public ListData<SociaxItem> refreshHeader(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return refreshNew(PAGE_COUNT);
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return getApiChannel().getAllChannel(PAGE_COUNT, getMaxid(), httpListener);
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return getApiChannel().getAllChannel(PAGE_COUNT, 0, httpListener);
    }

    protected Api.ChannelApi getApiChannel() {
        return thread.getApp().getChannelApi();
    }

    @Override
    public void addFooter(ListData<SociaxItem> list) {
        super.addFooter(list);
        if(fragment instanceof FragmentRecommendChannel) {
            ((FragmentRecommendChannel)fragment).loadDataDone();
        }
    }
}
