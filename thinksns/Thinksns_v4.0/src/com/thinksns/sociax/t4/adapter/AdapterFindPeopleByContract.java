package com.thinksns.sociax.t4.adapter;

import android.content.Intent;
import android.net.Uri;
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
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.function.FunctionChangeFollow;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-4
 */
public class AdapterFindPeopleByContract extends AdapterUserFollowingList {
    private ListData<SociaxItem> contactUsers;

    public AdapterFindPeopleByContract(FragmentSociax fragment,
                                       ListData<SociaxItem> list, int uid) {
        super(fragment, new ListData<SociaxItem>(), uid);
        this.contactUsers = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }


    @Override
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        String requestKey = "";
        for (int i = 0; i < contactUsers.size(); i++) {
            requestKey += ((ModelSearchUser) contactUsers.get(i)).getPhone() + ",";
        }

        requestKey.subSequence(0, requestKey.lastIndexOf(","));
        getApiUser().searchUserByContract(requestKey, httpListener);
        return null;
    }

    /**
     * 更新联系人数据，获取本地通讯录之后调用接口搜索，返回一个搜索结果列表，如果联系人也在Ts内，则显示成好友，否则显示成邀请 例如下面這个列表，
     * <p/>
     * array(2) { [0] => array(6) { ["tel"] => string(11) "18823013131" ["uid"] => string(5) "31918"
     * ["uname"] => string(6) "wztest" ["avatar"] => string(80) "http://tsimg.tsurl.cn/avatar/9d/bd/49/original.jpg!middle.avatar.jpg?v1410337191"
     * ["intro"] => string(0) "" ["follow_status"] => array(2) { ["following"] => int(0)
     * ["follower"] => int(0) } } [1] => array(2) { ["uid"] => int(0) ["tel"] => string(11)
     * "18862162339" } }
     */
    private ListData<SociaxItem> afterUpdateContract(ListData<SociaxItem> returnList) {
        ListData<SociaxItem> tempList = new ListData<SociaxItem>();
        ModelSearchUser tempInList, tempInreturn;
        //先列出已经注册的人
        for (int i = 0; i < returnList.size(); i++) {
            tempInreturn = ((ModelSearchUser) returnList.get(i));
            if (tempInreturn.getUid() != 0) {
                // 这部分人已经注册
                for (int j = 0; j < contactUsers.size(); j++) {
                    tempInList = ((ModelSearchUser) contactUsers.get(j));
                    if (tempInList.getPhone().equals(tempInreturn.getPhone())) {
                        tempInreturn.setIntro("用户名：" + tempInreturn.getUname());
                        tempInreturn.setUname(tempInList.getUname());
                        tempList.add(tempInreturn);
                        contactUsers.remove(j);
                    }
                }
            }
        }

        tempList.addAll(contactUsers);

        return tempList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderSociax viewHolder = null;
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

        ModelSearchUser user = getItem(position);
        if (user.getUserface() == null) {
            viewHolder.tv_user_photo.setImageResource(R.drawable.default_user);
        } else {
            Glide.with(context).load(user.getUserface())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideCircleTransform(context))
                    .crossFade()
                    .into(viewHolder.tv_user_photo);
        }

        viewHolder.tv_user_name.setText(user.getUname());
        viewHolder.tv_user_add.setVisibility(View.VISIBLE);
        viewHolder.tv_user_add.setTag(R.id.tag_position, position);
        viewHolder.tv_user_add.setTag(R.id.tag_follow, user);

        if (user.getFollowing() == null) {
            viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_green_digg);
            viewHolder.tv_user_add.setText("邀请");
            viewHolder.tv_user_add.setTag(R.id.tag_follow, user);
            viewHolder.tv_user_add.setTextColor(context.getResources().getColor(R.color.fav_border));
            convertView.setTag(R.id.tag_search_user, null);
            viewHolder.tv_user_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Uri smsToUri = Uri.parse("smsto:"
                            + ((ModelSearchUser) (v.getTag(R.id.tag_follow)))
                            .getPhone());

                    Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                    intent.putExtra("sms_body", context.getResources().getString(R.string.find_invite) + "http://" + Api.getHost());
                    inflater.getContext().startActivity(intent);
                }
            });

            viewHolder.tv_user_content.setVisibility(View.GONE);
        } else {
            if (user.getFollowing().equals("0")) {
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
                            context, AdapterFindPeopleByContract.this, v);
                    try {
                        fcChangeFollow.changeListFollow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            //显示用户名
            viewHolder.tv_user_content.setText(user.getIntro());
        }

        return convertView;
    }

    /**
     * 底部追加信息
     */

    public void addFooter(ListData<SociaxItem> list) {
        ListData<SociaxItem> result = afterUpdateContract(list);
        super.addFooter(result);
    }


    @Override
    public int getMaxid() {
        if (getLast() == null)
            return 0;
        else
            return getLast().getUid();
    }
}
