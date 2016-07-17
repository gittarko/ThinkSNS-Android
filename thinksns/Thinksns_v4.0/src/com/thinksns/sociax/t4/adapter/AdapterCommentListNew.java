package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.data.AppendComment;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * Created by hedong on 16/2/23.
 * 评论列表适配器
 */
public class AdapterCommentListNew extends ListBaseAdapter<ModelComment> {
    protected  int feed_id;
    protected AppendComment append;

    public AdapterCommentListNew(Context context) {
        super(context);
        this.feed_id=feed_id;
        this.append = new AppendComment(context);
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        HolderSociax holder = null;
        int type = getItemViewType(position);
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            LayoutInflater inflater = getLayoutInflater(mContext);
            if (type == 1) {
                convertView = inflater.inflate(R.layout.listitem_comment_list, null);
                holder = append.initHolder(convertView);
            } else if (type == 0) {
                convertView = inflater.inflate(R.layout.default_no_comment_bg, null);
            } else if (type == 2) {
                //加载正在加载数据的界面
//                convertView = inflater.inflate(R.layout.loading, null);
//                PullToRefreshListView listView = getPullRefreshView();
//                int width = listView.getWidth();
//                int height = listView.getHeight() - 100;
//                AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, height);
//                convertView.setLayoutParams(params);
            }

            convertView.setTag(R.id.tag_viewholder, holder);

        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        if (type == 1) {
            convertView.setTag(R.id.tag_object, getItem(position));
            append.appendCommentData(holder, getItem(position));
        }

        return convertView;
    }


}
