package com.thinksns.sociax.t4.android.fragment;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiWeiba;
import com.thinksns.sociax.db.PostSqlHelper;
import com.thinksns.sociax.gimgutil.AsyncTask;
import com.thinksns.sociax.modle.Contact;
import com.thinksns.sociax.t4.adapter.AdapterCommentList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Listener.onWebViewLoadListener;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.AppendPost;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.interfaces.OnPopupWindowClickListener;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowCommon;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowPostMore;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.android.weiba.ActivityPostCreat;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.*;
import com.thinksns.sociax.t4.unit.ButtonUtils;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.unit.SociaxUIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明： 需要传入intent ModelPost post或者post_id
 * 当传入post的时候，可能会因为parcle报空指针错误 String
 * weiba_name(帖子所在微吧名称，可选)
 *
 */
public class FragmentPostDetail extends FragmentSociax implements PullToRefreshBase.OnRefreshListener2<ListView> {
    public static final String DEL_COMMENT = "delete_comment";
    protected static final String TAG = "TSTAG_FragmentPostDetail";

    private ModelPost post;
    private int post_id = -1;
    private ImageView img_back;
    private ImageView iv_digg, img_face;
    private EditText et_comment;
    private ListFaceView list_face;
    private Button btn_send;
    private View header;
    private EmptyLayout emptyLayout;

    private Handler handler;
    private BroadcastReceiver mReceiver;
    private AppendPost append;
    private HolderSociax holder;
    private PullToRefreshListView pullToRefreshListView;
    private PopupWindowPostMore pup;

    @Override
    public void initView() {
        iv_digg = (ImageView) findViewById(R.id.iv_dig);
        et_comment = (EditText) findViewById(R.id.et_comment);
        list_face = (ListFaceView) findViewById(R.id.face_view);
        img_face = (ImageView) findViewById(R.id.img_face);
        btn_send = (Button) findViewById(R.id.btn_send_comment);

        list_face.initSmileView(et_comment);
        //设置下拉刷新组件
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        //设置列表样式
        listView = pullToRefreshListView.getRefreshableView();
        listView.setDividerHeight(UnitSociax.dip2px(getActivity(), 0.5f));
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_listview_divider)));
        //添加帖子详情头部视图
        header = inflater.inflate(R.layout.header_post_comment_list, null);
        initHeader(header);
        header.setVisibility(View.GONE);
        listView.addHeaderView(header);
        //空置页面
        emptyLayout = (EmptyLayout)findViewById(R.id.empty_layout);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case StaticInApp.POST_COMMENT:
                        et_comment.setTag(null);
                        et_comment.setHint("发表评论");
                        checkSendResult((ModelBackMessage) msg.obj);
                        break;
                    case StaticInApp.CHANGE_BUTTOM_POST_DETAIL_DIG:
                        boolean digState= (boolean) msg.obj;
                        showDiggUserUI(digState);
                        setButtomUI(digState);
                        break;
                    case StaticInApp.CHANGE_BUTTOM_POST_DETAIL_DIG_NOT_OK:
                        String error= (String) msg.obj;
                        Toast.makeText(getActivity().getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        initReceiver();
    }

    public ModelPost getPost() {
        return post;
    }

    /**
     * 显示网络请求结果
     *
     * @param backMsg
     */
    private void checkSendResult(ModelBackMessage backMsg) {
        boolean isSuccess;
        if (backMsg != null) {
            isSuccess = backMsg.getStatus() >= 1;
            if (isSuccess) {
                Toast.makeText(getActivity(), backMsg.getMsg(), Toast.LENGTH_SHORT).show();
                adapter.doUpdataList();
            }
        } else {
            Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化广播接受
     */
    private void initReceiver() {
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String action = arg1.getAction();
                if (action.equals(StaticInApp.CREATE_NEW_WEIBA_COMMENT)) {
                    //有新评论
                    if (adapter != null) {
                        adapter.doUpdataList();
                    }
                    int count = post.getReply_count() + 1;
                    post.setReply_count(count);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticInApp.CREATE_NEW_WEIBA_COMMENT);
        filter.addAction(DEL_COMMENT);
        getActivity().registerReceiver(mReceiver, filter);

    }

    /**
     * 帖子部分的内容 第一次从帖子列表传入post，不详细，没有赞和favourite信息，但是可以先预览
     */
    private void setHeaderContent(View convertView) {
        if(post == null)
            return;

        if (append == null || holder == null) {
            initHeader(convertView);
        }

        //设置点赞状态
        setButtomUI(post.isDigg());

        append.appendPostHeaderData(holder, post, new onWebViewLoadListener() {
            @Override
            public void onPageStarted() {
                //网页开始加载
                ((AdapterCommentList)adapter).setFeedId(post.getFeed_id());
                list = post.getCommentInfoList();
                if(list.size() == 0) {
                    ModelComment comment = new ModelComment();
                    list.add(comment);
                }

                ((AdapterCommentList) adapter).setData(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPageFinished() {
                //网页加载完毕
                header.setVisibility(View.VISIBLE);
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                adapter.doRefreshHeader();
            }
        });
    }

    private void initHeader(View convertView) {
        append = new AppendPost(getActivity());
        holder = append.initHolder(convertView);
    }

    /**
     * 赞和评论的状态
     */
    private void setButtomUI(boolean digState) {
        if (post == null) {
            return;
        }

        iv_digg.setImageDrawable(getResources().getDrawable(
                digState ? R.drawable.ic_share_detail_like_blue : R.drawable.ic_share_detail_like
        ));
//        iv_digg.setImageDrawable(getResources().getDrawable(
//                post.isDigg() ? R.drawable.ic_share_detail_like_blue : R.drawable.ic_share_detail_like
//        ));

    }


    @Override
    public void initIntentData() {
        if (getActivity().getIntent().hasExtra("post")) {// 如果传入post（置顶帖最好不要传入post，传post_id）
            post = (ModelPost) getActivity().getIntent().getSerializableExtra(
                    "post");
            post_id = post.getPost_id();
        } else if (getActivity().getIntent().hasExtra("post_id")) {
            // 如果传入的是post_id
            post_id = getActivity().getIntent().getIntExtra("post_id", -1);
        }
    }

    @Override
    public void initListener() {
        // listView 点击隐藏评论按钮
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (iv_digg.getVisibility() == View.GONE) {
                    iv_digg.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                }
                et_comment.clearFocus();
                et_comment.setTag(null);
                et_comment.setHint("发表评论");
                UnitSociax.hideSoftKeyboard(getActivity(), et_comment);
                return false;
            }
        });

        // 评论按钮
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
                SociaxUIUtils.hideSoftKeyboard(getActivity(), et_comment);
            }
        });

        // 输入框点击
        et_comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (iv_digg.getVisibility() == View.VISIBLE) {
                    iv_digg.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                }

                if (img_face.getVisibility() == View.GONE) {
                    img_face.setVisibility(View.VISIBLE);
                }
                if (list_face.getVisibility() == View.VISIBLE) {
                    list_face.setVisibility(View.GONE);
                }
                return false;
            }
        });

        // 输入框文本检测
        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btn_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundbackground_blue_chat_item));
                    btn_send.setClickable(true);
                } else {
                    btn_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundbackground_gray_chat_item));
                    btn_send.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //点击表情
        img_face.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_face.getVisibility() == View.VISIBLE) {
                    SociaxUIUtils.showSoftKeyborad(getActivity(), et_comment);
                    img_face.setImageResource(R.drawable.face_bar);
                    list_face.setVisibility(View.GONE);
                } else {
                    SociaxUIUtils.hideSoftKeyboard(getActivity(), et_comment);
                    img_face.setImageResource(R.drawable.key_bar);
                    list_face.setVisibility(View.VISIBLE);
                }
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ModelComment comment = (ModelComment) view.getTag(R.id.tag_object);
                if (comment == null)
                    return;
                if (Integer.parseInt(comment.getUid()) == Thinksns.getMy().getUid()) {
                    //删除自己的评论
                } else {
                    //回复别人评论
//                    showPop(comment, view);
                    et_comment.setTag(comment);
                    et_comment.setHint("回复" + comment.getUname() + ":");
                    et_comment.setFocusable(true);
                    et_comment.setFocusableInTouchMode(true);
                    et_comment.requestFocus();
                    btn_send.setVisibility(View.VISIBLE);
                    iv_digg.setVisibility(View.GONE);
                    if (list_face.getVisibility() == View.VISIBLE)
                        list_face.setVisibility(View.GONE);
                    UnitSociax.showSoftKeyborad(getActivity(), et_comment);
                }
            }
        });

        //加载网络内容出现错误，点击重试
        emptyLayout.setOnLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新加载帖子详情
                loadData();
            }
        });

        //对帖子点赞
        iv_digg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ButtonUtils.isFastDoubleClick())
                    return;
                changePostDigg();
            }
        });
    }

    //对帖子点赞
    private void changePostDigg() {
        final boolean isDigg = post.isDigg();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object result = new Api.WeibaApi().getChangePostDigg(post_id,
                            post.getWeiba_id(), post.getPost_uid(), isDigg ? "1" : "0");
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (!jsonObject.getString("status").equals("1")||jsonObject.getInt("status")==1) {
                        //还原点赞初始状态
                        post.setDigg(!isDigg);
//                        showDiggUserUI(true);
//                        post.setDigg(isDigg);
//                        showDiggUserUI(isDigg);

                        Message msg=new Message();
                        msg.what=StaticInApp.CHANGE_BUTTOM_POST_DETAIL_DIG;
                        msg.obj=post.isDigg();
                        handler.sendMessage(msg);

                    }else {
//                        post.setDigg(isDigg);
//                        showDiggUserUI(false);
                        Message msg=new Message();
                        msg.what=StaticInApp.CHANGE_BUTTOM_POST_DETAIL_DIG_NOT_OK;

                        String error=jsonObject.getString("msg");
                        if (error!=null&&!error.equals("null")&&!error.equals("")){
                            msg.obj=error;
                        }else {
                            msg.obj="操作失败";
                        }

                        handler.sendMessage(msg);
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
//        post.setDigg(!isDigg);
//        setButtomUI();
//        //改变点赞人头像
//        showDiggUserUI(!isDigg);
    }

    //添加点赞用户,通常是当前登录用户
    private void showDiggUserUI(final boolean isDigg) {
        if(holder == null)
            return;
//        if(holder == null
//                || getActivity() == null
//                || getActivity().isFinishing())
//            return;

//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                if(holder.ll_digg != null) {
                    ListData<ModelDiggUser> diggList = post.getDiggInfoList();
                    ModelDiggUser user = new ModelDiggUser();
                    user.setUid(Thinksns.getMy().getUid());
                    user.setAvatar(Thinksns.getMy().getUserface());

                    if(diggList == null)
                        diggList = new ListData<ModelDiggUser>();
                    if(!isDigg) {
                        diggList.remove(user);
                        post.setPraise(post.getPraise() - 1);
                    }else {
                        post.setPraise(post.getPraise() + 1);
                        diggList.add(0, user);
                    }

                    AppendPost.appendDiggUser(holder.ll_digg, diggList);
                    //更新点赞数目
                    TextView tv_digg_num = (TextView) holder.ll_digg.findViewById(R.id.tv_dig_num);
                    tv_digg_num.setText(String.valueOf(post.getPraise()));

                    if(diggList.size() == 0) {
                        holder.ll_digg.setVisibility(View.GONE);
                    }else {
                        holder.ll_digg.setVisibility(View.VISIBLE);
                    }
                }
//            }
//        });

    }

    /**
     * 提交评论
     */
    private void doSubmit() {
        final ModelComment comment = new ModelComment();
        if (et_comment.getTag() != null) {
            ModelComment replyComment = (ModelComment) et_comment.getTag();
            comment.setContent("回复@" + replyComment.getUname() + ":");
        }
        String content = comment.getContent() == null ? "" : comment.getContent();
        comment.setContent(content + et_comment.getText().toString().trim());
        comment.setComment_id(post_id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiWeiba api = new Api.WeibaApi();
                Message msg = handler.obtainMessage();
                try {
                    msg.what = StaticInApp.POST_COMMENT;
                    msg.obj = api.replyPost(comment);
                } catch (VerifyErrorException e) {
                    e.printStackTrace();
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (UpdateException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }).start();
        et_comment.setText(null);
    }

    @Override
    public void initData() {
        //读取本地帖子详情
        post = PostSqlHelper.getInstance(getActivity()).getPostInfo(post_id);
        if(list == null) {
            list = new ListData<SociaxItem>();
        }
        adapter = new AdapterCommentList(
                FragmentPostDetail.this, list, post_id);
        listView.setAdapter(adapter);
        loadData();
    }

    //加载网络内容
    private void loadData() {
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        if(post != null) {
            setHeaderContent(header);
        }
        //请求最新帖子内容
        getPostDetailTask();
    }

    /**
     * 获取详情线程
     */
    private void getPostDetailTask() {
        new PostDetailsAsyncTask().execute();
    }

    /**
     * 获取帖子详情异步任务类
     */
    class PostDetailsAsyncTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                Object result = new Api.WeibaApi().getPostDetail(post_id);
                return new JSONObject(result.toString());
            }catch(ApiException e) {
                e.printStackTrace();
            }catch(JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)  {
            try {
                if(jsonObject != null) {
                    post = new ModelPost(jsonObject);
                    //保存至本地
                    PostSqlHelper.getInstance(getActivity()).addPost(post);
                    executeDataSuccess(list);
                    setHeaderContent(header);
                }else if(post == null){
                    //本地没有帖子内容并且网络错误
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_postdetail;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        if (requestCode == 1) {
            //刷新评论
            adapter.doUpdataList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void showPop(final ModelComment comment, View v) {
        PopupWindowCommon pup = new PopupWindowCommon(getActivity(), v, "", "回复评论", "取消");
        pup.setOnPopupWindowClickListener(new OnPopupWindowClickListener() {
            @Override
            public void secondButtonClick() {

            }

            @Override
            public void firstButtonClick() {
                //回复他人的评论
                Bundle data = new Bundle();
                data.putInt("type", StaticInApp.WEIBA_COMMENT_REPLY);
                data.putInt("post_id", post.getPost_id());
                data.putInt("comment_id", comment.getComment_id());        //评论id
                data.putString("comment_user", comment.getUname());        //评论人姓名
                data.putSerializable("commentModel", comment);
                Intent intent = new Intent(getActivity(), ActivityPostCreat.class);
                intent.putExtras(data);
                startActivityForResult(intent, 1);
            }
        });
    }

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        if(adapter != null) {
            adapter.doRefreshHeader();
            refreshView.setEnabled(false);
        }
    }

    /**
     * 上拉加载更多
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if(adapter != null) {
            adapter.doRefreshFooter();
            refreshView.setEnabled(false);
        }
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullToRefreshListView;
    }

    @Override
    public EmptyLayout getEmptyLayout() {
        return emptyLayout;
    }

    @Override
    public void executeDataSuccess(ListData<SociaxItem> list) {
        if(list == null)
            list = new ListData<SociaxItem>();
        if(list.size() < AdapterSociaxList.PAGE_COUNT) {
            //当评论小于一页个数时禁止上拉加载更多
            if(adapter.getRefreshState() == AdapterSociaxList.REFRESH_FOOTER) {
                pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
}
