package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import com.thinksns.sociax.android.R;

import com.thinksns.sociax.t4.android.data.AppendPost;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelChannel;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * Created by hedong on 16/3/1.
 * 帖子列表适配器
 */
public class AdapterBasePostList extends ListBaseAdapter<ModelPost> {
    private AppendPost append;

    public AdapterBasePostList(Context context) {
        super(context);
        append = new AppendPost(context);
    }

    @Override
    protected View getRealView(final int position, View convertView, final ViewGroup parent) {
        HolderSociax holder = null;
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            LayoutInflater inflater = getLayoutInflater(mContext);
            convertView = inflater.inflate(R.layout.listitem_post, null);
            holder = append.initHolder(convertView);
            convertView.setTag(R.id.tag_viewholder, holder);
        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        append.appendPostListData(holder, getItem(position));
        /***点击帖子信息进入帖子详情***/
        holder.tv_post_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView)parent).performItemClick(v, position, position);
            }
        });
        return convertView;
    }
}
