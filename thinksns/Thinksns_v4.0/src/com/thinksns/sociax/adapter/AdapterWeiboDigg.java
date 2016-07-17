package com.thinksns.sociax.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelDiggUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

import org.json.JSONArray;

public class AdapterWeiboDigg extends AdapterSociaxList {

    private int weibo_id;
    private int maxId = 0;

    public AdapterWeiboDigg(FragmentSociax fragment, ListData<SociaxItem> list, int weibo_id) {
        super(fragment, list);
        this.weibo_id = weibo_id;
    }

    @Override
    public int getMaxid() {
        return 0;
    }

    @Override
    public ModelDiggUser getItem(int position) {
        return (ModelDiggUser) list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder;
        if (convertView == null) {
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
        convertView.setTag(R.id.tag_search_user, getItem(position));
        Glide.with(context).load(getItem(position).getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(context))
                .crossFade()
                .into(viewHolder.tv_user_photo);

        viewHolder.tv_user_name.setText(getItem(position).getUname());
        if (getItem(position).getIntro().isEmpty() || getItem(position).getIntro().equals("null")) {
            viewHolder.tv_user_content.setText("这家伙很懒，什么也没留下");
        } else {
            viewHolder.tv_user_content.setText(getItem(position).getIntro());
        }
        viewHolder.tv_user_add.setVisibility(View.VISIBLE);
        viewHolder.tv_user_add.setTag(R.id.tag_position, position);
        viewHolder.tv_user_add.setTag(R.id.tag_follow, getItem(position));

        if (getItem(position).getUid() != Thinksns.getMy().getUid()) {
            viewHolder.tv_user_add.setVisibility(View.VISIBLE);
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
        } else {
            viewHolder.tv_user_add.setVisibility(View.GONE);
        }

        viewHolder.tv_user_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setClickable(false);
                FunctionChangeFollow fcChangeFollow = new FunctionChangeFollow(context, AdapterWeiboDigg.this, v);
                fcChangeFollow.changeListFollow();
            }
        });

        return convertView;
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
        getDiggList();
        return null;
    }

    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        getDiggList();
        return null;
    }

    private void getDiggList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thinksns app = (Thinksns) context.getApplicationContext();
                try {
                    String result = app.getStatuses().getDiggList(weibo_id, maxId)
                            .toString();
                    JSONArray result2json = new JSONArray(result);
                    ListData<SociaxItem> list = new ListData<SociaxItem>();
                    if (result2json.length() != 0) {
                        for (int i = 0; i < result2json.length(); i++) {
                            ModelDiggUser modelDiggUser = new ModelDiggUser(result2json
                                    .getJSONObject(i));
                            list.add(modelDiggUser);
                            if (i == result2json.length() - 1) {
                                maxId = modelDiggUser.getId();
                            }
                        }
                    }
                    httpListener.onSuccess(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
