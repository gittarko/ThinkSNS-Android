package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelDiggUser;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * Created by hedong on 16/3/31.
 * 某帖子的点赞用户列表数据集合
 */
public class AdapterPostDiggUsers extends ListBaseAdapter<ModelDiggUser>{
    public AdapterPostDiggUsers(Context context) {
        super(context);
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder;
        LayoutInflater inflater = getLayoutInflater(mContext);
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
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
            convertView.setTag(R.id.tag_viewholder, viewHolder);
        } else {
            viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        final ModelDiggUser user = getItem(position);

        convertView.setTag(R.id.tag_search_user, user);

        Glide.with(mContext).load(user.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(mContext))
                .crossFade()
                .into(viewHolder.tv_user_photo);
        viewHolder.tv_user_name.setText(user.getUname());

        if (TextUtils.isEmpty(user.getIntro())) {
            viewHolder.tv_user_content.setText("这家伙很懒，什么也没留下");
        } else {
            viewHolder.tv_user_content.setText(user.getIntro());
        }

        viewHolder.tv_user_add.setTag(R.id.tag_position, position);
        viewHolder.tv_user_add.setTag(R.id.tag_follow, user);

        if (user.getUid() != Thinksns.getMy().getUid()) {
            viewHolder.tv_user_add.setVisibility(View.VISIBLE);
            if (user.getFollowing().equals("0")) {
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
        } else {
            viewHolder.tv_user_add.setVisibility(View.GONE);
        }

        //加关注
        viewHolder.tv_user_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setClickable(false);
                FunctionChangeFollow fcChangeFollow = new FunctionChangeFollow(mContext, AdapterPostDiggUsers.this, v);
                fcChangeFollow.changeListFollow();
            }
        });

        return convertView;

    }
}
