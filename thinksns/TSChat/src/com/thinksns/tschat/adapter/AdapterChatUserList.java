package com.thinksns.tschat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksns.tschat.R;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.chat.ChatSocketClient;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.listener.ChatMembersInter;
import com.thinksns.tschat.listener.OnChatItemClickListener;
import com.thinksns.tschat.ui.ActivityChatInfo;
import com.thinksns.tschat.ui.ActivitySelectUser;
import com.thinksns.tschat.widget.UIImageLoader;
import com.thinksns.tschat.widget.roundimageview.RoundedImageView;

import java.util.List;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-28
 */
public class AdapterChatUserList extends BaseAdapter {
    private Context context;
    List<ModelUser> list;
    LayoutInflater inflater;
    private int room_id;
    private String type = "show";// 当前adapter类型：show展示群成员列表，delete，删除群成员

    private OnChatItemClickListener chatItemClickListener;

    /**
     * 当前adapter类型：show展示群成员列表，delete，删除群成员
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * 当前adapter类型：show展示群成员列表，delete，删除群成员
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param context
     * @param list
     * @param type    当前adapter类型：show展示群成员列表，delete，删除群成员
     * @param room_id 房间id
     */
    public AdapterChatUserList(Context context, List<ModelUser> list,
                               String type, int room_id) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.type = type;
        this.room_id = room_id;
    }

    public void setData(List<ModelUser> data) {
        if (list != null)
            list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnChatItemClickListener(OnChatItemClickListener chatItemClickListener) {
        this.chatItemClickListener = chatItemClickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ModelUser getItem(int position) {
        return (ModelUser) list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        RoundedImageView img_user_header;
        TextView tv_user_name;
        ImageView img_delete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_chat_info_user, null);
            holder.img_user_header = (RoundedImageView) convertView
                    .findViewById(R.id.img_header);
            holder.tv_user_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            holder.img_delete = (ImageView) convertView
                    .findViewById(R.id.img_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ModelUser user = getItem(position);
        if (user.getUid() == -1) {
            // 添加图标
            holder.img_delete.setVisibility(View.GONE);
            holder.img_user_header.setImageResource(R.drawable.tv_add_chat_user);
            holder.tv_user_name.setText("");
            holder.img_user_header.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //邀请新成员
                    if(context instanceof ChatMembersInter) {
                        ((ChatMembersInter)context).addMember(null);
                    }
                }
            });
        } else if (user.getUid() == -2) {
            // 删除图标
            holder.img_delete.setVisibility(View.GONE);
            holder.img_user_header.setImageResource(R.drawable.tv_delete_chat_user);
            holder.tv_user_name.setText("");
            holder.img_user_header.setEnabled(true);
            holder.img_user_header.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeAdapterType();
                }
            });
        } else {
            UIImageLoader.getInstance(parent.getContext()).displayImage(user.getUserface(), holder.img_user_header);
            holder.tv_user_name.setText(user.getUserName());
            if (type.equals("delete")) {
                holder.img_delete.setVisibility(View.VISIBLE);
            } else {
                holder.img_delete.setVisibility(View.GONE);
            }

            if (user.getUid() == TSChatManager.getLoginUser().getUid()) {
                //不能删除自己
                holder.img_delete.setVisibility(View.GONE);
                holder.img_user_header.setEnabled(false);
            }else {
                holder.img_user_header.setEnabled(true);
            }

            holder.img_user_header.setTag(R.id.tag_search_user, user);
            holder.img_user_header.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelUser user = (ModelUser)v.getTag(R.id.tag_search_user);
                    if (type.equals("show")) {
                        if (chatItemClickListener != null) {
                            int uid = user.getUid();
                            v.setTag(uid);
                            chatItemClickListener.onClickUserHead(v);
                        }
                    }else {
                        //删除成员，调用删除
                        if (context instanceof ChatMembersInter) {
                            ((ChatMembersInter) context).deleteMember(user);
                        }
                    }
                }
            });
        }
        return convertView;
    }

    /**
     * 修改adapter类型
     */
    protected void changeAdapterType() {
        if (type.equals("show")) {
            type = "delete";
        } else {
            type = "show";
        }
        this.notifyDataSetChanged();
    }
}
