package com.thinksns.sociax.t4.android.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterAllTag;
import com.thinksns.sociax.t4.adapter.AdapterMyTag;
import com.thinksns.sociax.t4.adapter.AdapterTagList;
import com.thinksns.sociax.t4.adapter.AdapterTagPerson;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.component.GridViewMyTag;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.t4.model.*;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.UserDataInvalidException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类说明：
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年10月25日
 */
public class ActivityMyTag extends ThinksnsAbscractActivity implements
        OnItemClickListener, AdapterTagList.onItemTagClickListener {

    private GridViewMyTag gv_my_tag;
    private ArrayList<ModelAllTag> list_all = null;
    private ArrayList<ModelMyTag> list_my = null;
    private AdapterMyTag adapterMyTag = null;
    private TextView tv_no_tags = null;
//    private ImageView iv_back;
    ModelUser user = null;
    Thinksns app = null;
    private ScrollView sc_tags;
    private ExecutorService single;
    private boolean isDothing = false;
    private LinearLayout layout;
    private SmallDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    private void initView() {
        gv_my_tag = (GridViewMyTag) findViewById(R.id.gv_my_tag);

        tv_no_tags = (TextView) findViewById(R.id.tv_no_tags);
//        iv_back = (ImageView) findViewById(R.id.iv_back);

        loadingView = (LoadingView) findViewById(LoadingView.ID);

        layout = (LinearLayout) findViewById(R.id.tag_person);

        sc_tags = (ScrollView) findViewById(R.id.sc_tags);

        app = (Thinksns) this.getApplicationContext();
        try {
            user = app.getUserSql().getUser(Thinksns.getMy().getUid() + "");
        } catch (UserDataInvalidException e) {
            e.printStackTrace();
        }

        gv_my_tag.setOnItemClickListener(this);

        single = Executors.newSingleThreadExecutor();
        mDialog = new SmallDialog(this, getString(R.string.please_wait));
        loadingView.show(sc_tags);
        getMyTag();
        getAllTag();
    }

    private void initListener() {
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (user != null && list_my != null) {
//                    String userTag = "";
//                    for (int i = 0; i < list_my.size(); i++) {
//                        userTag += list_my.get(i).getTag_name() + "、";
//                    }
//                    if (!userTag.equals("")) {
//                        userTag.substring(0, userTag.length() - 1);
//                    }
//                    user.setUserTag(userTag);
//                    app.getUserSql().updateUser(user);
//                    updateUserHome(user);
//                }
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("user", user);
//                intent.putExtras(bundle);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
    }

    //发送广播
    public void updateUserHome(ModelUser user) {
        Intent intent = new Intent(StaticInApp.UPDATE_USER_HOME_TAG);
        intent.putExtra("user", user);
        this.sendBroadcast(intent);
    }

    // 我的标签
    public void getMyTag() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = StaticInApp.GET_MY_TAG;
                    try {
                        Thinksns app = (Thinksns) getApplicationContext();
                        msg.obj = app.getTagsApi().getMyTag();
                    } catch (ApiException e) {
                        e.printStackTrace();
                        loadingView.hide(sc_tags);
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 所有标签
    public void getAllTag() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = StaticInApp.GET_TAG_LIST;
                try {
                    msg.obj = ((Thinksns) getApplicationContext()).getUsers().getTagList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    // 添加标签
    public void addTag(final String name) {
        single.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = StaticInApp.ADD_MY_TAG;
                    try {
                        Thinksns app = (Thinksns) getApplicationContext();
                        msg.obj = app.getTagsApi().addTag(name);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 删除标签
    public void delTag(final int tag_id, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = StaticInApp.DEL_MY_TAG;
                    try {
                        Thinksns app = (Thinksns) getApplicationContext();
                        msg.obj = app.getTagsApi().deleteTag(tag_id);
                        msg.arg1 = position;
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticInApp.GET_MY_TAG:
                    list_my = (ArrayList<ModelMyTag>) msg.obj;
                    if (list_my != null && list_my.size() == 0) {
                        tv_no_tags.setVisibility(View.VISIBLE);
                        gv_my_tag.setVisibility(View.GONE);
                    }
                    adapterMyTag = new AdapterMyTag(ActivityMyTag.this, list_my);
                    gv_my_tag.setAdapter(adapterMyTag);
                    loadingView.hide(sc_tags);
                    break;
                case StaticInApp.GET_TAG_LIST:
                    ListData<SociaxItem> list = (ListData<SociaxItem>) msg.obj;
                    if (list == null || list.size() == 0) {
                        Toast.makeText(ActivityMyTag.this, "读取错误", Toast.LENGTH_SHORT).show();
                    } else {
                        LayoutInflater inflater = ActivityMyTag.this.getLayoutInflater();
                        for (int i = 0; i < list.size(); i++) {
                            View view = inflater.inflate(R.layout.tag_item, null);
                            final TextView title = (TextView) view.findViewById(R.id.textView_tagtitle);
                            GridView gridView = (GridView) view.findViewById(R.id.gridView_tag);
                            gridView.setOnItemClickListener(ActivityMyTag.this);
                            AdapterTagList adapter = new AdapterTagList(inflater);
                            adapter.setListener(ActivityMyTag.this);
                            ModelUserTagandVerify taglist = (ModelUserTagandVerify) list.get(i);
                            final String tle = taglist.getTitle();
                            title.setText(tle);
                            List<ModelUserTagandVerify.Child> list2 = taglist.getChild();
                            if (list2 != null) {
                                adapter.bindData(list2);
                                gridView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            layout.addView(view, i);
                        }
                    }
                    loadingView.hide(sc_tags);
                    isDothing = false;
                    break;
                case StaticInApp.ADD_MY_TAG:
                    isDothing = false;
//                    try {
//                        JSONObject json_add = new JSONObject((String) (msg.obj));
//                        if (json_add != null) {
//                            String info = json_add.getString("info");
//                            int status = json_add.getInt("status");
//                            Toast.makeText(ActivityMyTag.this, info, Toast.LENGTH_SHORT).show();
//
//                            if (status == 1) {
//                                JSONArray addArray = json_add.getJSONArray("data");
//                                for (int i = 0; i < addArray.length(); i++) {
//                                    ModelMyTag modelMyTag = new ModelMyTag(addArray.getJSONObject(i));
//                                    if (list_my.size() <= 5) {
//                                        list_my.add(modelMyTag);
//                                        if (list_my.size() > 0) {
//                                            tv_no_tags.setVisibility(View.GONE);
//                                            gv_my_tag.setVisibility(View.VISIBLE);
//                                        }
//                                        adapterMyTag.notifyDataSetChanged();
//                                    } else {
//                                        Toast.makeText(ActivityMyTag.this, "最多选5个标签哦", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (DataInvalidException e) {
//                        e.printStackTrace();
//                    }
                    try {
                        JSONObject json_add = new JSONObject((String) (msg.obj));
                        if (json_add != null) {
                            String info = json_add.getString("info");
                            int status = json_add.getInt("status");
                            Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                mDialog.dismiss();
//                                JSONArray addArray = json_add.getJSONArray("data");
//                                for (int i = 0; i < addArray.length(); i++) {
//                                    ModelMyTag modelMyTag = new ModelMyTag(addArray.getJSONObject(i));
//                                    if (list_my.size() < 5) {
//                                        list_my.add(modelMyTag);
//                                        if (list_my.size() > 0) {
//                                            tv_no_tags.setVisibility(View.GONE);
//                                            gv_my_tag.setVisibility(View.VISIBLE);
//                                        }
//                                        adapterMyTag.notifyDataSetChanged();
//                                    }
//                                }
                                setBack();
                            }else {
                                mDialog.dismiss();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mDialog.dismiss();
                    }

                    break;
                case StaticInApp.DEL_MY_TAG:
//                    isDothing = false;
//                    try {
//                        int position = msg.arg1;
//                        JSONObject json_del = new JSONObject((String) (msg.obj));
//                        if (json_del != null) {
//                            String info = json_del.getString("info");
//                            int status = json_del.getInt("status");
//                            Toast.makeText(ActivityMyTag.this, info, Toast.LENGTH_SHORT).show();
//                            if (status == 1) {
//                                //改变全部标签中对应标签的显示状态
//                                list_my.remove(list_my.get(position));
//                                if (list_my.size() == 0) {
//                                    tv_no_tags.setVisibility(View.VISIBLE);
//                                    gv_my_tag.setVisibility(View.GONE);
//                                }
//                                adapterMyTag.notifyDataSetChanged();
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    isDothing = false;
                    try {
                        int position = msg.arg1;
                        JSONObject json_del = new JSONObject((String) (msg.obj));
                        if (json_del != null) {
                            String info = json_del.getString("info");
                            int status = json_del.getInt("status");
                            Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                            if (status == 1) {
                                //改变全部标签中对应标签的显示状态
                                list_my.remove(list_my.get(position));
                                if (list_my.size() == 0) {
                                    tv_no_tags.setVisibility(View.VISIBLE);
                                    gv_my_tag.setVisibility(View.GONE);
                                }
                                adapterMyTag.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public String getTitleCenter() {
        return "选择标签";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(this,R.drawable.img_back,"确定");
    }

    @Override
    public View.OnClickListener getLeftListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
    }

    public void setBack(){
        if (user != null && list_my != null) {
            String userTag = "";
            for (int i = 0; i < list_my.size(); i++) {
                userTag += list_my.get(i).getTag_name() + "、";
            }
            if (!userTag.equals("")) {
                userTag.substring(0, userTag.length() - 1);
            }
            user.setUserTag(userTag);
            Thinksns.setMy(user);
            app.getUserSql().updateUser(user);
            updateUserHome(user);
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public View.OnClickListener getRightListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //把tag存起来
                SharedPreferences preferences= Thinksns.getContext().getSharedPreferences(StaticInApp.TAG_CLOUD_MINE, Context.MODE_PRIVATE);
                String str=preferences.getString("title","");
                if (str!=null&&!str.equals("null")&&!str.equals("")){
                    final String ids=str.substring(0, str.lastIndexOf(","));

                    if (!mDialog.isShowing()){
                        mDialog.setContent("请稍后...");
                        mDialog.show();
                    }

                    single.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Message msg = new Message();
                                msg.what = StaticInApp.ADD_MY_TAG;
                                try {
                                    Thinksns app = (Thinksns)getApplicationContext();
                                    msg.obj = app.getTagsApi().addTag(ids);
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                    mDialog.dismiss();
                                }
                                handler.sendMessage(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mDialog.dismiss();
                            }
                        }
                    });
                }else {
                    setBack();
                    mDialog.dismiss();
                }
            }
        };
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tag_select;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (isDothing)
            return;
        switch (parent.getId()) {
            case R.id.gv_my_tag:

                if (list_my != null) {
                    if (list_my.size() == 1) {
                        Toast.makeText(ActivityMyTag.this, "至少要有一个标签哦", Toast.LENGTH_SHORT).show();
                    } else {
                        delTag(list_my.get(position));
//                        delTag(list_my.get(position).getTag_id(), position);
                        isDothing = false;
                    }
                }
                break;
        }
    }

    @Override
    public void onTitleClick(String title) {
//        addTag(title);
    }

    // 删除标签
    public void delTag(final ModelMyTag myTag) {
        list_my.remove(myTag);
        adapterMyTag.notifyDataSetChanged();

        if (list_my.size() == 0) {
            tv_no_tags.setVisibility(View.VISIBLE);
            gv_my_tag.setVisibility(View.GONE);
        }

        setStrFromList();
    }

    @Override
    public void onTitleClick(ModelUserTagandVerify.Child child) {

        boolean isConstans=false;
        String value="";
        for (int i=0;i<list_my.size();i++){
            value=list_my.get(i).getTag_name();
            if (child.getTitle().equals(value)){
                isConstans=true;
            }
        }

        if (isConstans==false){
            if (list_my.size() < 5) {
                ModelMyTag myTag=new ModelMyTag();
                myTag.setTag_id(Integer.parseInt(child.getId()));
                myTag.setTag_name(child.getTitle());

                list_my.add(myTag);
                adapterMyTag.notifyDataSetChanged();

                if (list_my.size() > 0) {
                    tv_no_tags.setVisibility(View.GONE);
                    gv_my_tag.setVisibility(View.VISIBLE);
                }

                setStrFromList();
            } else {
                Toast.makeText(getApplicationContext(), "最多选5个标签哦", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"不能重复选择标签",Toast.LENGTH_SHORT).show();
        }
    }


    //更新sharedpreference里的字符串
    public void setStrFromList(){
        //把tag存起来
        SharedPreferences preferences = Thinksns.getContext().getSharedPreferences(StaticInApp.TAG_CLOUD_MINE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("title", "");

        StringBuffer ids_buffer=new StringBuffer();
        if (list_my!=null&&list_my.size()>0){
            for (int i=0;i<list_my.size();i++){
                ids_buffer.append(list_my.get(i).getTag_name()+",");
            }

            editor.putString("title", ids_buffer.toString());
            editor.commit();
        }
    }
}
