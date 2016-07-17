package com.thinksns.sociax.t4.android.info;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.component.LazyViewPager;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.t4.model.ModelInformationCate;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;

import java.util.ArrayList;

/**
 * 类说明：资讯
 * Created by Zoey on 2016-04-27.
 */
public class ActivityInformation extends ThinksnsAbscractActivity {

    private TabLayout tb_information;
    private LazyViewPager vp_information;
    private SmallDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        tb_information = (TabLayout) this.findViewById(R.id.tb_information);
        vp_information = (LazyViewPager) this.findViewById(R.id.vp_information);
        vp_information.setOffscreenPageLimit(0);

        dialog = new SmallDialog(this, getString(R.string.please_wait));

        initTab();//初始化tab
    }

    //初始化TabLayout的设置
    public void initTab(){
        tb_information.setTabMode(TabLayout.MODE_SCROLLABLE);
        vp_information.setOffscreenPageLimit(0);
    }

    public void initListener(){
        vp_information.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tb_information));
        tb_information.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_information.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void initData(){
        if (!dialog.isShowing()){
            dialog.setContent("请稍后...");
            dialog.show();
        }
        getCate();
    }

    /**
     * 获取分类
     */
    public void getCate() {
        try {
            Thinksns.getApplication().getInformationApi().getCate(new ApiHttpClient.HttpResponseListener() {
                @Override
                public void onSuccess(Object result) {
                    dialog.dismiss();
                    ArrayList<ModelInformationCate> cateList = (ArrayList<ModelInformationCate>) result;
                    for (int i = 0; i < cateList.size(); i++) {
                        tb_information.addTab(tb_information.newTab().setText(cateList.get(i).getName()));
                    }
                    setPager(cateList);
                }

                @Override
                public void onError(Object result) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ApiException e) {
            dialog.dismiss();
            e.printStackTrace();
        }
    }

    public void setPager(ArrayList<ModelInformationCate> tables) {
        InformationPagerAdapter adapter = new InformationPagerAdapter(getSupportFragmentManager(), tables);
        vp_information.setAdapter(adapter);
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    public String getTitleCenter() {
        return "资讯";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_information;
    }
}
