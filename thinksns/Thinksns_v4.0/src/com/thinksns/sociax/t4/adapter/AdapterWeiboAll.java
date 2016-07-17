package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.data.AppendWeibo;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.component.HolderSociax;

import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * Created by hedong on 16/2/19.
 * 微博基类，默认使用全部微博，其他微博类型获取自己的API
 */
public class AdapterWeiboAll extends ListBaseAdapter<ModelWeibo> {
    protected AppendWeibo append;		// 数据映射
    protected ListView mListView;
    public AdapterWeiboAll(Context context, WeiboListViewClickListener listener, ListView listView) {
        super(context);
        this.mListView = listView;
        append = new AppendWeibo(context, this, listener);
    }

    @Override
    public int getItemForPosition(ModelWeibo obj) {
        int i = 0;
        for(; i<mDatas.size(); i++) {
            ModelWeibo item = (ModelWeibo) mDatas.get(i);
            if(item != null && item.getWeiboId() == obj.getWeiboId())
                return i;
        }
        if(i == mDatas.size())
            i = -1;
        return i;

    }

    @Override
    public int getMaxId() {
        if(mDatas.size() == 0)
            return 0;
        return ((ModelWeibo)mDatas.get(mDatas.size()-1)).getWeiboId();
    }

    @Override
    protected View getRealView(final int position, View convertView, ViewGroup parent) {
            HolderSociax holder = null;
            if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
                convertView = getLayoutInflater(mContext).inflate(R.layout.listitem_weibo_nobackground, null);
                holder = append.initHolder(convertView);
                convertView.setTag(R.id.tag_viewholder, holder);

            } else {
                holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
            }

            ModelWeibo modelWeibo = getItem(position);
            convertView.setTag(R.id.tag_weibo, modelWeibo);
            holder.ll_from_weibo_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListView.performItemClick(mListView, position, position);
                }
            });

            append.appendWeiboItemDataWithNoBackGround(position, holder, modelWeibo);

            return convertView;
    }
}
