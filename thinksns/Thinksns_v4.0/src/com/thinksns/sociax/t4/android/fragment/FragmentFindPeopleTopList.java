package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterTopUserList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.model.ModelRankListItem;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：风云榜
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-13
 */
public class FragmentFindPeopleTopList extends FragmentSociax implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private TextView tv_score, tv_medal;
    private LinearLayout ll_manage;
    private View header;
    private ImageView img_my;
    private TextView tv_myuname;

    private PullToRefreshListView pullToRefreshListView;

    @Override
    public void initView() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_medal = (TextView) findViewById(R.id.tv_medal);
        ll_manage = (LinearLayout) findViewById(R.id.ll_manage);
        header = inflater.inflate(R.layout.listitem_own_rankitem, null);
        img_my = (ImageView) header.findViewById(R.id.image_photo);
        tv_myuname = (TextView) header.findViewById(R.id.unnames);

        listView = pullToRefreshListView.getRefreshableView();
        list = new ListData<SociaxItem>();
        adapter = new AdapterTopUserList(this, list, header, 1);
        listView.setDivider(new ColorDrawable(0xffdddddd));
        listView.setDividerHeight(1);
        listView.setAdapter(adapter);
        listView.addHeaderView(header);
    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
        pullToRefreshListView.setOnRefreshListener(this);
        img_my.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入个人主页
                Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
                intent.putExtra("uid", Thinksns.getMy().getUid());
                startActivity(intent);
            }
        });
        tv_score.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ll_manage.setBackgroundResource(R.drawable.bg_top_score);
                tv_score.setTextColor(getResources().getColor(R.color.white));
                tv_medal.setTextColor(getResources().getColor(R.color.ge_line));
                if (adapter != null) {
                    ((AdapterTopUserList) adapter).setType(1);
                    adapter.doUpdataList();
                }
            }
        });
        tv_medal.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ll_manage.setBackgroundResource(R.drawable.bg_top_medal);
                tv_medal.setTextColor(getResources().getColor(R.color.white));
                tv_score.setTextColor(getResources().getColor(R.color.ge_line));
                if (adapter != null) {
                    ((AdapterTopUserList) adapter).setType(2);
                    adapter.doUpdataList();
                }
            }
        });
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelRankListItem rank = (ModelRankListItem)adapter.getItem((int)id);
                if(rank != null) {
                    Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
                    intent.putExtra("uid", rank.getUid());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void initData() {
        Glide.with(getActivity()).load(Thinksns.getMy().getUserface())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(getActivity()))
                .crossFade()
                .into(img_my);

        tv_myuname.setText(Thinksns.getMy().getUserName());

        adapter.doUpdataList();
    }

    @Override
    public PullToRefreshListView getPullRefreshView() {
        return pullToRefreshListView;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user_rank;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        if (adapter != null) {
            adapter.doUpdataList();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
    }
}
