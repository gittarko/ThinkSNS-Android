package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.interfaces.OnTabListener;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.presenter.WeiboListListPresenter;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.eventbus.WeiboEvent;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.ButtonUtils;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.unit.SociaxUIUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hedong on 16/2/19.
 * 微博列表基类
 * 包含常用微博操作：请求微博列表/关注/评论/收藏/删除/举报
 *
 */
public abstract class FragmentWeiboListViewNew extends BaseListFragment<ModelWeibo> implements WeiboListViewClickListener,
        OnTabListener, AdapterView.OnItemLongClickListener {
    protected RelativeLayout rl_comment;        // 评论列表
    protected EditText et_comment;              // 评论输入框
    protected Button btn_send;                  // 评论发送按钮
    protected RelativeLayout rl_more;           // 用来隐藏more popwindow
    protected ImageView img_face;               // 评论表情按钮
    protected ListFaceView list_face;           // 表情框

    protected ModelWeibo selectWeibo;
    protected ModelComment replyComment;
    protected int replyCommentId = 0;
    protected BroadcastReceiver updateWeibo;
    private int selectpostion = -1;
    protected IntentFilter intentFilter;
    //是否用于首页的微博列表;首页的微博列表包含一些特殊的事件处理，别的地方可以不使用
    protected boolean isInHome = true;
//    public EventBus eventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //根据微博ID更新微博的评论列表
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateWeiboEvent(WeiboEvent event) {
        int position = event.position;
        if (mAdapter.getData().size() > position) {
            mAdapter.setItem(position, event.weibo);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 用于接收改变了微博列表数据的广播
     */

    protected void initReceiver() {
        updateWeibo = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(StaticInApp.NOTIFY_WEIBO)
                        || action.equals(StaticInApp.NOTIFY_CREATE_WEIBO)) {
                    //请求网络数据
                    mPresenter.loadNetData();
                    //列表滑动至顶部
                    mListView.setSelection(0);
                    //重新加载列表内容
                    mPresenter.setMaxId(0);
                }else if(action.equals(StaticInApp.UPDATE_SINGLE_WEIBO)) {
                    //更新单条微博
                    ModelWeibo weibo = (ModelWeibo)intent.getSerializableExtra("weibo");
                    int type = intent.getIntExtra("type", 0);
                    if(weibo != null) {
                        switch (type) {
                            case 0:
                                break;
                            default:
                                //更新微博内容
                                for (int i = 0; i < mAdapter.getDataSize(); i++) {
                                    if (mAdapter.getItem(i).equals(weibo)) {
                                        mAdapter.setItem(i, weibo);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                        }

                    }
                }else if(action.equals(StaticInApp.NOTIFY_FOLLOW_USER)) {
                    int uid = intent.getIntExtra("uid", 0);
                    int follow = intent.getIntExtra("follow", 0);
                    if(uid > 0) {
                        updateUserFollow(uid, follow);
                    }
                }
            }
        };

        intentFilter = getIntentFilter();
        if (intentFilter != null) {
            getActivity().registerReceiver(updateWeibo, intentFilter);
        }

    }

    /**
     * 刷新关注、取消关注用户的状态
     * @param following
     */
    private void updateUserFollow(int uid, int following) {
        for (int i = 0; i < mAdapter.getDataSize(); i++) {
            if (mAdapter.getItem(i).getUid() == uid) {
                mAdapter.getItem(i).setFollowing(following);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    protected IntentFilter getIntentFilter() {
        IntentFilter filter_update_weibo = new IntentFilter();
        filter_update_weibo.addAction(StaticInApp.NOTIFY_WEIBO);
        return filter_update_weibo;
    }


    @Override
    protected ListBaseAdapter<ModelWeibo> getListAdapter() {
        return null;
    }
    protected String getCacheKey() {
        return "weibo";
    }

    @Override
    protected void initPresenter() {
        mPresenter = new WeiboListListPresenter(getActivity(), this, this);
        mPresenter.setCacheKey(getCacheKey());
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        rl_more = (RelativeLayout) view.findViewById(R.id.rl_more);
        rl_comment = (RelativeLayout) getActivity().findViewById(R.id.ll_send_comment);

        et_comment = (EditText) getActivity().findViewById(R.id.et_comment);
        et_comment.setHint("输入评论...");

        btn_send = (Button) getActivity().findViewById(R.id.btn_send_comment);
        img_face = (ImageView) getActivity().findViewById(R.id.img_face);
        list_face = (ListFaceView) getActivity().findViewById(R.id.face_view);
        list_face.initSmileView(et_comment);
        //注册监听事件
        img_face.setOnClickListener(this);
        et_comment.setOnClickListener(this);
        //点击评论框外部隐藏评论狂
        mListView.setOnTouchListener(commentBoxTouch);

    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setOnItemLongClickListener(this);
    }

    private View.OnTouchListener commentBoxTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            resetComentUI();
            return false;
        }
    };

    @Override
    protected void initListener() {
        if(isInHome) {
            mListView.setOnScrollListener(this);
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver();
        super.onDestroy();
    }

    //取消注册广播
    protected void unregisterReceiver() {
        try {
            if (intentFilter != null) {
                getActivity().unregisterReceiver(updateWeibo);
            }
        }catch (Exception e) {
            //捕获没有注册广播的情况
            e.printStackTrace();
        }
    }


    /**
     * 设置评论框可见
     */
    public void setCommentVisible() {
        rl_comment.setVisibility(View.VISIBLE);
        // 隐藏底部栏
        View bottom = getActivity().findViewById(R.id.rg_bottom);
        if (bottom != null)
            bottom.setVisibility(View.GONE);
        if(replyComment != null) {
            et_comment.setHint("回复" + replyComment.getToName() + ":");
        }
        et_comment.requestFocus();
        //延迟显示输入法键盘
        rl_comment.postDelayed(new Runnable() {
            @Override
            public void run() {
                SociaxUIUtils.showSoftKeyborad(getActivity(), et_comment);
            }
        }, 100);

    }

    //重置编辑框UI为初始状态
    protected void resetComentUI() {
        SociaxUIUtils.hideSoftKeyboard(getActivity(), et_comment);
        rl_comment.postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_comment.setVisibility(View.GONE);
                // 隐藏底部栏
                View bottom = getActivity().findViewById(R.id.rg_bottom);
                if (bottom != null)
                    bottom.setVisibility(View.VISIBLE);
                //保留编辑框的输入
//                et_comment.setHint("请输入评论...");
//                et_comment.setText("");
                et_comment.clearFocus();
                //将数据恢复到初始状态
                selectpostion = -1;
                if(selectWeibo != null)
                    selectWeibo = null;
                if(replyComment != null)
                    replyComment = null;

                img_face.setImageResource(R.drawable.face_bar);
                list_face.setVisibility(View.GONE);
            }
        }, 500);


    }


    @Override
    public void onDigWeibo(int i) {

    }

    @Override
    public void onCommentWeibo(final ModelWeibo weibo, final ModelComment comment) {
        if (weibo == null || weibo.getCommentList() == null) {
            return;
        }

        selectWeibo = weibo;
        if(!selectWeibo.isCan_comment()) {
            Toast.makeText(getActivity(), "您没有权限评论TA的分享", Toast.LENGTH_LONG).show();
            return;
        }

        if(comment != null && comment.getToName() != null) {
            replyComment = comment;
            getItemOptions(comment);
        }else {
            btn_send.setOnClickListener(this);
            //显示评论框
            setCommentVisible();
        }
    }

    //获取菜单选项
    private void getItemOptions(final ModelComment comment) {
        final boolean isMy = Integer.parseInt(comment.getUid()) == Thinksns.getMy().getUid();
        replyComment = comment;
        List<String> datas = new ArrayList<String>();
        if(isMy) {
            datas.add("删除");
        }else {
            datas.add("评论");
        }

        datas.add("复制");
        datas.add("取消");

        createOptionsMenu(datas);

    }

    PopupWindowListDialog.Builder builder = null;
    //创建菜单项
    private void createOptionsMenu(List<String> datas) {
        builder = new PopupWindowListDialog.Builder(getActivity());
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    if(Integer.parseInt(replyComment.getUid()) == Thinksns.getMy().getUid()) {
                        //删除评论
                        ((WeiboListListPresenter)mPresenter).deleteWeiboComment(replyComment);
                    }else {
                        //回复别人的评论
                        replyUser(replyComment);
                    }
                }else if(position == 1) {
                    //复制评论
                    UnitSociax.copy(replyComment.getContent(), getActivity());
                }

                builder.dimss();
            }
        });

        builder.create(datas);
    }

    private void replyUser(ModelComment replyComment) {
        btn_send.setOnClickListener(this);
        setCommentVisible();
    }

    //微博更多操作
    @Override
    public void onWeiboMoreClick(int i) {
        selectWeibo = mAdapter.getItem(i);
        ((WeiboListListPresenter)mPresenter).doWeiboMore(selectWeibo).
                showBottom(rl_comment);;
    }


    @Override
    public void onDeleteWeiboStatus(int type) {
        if(type == 1) {
            mAdapter.removeItem(selectWeibo);
            mPresenter.saveCaCheData(mAdapter.getData());
        }
    }

    @Override
    public void onDeleteWeiboComment(final int status) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(status == 1) {
                    selectWeibo.getCommentList().remove(replyComment);
                    mAdapter.notifyDataSetChanged();
                    mPresenter.saveCaCheData(mAdapter.getData());
                }
            }
        });

    }

    @Override
    public void onCollectWeiboStatus(int type) {
        if(type == 1) {
            refreshData();
        }
    }


    @Override
    public void onFollowWeibo(final ModelWeibo weibo) {
        selectWeibo = weibo;
        ((WeiboListListPresenter)mPresenter).followWeibo(weibo);
    }

    @Override
    public void onFollowWeiboStatus(int type) {
        if(type == 1) {
            setWeiboFollowById(selectWeibo.getUid());
            mAdapter.notifyDataSetChanged();
            mPresenter.saveCaCheData(mAdapter.getData());
        }else {
            //关注失败
        }
    }

    //将此人的状态都更改为已关注
    private void setWeiboFollowById(int uid) {
        ListData<ModelWeibo> list = mAdapter.getData();
        int size = list.size();
        for(int i=0; i < size; i++) {
            ModelWeibo weibo = (ModelWeibo)list.get(i);
            if(weibo.getUid() != uid)
                continue;
            weibo.setFollowing(1);
            mAdapter.setItem(i, weibo);
        }
    }

    @Override
    public void onDigWeiboStatus(int type) {

    }

    /**
     * 更新评论状态
     * @param type 1 成功 0 失败
     */
    @Override
    public void onCommentWeiboStatus(final int type) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshComment(type);
            }
        });
    }

    //刷新微博列表评论内容
    private void refreshComment(int type) {
        if (type == 1) {
            Toast.makeText(getActivity(), "评论成功", Toast.LENGTH_SHORT)
                    .show();
            //清空评论框的内容
            et_comment.setText("");
            resetComentUI();
            //更新缓存
            refreshData();
        } else {
            Toast.makeText(getActivity(), "评论失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //刷新列表并更新本地缓存
    public void refreshData() {
        selectpostion = mAdapter.getItemForPosition(selectWeibo);
        if(selectpostion != -1) {
            mAdapter.setItem(selectpostion, selectWeibo);
            mAdapter.notifyDataSetChanged();
            mPresenter.saveCaCheData(mAdapter.getData());
        }
    }

    int firstVisibleItem = 0;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (scrollState != SCROLL_STATE_IDLE) {
            return;
        }

        if (firstVisibleItem <= 1)
            FragmentHome.getInstance().animatorShow(true);
        else {
            FragmentHome.getInstance().animatorHide();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        this.firstVisibleItem = firstVisibleItem;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(id == -1) {
            return;
        }

        ModelWeibo weibo = mAdapter.getItem((int)id);
        if(weibo != null) {
            Intent intent = new Intent(getActivity(), ActivityWeiboDetail.class);
            Bundle data = new Bundle();
//
            data.putSerializable("weibo", weibo);
            intent.putExtras(data);
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
        //长按复制内容
        builder = new PopupWindowListDialog.Builder(getActivity());
        builder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ModelWeibo weibo = (ModelWeibo)view.getTag(R.id.tag_weibo);
                if(weibo == null)
                    return;
                if(position == 0) {
                    //复制评论
                    UnitSociax.copy(weibo.getContent(), getActivity());
                }

                builder.dimss();
            }
        });

        List<String> datas = new ArrayList<String>();
        datas.add("复制");
        builder.create(datas);
        return true;

    }

    /**
     *将表情字符映射到编辑框中
     */
    protected ListFaceView.FaceAdapter mFaceAdapter = new ListFaceView.FaceAdapter() {

        @Override
        public void doAction(int paramInt, String paramString) {
            EditText localEditDiggView = et_comment;
            int i = localEditDiggView.getSelectionStart();
            int j = localEditDiggView.getSelectionStart();
            String str1 = "[" + paramString + "]";
            String str2 = localEditDiggView.getText().toString();
            SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
            localSpannableStringBuilder.append(str2, 0, i);
            localSpannableStringBuilder.append(str1);
            localSpannableStringBuilder.append(str2, j, str2.length());
            UnitSociax.showContentFaceView(getActivity(),
                    localSpannableStringBuilder);
            localEditDiggView.setText(localSpannableStringBuilder,
                    TextView.BufferType.SPANNABLE);
            localEditDiggView.setSelection(i + str1.length());
            Log.v("Tag", localEditDiggView.getText().toString());
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_send_comment)
        {
            if (ButtonUtils.isFastDoubleClick()) {
                return;
            }

            list_face.setVisibility(View.GONE);
            String content = et_comment.getText().toString().trim();
            if (content.length() == 0) {
                Toast.makeText(getActivity(), "评论不能为空", Toast.LENGTH_SHORT).show();
            } else {
                ((WeiboListListPresenter)mPresenter).commentWeibo(selectWeibo, content, replyComment);
            }
        }else if(id == R.id.img_face) {
            if (list_face.getVisibility() == View.VISIBLE) {
                SociaxUIUtils.showSoftKeyborad(getActivity(), et_comment);
                img_face.setImageResource(R.drawable.face_bar);
                list_face.setVisibility(View.GONE);
            } else {
                SociaxUIUtils.hideSoftKeyboard(getActivity(), et_comment);
                img_face.setImageResource(R.drawable.key_bar);
                list_face.setVisibility(View.VISIBLE);
            }
        }else if(id == R.id.et_comment) {
            img_face.setImageResource(R.drawable.face_bar);
            list_face.setVisibility(View.GONE);
        }

        super.onClick(v);
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelWeibo> data) {
        //设置缺省文字提示
        mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));
        super.onLoadDataSuccess(data);
    }

    @Override
    public void onRefresh() {
        mPresenter.requestData(false);
    }
}
