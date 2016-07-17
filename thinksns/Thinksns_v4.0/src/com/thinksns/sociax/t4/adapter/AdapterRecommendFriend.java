package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentRecommendFriend;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibo;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

public class AdapterRecommendFriend extends AdapterUserFollowingList {

    private int count;

    public AdapterRecommendFriend(FragmentSociax fragment, ListData<SociaxItem> list, int uid, int count) {
        super(fragment, list, uid);
    }

    public AdapterRecommendFriend(ThinksnsAbscractActivity context, ListData<SociaxItem> list, int count) {
        super(context, list);
    }

    @Override
    public int getMaxid() {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder = null;
        int type = getItemViewType(position);
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            viewHolder = new HolderSociax();
            if (type == 1) {
                convertView = inflater.inflate(R.layout.listitem_rcd_user, null);
                viewHolder.tv_user_photo = (ImageView) convertView.findViewById(R.id.image_photo);
                viewHolder.tv_user_name = (TextView) convertView
                        .findViewById(R.id.unnames);
                viewHolder.rl_rcd_item = (RelativeLayout) convertView.findViewById(R.id.rl_rcd_item);
                viewHolder.iv_chonsed = (ImageView) convertView.findViewById(R.id.iv_chonsed);
                viewHolder.iv_chonsed.setTag(false);
            } else if (type == 0){
                convertView = inflater.inflate(R.layout.default_nobody_bg, null);
                holder = new HolderSociax();
                holder.tv_empty_content = (TextView) convertView.findViewById(R.id.tv_empty_content);
            } else if (type == 2) {
                convertView = inflater.inflate(R.layout.loading, null);
            }
            convertView.setTag(R.id.tag_viewholder, viewHolder);
        } else {
            viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        if (type == 1) {
            convertView.setTag(R.id.tag_search_user, getItem(position));
            Glide.with(context).load(getItem(position).getUserface())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(context))
                    .crossFade()
                    .into(viewHolder.tv_user_photo);
            viewHolder.rl_rcd_item.setTag(R.id.tag_position, position);
            viewHolder.rl_rcd_item.setTag(R.id.tag_follow, getItem(position));
            viewHolder.tv_user_name.setText(getItem(position).getUname());
            if (getItem(position).getFollowing().equals("0")) {
                viewHolder.iv_chonsed.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unchonsed));
            } else {
                viewHolder.iv_chonsed.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chonsed));
            }
            viewHolder.rl_rcd_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    FunctionChangeFollow fcChangeFollow = new FunctionChangeFollow(
                            context, AdapterRecommendFriend.this, v);
                    fcChangeFollow.changeListFollow();
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelSearchUser user = getItem(position);
                    user.setFollowing(user.getFollowing().equals("0") ? "1" : "0");
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {

        return (ListData<SociaxItem>) getApiUser().searchUser(httpListener, count);
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return (ListData<SociaxItem>) getApiUser().searchUser(httpListener, count);
    }

    @Override
    public void addFooter(ListData<SociaxItem> list) {
        super.addFooter(list);
        if(fragment instanceof FragmentRecommendFriend) {
            ((FragmentRecommendFriend)fragment).loadDataDone();
        }
    }
}
