package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-24
 */
public class AdapterSearchAt extends AdapterUserFollowingList {
    String key;

    public AdapterSearchAt(FragmentSociax fragment,
                           ListData<SociaxItem> list, int uid, String key) {
        super(fragment, list, uid);
        this.key = key;
    }

    /**
     * 从@用户中搜索用户
     *
     * @param context
     * @param list
     * @param key
     */
    public AdapterSearchAt(ThinksnsAbscractActivity context,
                           ListData<SociaxItem> list, String key) {
        super(context, list);
        this.key = key;
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return (ListData<SociaxItem>) getApiUser().searchAtUser(key,
                getMaxid(), count, httpListener);
    }

    @Override
    public int getMaxid() {
        if (getLast() == null) {
            return 0;
        } else
            return getLast().getFollow_id();
    }

    @Override
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return (ListData<SociaxItem>) getApiUser().searchAtUser(key,
                getMaxid(), PAGE_COUNT, httpListener);
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
            viewHolder.tv_user_content.setText(getItem(position).getIntro());
            viewHolder.tv_user_add.setVisibility(View.GONE);
        } else if (type == 0) {
            holder.tv_empty_content.setText("您还有没伙伴可以@");
        }
        return convertView;
    }
}