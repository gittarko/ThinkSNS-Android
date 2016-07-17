package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;

import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * Created by hedong on 16/2/29.
 * 用户列表
 */
public class AdapterUserFollowingListNew extends ListBaseAdapter<ModelSearchUser> {
    private UnitSociax unit;

    public AdapterUserFollowingListNew(Context context) {
        super(context);
        this.unit = new UnitSociax(context);
    }

    @Override
    public int getMaxId() {
        if(mDatas == null || mDatas.size() == 0)
            return 0;
        return ((ModelSearchUser)mDatas.get(mDatas.size() - 1)).getFollow_id();
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder = null;
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            LayoutInflater inflater = getLayoutInflater(mContext);
            viewHolder = new HolderSociax();
                convertView = inflater.inflate(R.layout.listitem_user, null);
                viewHolder.tv_user_photo = (ImageView) convertView
                        .findViewById(R.id.image_photo);
                viewHolder.tv_user_name = (TextView) convertView
                        .findViewById(R.id.unnames);
                viewHolder.tv_user_content = (TextView) convertView
                        .findViewById(R.id.uncontent);
                viewHolder.tv_user_add = (TextView) convertView
                        .findViewById(R.id.image_add);
                viewHolder.ll_uname_adn = (LinearLayout) convertView
                        .findViewById(R.id.ll_uname_adn);
            convertView.setTag(R.id.tag_viewholder, viewHolder);
        } else {
            viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

            convertView.setTag(R.id.tag_search_user, getItem(position));

            Glide.with(mContext).load(getItem(position).getUserface())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(mContext))
                    .crossFade()
                    .into(viewHolder.tv_user_photo);

            viewHolder.tv_user_name.setText(getItem(position).getUname());
            String intro = getItem(position).getIntro();
            if (intro == null || intro.isEmpty() || intro.equals("null"))
                intro = "这家伙很懒，什么也没留下";
            viewHolder.tv_user_content.setText(intro);

            if (viewHolder.ll_uname_adn != null && getItem(position).getUserApprove() != null
                    && getItem(position).getUserApprove().getApprove().size() > 0) {
                unit.addUserGroup(getItem(position).getUserApprove().getApprove(),
                        viewHolder.ll_uname_adn);
            } else {
                if (viewHolder.ll_uname_adn != null) {
                    viewHolder.ll_uname_adn.removeAllViews();
                }
            }

            viewHolder.tv_user_add.setTag(R.id.tag_position, position);
            viewHolder.tv_user_add.setTag(R.id.tag_follow, getItem(position));

            if (getItem(position).getFollowing().equals("0")) {
                //加关注
                viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_green_digg);
                viewHolder.tv_user_add.setText(R.string.fav_add_follow);
                viewHolder.tv_user_add.setTextColor(mContext.getResources().getColor(R.color.fav_border));
            } else {
                //取消关注
                viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_fav_true);
                viewHolder.tv_user_add.setText(R.string.fav_followed);
                viewHolder.tv_user_add.setTextColor(mContext.getResources().getColor(R.color.fav_text_true));
            }

            viewHolder.tv_user_add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.setClickable(false);
                    FunctionChangeFollow fcChangeFollow = new FunctionChangeFollow(mContext,
                            AdapterUserFollowingListNew.this, v);
                    fcChangeFollow.changeListFollow();
                }
            });

        return convertView;
    }
}
