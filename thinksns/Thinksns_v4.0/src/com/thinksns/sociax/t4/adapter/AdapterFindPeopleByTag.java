package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明： 根据标签找人
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-4
 */
public class AdapterFindPeopleByTag extends AdapterUserFollowingList {
    int tag_id;

    public AdapterFindPeopleByTag(FragmentSociax fragment,
                                  ListData<SociaxItem> list, int uid, int tag_id) {
        super(fragment, list, uid);
        this.tag_id = tag_id;
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {

        return (ListData<SociaxItem>) getApiUser().searchUserByTag(tag_id, 0, httpListener);
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return (ListData<SociaxItem>) getApiUser().searchUserByTag(tag_id,
                getMaxid(), httpListener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder = null;
        int type = getItemViewType(position);
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            if (type == 1) {
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
            } else if (type == 0) {
                convertView = inflater.inflate(R.layout.default_nobody_bg, null);
                holder = new HolderSociax();
                holder.tv_empty_content = (TextView) convertView.findViewById(R.id.tv_empty_content);
            } else if (type == 2) {
                //加载正在加载数据的界面
                convertView = inflater.inflate(R.layout.loading, null);
                PullToRefreshListView listView = getPullRefreshView();
                int width = listView.getWidth();
                int height = listView.getHeight() - 100;
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
                convertView.setLayoutParams(params);
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

            viewHolder.tv_user_name.setText(getItem(position).getUname());
            if (getItem(position).getIntro().isEmpty() ||
                    getItem(position).getIntro().equals("null")) {
                viewHolder.tv_user_content.setText("这家伙很懒，什么也没留下");
            } else
                viewHolder.tv_user_content.setText(getItem(position).getIntro());

            viewHolder.tv_user_add.setVisibility(View.VISIBLE);
            viewHolder.tv_user_add.setTag(R.id.tag_position, position);
            viewHolder.tv_user_add.setTag(R.id.tag_follow, getItem(position));

            if (getItem(position).getFollowing().equals("0")) {
                //加关注
                viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_green_digg);
                viewHolder.tv_user_add.setText(R.string.fav_add_follow);
                viewHolder.tv_user_add.setTextColor(context.getResources().getColor(R.color.fav_border));
            } else {
                //取消关注
                viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_fav_true);
                viewHolder.tv_user_add.setText(R.string.fav_followed);
                viewHolder.tv_user_add.setTextColor(context.getResources().getColor(R.color.fav_text_true));
            }

            viewHolder.tv_user_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    FunctionChangeFollow fcChangeFollow = new FunctionChangeFollow(
                            context, AdapterFindPeopleByTag.this, v);
                    fcChangeFollow.changeListFollow();
                }
            });
        } else if (type == 0) {
            holder.tv_empty_content.setText("当前分类下还没有小伙伴");
        }
        return convertView;
    }

    @Override
    public int getMaxid() {
        if (getLast() == null)
            return 0;
        else
            return getLast().getUid();
    }
}
