package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api.Users;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelRankListItem;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明： 风云榜
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-13
 */
public class AdapterTopUserList extends AdapterSociaxList {
    private static final String SmartImageView = null;
    View header;
    ImageView img_my;
    TextView tv_myuname, tv_mydes;
    /**
     * 积分/勋章，1、2
     */
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 从fragment中生成
     *
     * @param fragment
     * @param list
     * @param header
     * @param type     1 积分列表，2 勋章列表
     */
    public AdapterTopUserList(FragmentSociax fragment,
                              ListData<SociaxItem> list, View header, int type) {
        super(fragment, list);
        this.fragment = fragment;
        this.header = header;
        this.type = type;
        img_my = (ImageView) header.findViewById(R.id.image_photo);
        tv_myuname = (TextView) header.findViewById(R.id.unnames);
        tv_mydes = (TextView) header.findViewById(R.id.uncontent);
        tv_mydes.setText("正在获取您的排名...");
    }

    public AdapterTopUserList(ThinksnsAbscractActivity context,
                              ListData<SociaxItem> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder;
        if (convertView == null) {
            viewHolder = new HolderSociax();
            convertView = inflater.inflate(R.layout.listitem_user_rankitem,
                    null);
            viewHolder.tv_user_photo = (ImageView) convertView
                    .findViewById(R.id.image_photo);
            viewHolder.tv_user_name = (TextView) convertView
                    .findViewById(R.id.unnames);
            viewHolder.tv_user_content = (TextView) convertView
                    .findViewById(R.id.uncontent);
            viewHolder.tv_user_add = (TextView) convertView
                    .findViewById(R.id.image_add);
            viewHolder.tv_rank = (TextView) convertView
                    .findViewById(R.id.tv_rank);
            convertView.setTag(R.id.tag_viewholder, viewHolder);
        } else {
            viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        if (position == 0) {
            tv_mydes.setText(getItem(position).getRankMy() + "");
        }

        convertView.setTag(R.id.tag_search_user, getItem(position));

        Glide.with(context).load(getItem(position).getUface())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(context))
                .crossFade()
                .into(viewHolder.tv_user_photo);

        viewHolder.tv_user_name.setText(getItem(position).getUname());
        if (type == 1) {
            viewHolder.tv_user_content.setText("积分："
                    + getItem(position).getExperience() + "  	分享："
                    + getItem(position).getFeed_count());
        } else {
            viewHolder.tv_user_content.setText("勋章："
                    + getItem(position).getMcount());

        }

        if (position < 3) {
            viewHolder.tv_rank.setTextColor(context.getResources().getColor(
                    R.color.title_blue));
        } else {
            viewHolder.tv_rank.setTextColor(context.getResources().getColor(
                    R.color.black));
        }
        viewHolder.tv_rank.setText(getItem(position).getRank() + "");

        return convertView;
    }

    @Override
    public int getMaxid() {
        return 0;
    }

    @Override
    public ModelRankListItem getLast() {
        if (list.size() > 0) {
            return (ModelRankListItem) this.list.get(this.list.size() - 1);
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
        return getApiUser().getUserTopList(type);
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return getApiUser().getUserTopList(type);
    }

    @Override
    public ModelRankListItem getItem(int position) {
        if (list.size() > position) {
            return (ModelRankListItem) this.list.get(position);
        } else {
            return null;
        }
    }

    Users getApiUser() {
        return thread.getApp().getUsers();
    }
}
