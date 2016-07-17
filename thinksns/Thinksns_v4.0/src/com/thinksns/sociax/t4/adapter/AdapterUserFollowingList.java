package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api.Users;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明： 关注的人adpater
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-28
 */
public class AdapterUserFollowingList extends AdapterSociaxList {

    protected int uid;
    protected UnitSociax uint;//工具类

    protected static String msg = "您还没有关注的好伙伴";

    /**
     * 从fragment中生成
     *
     * @param fragment
     * @param list
     * @param uid      获取该uid的信息
     */
    public AdapterUserFollowingList(FragmentSociax fragment,
                                    ListData<SociaxItem> list, int uid) {
        super(fragment, list);
        this.fragment = fragment;
        this.uid = uid;
        this.uint = new UnitSociax(context);
    }

    public AdapterUserFollowingList(ThinksnsAbscractActivity context, ListData<SociaxItem> list) {
        super(context, list);
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
                viewHolder.ll_uname_adn = (LinearLayout) convertView
                        .findViewById(R.id.ll_uname_adn);
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
            String intro = getItem(position).getIntro();
            if (intro == null || intro.isEmpty() || intro.equals("null"))
                intro = "这家伙很懒，什么也没留下";
            viewHolder.tv_user_content.setText(intro);

            if (viewHolder.ll_uname_adn != null && getItem(position).getUserApprove() != null
                    && getItem(position).getUserApprove().getApprove().size() > 0) {
                uint.addUserGroup(getItem(position).getUserApprove().getApprove(),
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
                    v.setClickable(false);
                    FunctionChangeFollow fcChangeFollow = new FunctionChangeFollow(context, AdapterUserFollowingList.this, v);
                    fcChangeFollow.changeListFollow();
                }
            });
        } else if (type == 0) {
            holder.tv_empty_content.setText(msg);
        }

        return convertView;
    }

    @Override
    public int getMaxid() {
        if (getLast() == null)
            return 0;
        else
            return getLast().getFollow_id();
    }

    @Override
    public ModelSearchUser getLast() {
        if (list.size() > 0) {
            return (ModelSearchUser) this.list.get(this.list.size() - 1);
        } else
            return null;
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
        return getApiUser().getUserFollowingList(uid,"", getMaxid(), httpListener);
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return getApiUser().getUserFollowingList(uid,"", 0, httpListener);
    }

    @Override
    public ModelSearchUser getItem(int position) {

        return (ModelSearchUser) this.list.get(position);
    }

    Users getApiUser() {
        return thread.getApp().getUsers();
    }
}
