package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.GlideRoundTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelInformationCateList;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * 类说明：分类列表页
 * Created by Zoey on 2016-04-27.
 */
public class AdapterInformation extends ListBaseAdapter<ModelInformationCateList> {


    public AdapterInformation(Context context) {
        super(context);
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder = null;
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            LayoutInflater inflater = getLayoutInflater(mContext);
            viewHolder = new HolderSociax();

            convertView = inflater.inflate(R.layout.item_information_list, null);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_subject = (TextView) convertView.findViewById(R.id.tv_subject);
            viewHolder.tv_abstract = (TextView) convertView.findViewById(R.id.tv_abstract);

            convertView.setTag(R.id.tag_viewholder, viewHolder);

        } else {
            viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        convertView.setTag(R.id.tag_information, getItem(position));

        ModelInformationCateList modelInformationCateList = getItem(position);

        Glide.with(mContext).load(modelInformationCateList.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bg_default_img_info)
                .transform(new GlideRoundTransform(mContext))
                .crossFade()
                .into(viewHolder.iv_icon);

        String subject = modelInformationCateList.getSubject();
        String abstracts = modelInformationCateList.getAbstracts();

        setTextCheck(viewHolder.tv_subject, subject);
        setTextCheck(viewHolder.tv_abstract, abstracts);

        return convertView;
    }

    public void setTextCheck(TextView tv, String str) {
        if (str != null && !str.equals("null") && !str.equals("")) {
            tv.setText(str);
        } else {
            tv.setText("");
        }
    }
}
