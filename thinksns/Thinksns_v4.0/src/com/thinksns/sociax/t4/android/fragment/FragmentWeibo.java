package com.thinksns.sociax.t4.android.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.android.img.UIImageLoader;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowWeiboMore;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.ButtonUtils;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.activity.widget.ListFaceView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.unit.SociaxUIUtils;

import org.json.JSONException;

import java.util.List;

/**
 * 类说明：微博类型的fragment的基类，默认是获取好友的微博 新的微博只要重写 initView中的adapter类型以及设定对应的列表即可 例如
 * FragmentRecommendWeibo，FragmentAtMeWeibo等
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-15
 */
public class FragmentWeibo extends FragmentSociax implements OnRefreshListener2<ListView> {
    protected RelativeLayout rl_comment;// 评论列表
    protected EditText et_comment;// 评论输入框
    protected Button btn_send;// 评论发送按钮
    protected ModelWeibo selectWeibo;
    protected ModelComment replyComment;

    protected ImageView img_face;// 评论表情按钮
    protected ListFaceView list_face;// 表情框
    protected PullToRefreshListView pullRefresh;

    protected int selectpostion;
    public ListHandler mHandler;
    protected RelativeLayout rl_more;// 用来隐藏more popwindow
    protected BroadcastReceiver updateWeibo;
    protected static final int PAGE_COUNT = 20;

    @Override
    public void initView() {
        pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullRefresh.setMode(Mode.BOTH);
        listView = pullRefresh.getRefreshableView();
        listView.setDivider(new ColorDrawable(0xffdddddd));
        listView.setDividerHeight(1);

        adapter = createAdapter();
        if (adapter != null)
            listView.setAdapter(adapter);

        rl_more = (RelativeLayout) findViewById(R.id.rl_more);
        mHandler = new ListHandler();
        rl_comment = (RelativeLayout) getActivity().findViewById(R.id.ll_send_comment);
        et_comment = (EditText) getActivity().findViewById(R.id.et_comment);
        btn_send = (Button) getActivity().findViewById(R.id.btn_send_comment);
        img_face = (ImageView) getActivity().findViewById(R.id.img_face);
        list_face = (ListFaceView) getActivity().findViewById(R.id.face_view);
        //初始化表情布局
        list_face.initSmileView(et_comment);
        initReceiver();
    }

    @Override
    public View getDefaultView() {
        return findViewById(R.id.default_share_bg);
    }

    public void initReceiver() {
        updateWeibo = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(StaticInApp.NOTIFY_WEIBO)) {
                    adapter.doRefreshHeader();
                }else if (action.equals(StaticInApp.UPDATA_WEIBA)) {
                    if (adapter != null) {
                        adapter.doRefreshHeader();
                    }
                }
            }
        };

        IntentFilter filter_update_weibo = new IntentFilter();
        filter_update_weibo.addAction(StaticInApp.NOTIFY_WEIBO);
        filter_update_weibo.addAction(StaticInApp.UPDATA_WEIBA);
        getActivity().registerReceiver(updateWeibo, filter_update_weibo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateWeibo != null) {
            getActivity().unregisterReceiver(updateWeibo);
        }
    }

    @Override
    public AdapterSociaxList createAdapter() {
        //获取最新的10条数据
        list = DbHelperManager.getInstance(getActivity(), ListData.DataType.FRIENDS_WEIBO).getHeaderData(10);
        return new AdapterWeiboList(this, list, Thinksns.getMy().getUid());
    }

    @Override
    public void initIntentData() {

    }

    @Override
    public void initListener() {
        pullRefresh.setOnRefreshListener(this);

        pullRefresh.setOnScrollListener(new ListScrollListener(UIImageLoader.getInstance(getActivity()).getImageLoader(),
                true, true));
    }

    final class ListScrollListener extends PauseOnScrollListener {
        int firstVisibleItem = 0;

        public ListScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super(imageLoader, pauseOnScroll, pauseOnFling);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            this.firstVisibleItem = firstVisibleItem;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            super.onScrollStateChanged(view, scrollState);
            if (scrollState == SCROLL_STATE_IDLE) {
                if (firstVisibleItem <= 1)
                    FragmentHome.getInstance().animatorShow(true);
                else {
                    FragmentHome.getInstance().animatorHide();
                }
            }
        }


    }

    @Override
    public void initData() {
        if (adapter != null) {
//			adapter.loadInitData();
        }
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullRefresh;
    }

    /**
     * 点击某个微博的评论
     */
    public void clickComment(int i) {
        selectWeibo = (ModelWeibo) list.get(i);
        if (selectWeibo == null) {
            return;
        }
        if (selectWeibo.isCan_comment()) {
            setCommentVisible();
            selectpostion = i;
            btn_send.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (ButtonUtils.isFastDoubleClick()) {
                        return;
                    }
                    img_face.setImageResource(R.drawable.key_bar);
                    list_face.setVisibility(View.GONE);
                    String content = et_comment.getText().toString().trim();
                    if (content.length() == 0) {
//						et_comment.setError("评论不能为空");
                        Toast.makeText(getActivity(), "评论不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        final Comment comment = new Comment();
                        comment.setContent(content);
                        comment.setStatus(selectWeibo);
                        comment.setUname(Thinksns.getMy().getUserName());
                        mHandler = new ListHandler();
                        if (selectWeibo.getComments() == null) {
                            Toast.makeText(getActivity(), "评论失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectWeibo.getComments().add(0, comment);
                        selectWeibo.setCommentCount(selectWeibo
                                .getCommentCount() + 1);
                        final Object obj[] = new Object[]{selectWeibo,
                                rl_comment, et_comment, selectpostion};
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                Message msg = mHandler.obtainMessage();
                                try {
                                    msg.what = AppConstant.COMMENT;
                                    msg.obj = obj;
                                    msg.arg1 = new Api.StatusesApi()
                                            .comment(comment);
                                } catch (VerifyErrorException e) {
                                    e.printStackTrace();
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                } catch (UpdateException e) {
                                    e.printStackTrace();
                                } catch (DataInvalidException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mHandler.sendMessage(msg);
                            }
                        }).start();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "您没有权限评论TA的分享", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 设置评论框可见
     */
    public void setCommentVisible() {
        if (rl_comment != null) {
            rl_comment.setVisibility(View.VISIBLE);
            rl_comment.setFocusable(true);
            // 隐藏底部栏
            View bottom = getActivity().findViewById(R.id.rg_bottom);
            if (bottom != null)
                bottom.setVisibility(View.GONE);
        }
        et_comment.setFocusable(true);
        et_comment.setClickable(true);
        et_comment.setSelected(true);
        et_comment.setFocusableInTouchMode(true);
        et_comment.requestFocus();
        et_comment.requestFocusFromTouch();
        et_comment.setText("");
        SociaxUIUtils.showSoftKeyborad(getActivity(), et_comment);
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
        et_comment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                img_face.setImageResource(R.drawable.face_bar);
                list_face.setVisibility(View.GONE);
            }
        });
        rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                rl_comment.setVisibility(View.GONE);
                // 显示底部栏
                View bottom = getActivity().findViewById(R.id.rg_bottom);
                if (bottom != null) {
                    bottom.setVisibility(View.VISIBLE);
                }
                img_face.setImageResource(R.drawable.face_bar);
                list_face.setVisibility(View.GONE);
                SociaxUIUtils.hideSoftKeyboard(getActivity(), et_comment);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    public class ListHandler extends Handler {

        public ListHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object digMsg[] = null;
            if (msg.obj instanceof Object[]) {
                digMsg = (Object[]) msg.obj;
            }
            Thinksns app = (Thinksns) getActivity().getApplicationContext();
            switch (msg.what) {
                case AppConstant.COMMENT:
                    int result = msg.arg1;
                    if (result == 1) {
                        Toast.makeText(getActivity(), "评论成功", Toast.LENGTH_SHORT)
                                .show();
                        Object obj[] = (Object[]) msg.obj;
                        ModelWeibo weibo = (ModelWeibo) obj[0];
                        RelativeLayout rl_comment = (RelativeLayout) obj[1];
                        EditText et_comment = (EditText) obj[2];
                        int position = (Integer) obj[3];
                        updateComment4Weibo(weibo, position);
                        rl_comment.setVisibility(View.GONE);
                        // 显示底部栏
                        View view = getActivity().findViewById(R.id.rg_bottom);
                        if (view != null)
                            view.setVisibility(View.VISIBLE);
                        SociaxUIUtils.hideSoftKeyboard(getActivity(), et_comment);
                    } else {
                        Toast.makeText(getActivity(), "评论失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        }
    }

    public void updateComment4Weibo(ModelWeibo weibo, int position) {
        this.list.set(position, weibo);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 点击更多之后的操作
     */
    public void clickMore(int i) {
        selectWeibo = (ModelWeibo) list.get(i);
        selectpostion = i;
        final PopupWindowWeiboMore popup = new PopupWindowWeiboMore(
                (FragmentSociax) this, selectWeibo, selectpostion,
                (AdapterWeiboList) adapter);
        popup.showBottom(rl_comment);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_common_weibolist;
    }


    /**
     * 回调方法，获取是否已经加载过
     */
    protected boolean getFirstLoad() {
        return false;
    }

    /**
     * 回调方法，设置已经回调过
     */
    protected void onFinishLoad(boolean finish) {

    }

    /**
     * 从网络加载数据
     */
    protected void loadRemoteData() {
        if (getFirstLoad()) {
            loadData();
            onFinishLoad(true);
        }
    }

    /**
     * 第一次进入使用loadInitData 以后进入使用pullRefresh
     */
    protected void loadData() {
        if (adapter != null && adapter.getCount() == 0) {
            //没有数据从网路获取
            adapter.loadInitData();
            //更新列表显示
            adapter.notifyDataSetChanged();
        } else {
            if (pullRefresh != null) {
                pullRefresh.setRefreshing();
            }
            onFinishLoad(true);
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

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        //下拉刷新
        if (adapter != null)
            adapter.doRefreshHeader();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (adapter != null) {
            adapter.doRefreshFooter();
        }
    }
}
