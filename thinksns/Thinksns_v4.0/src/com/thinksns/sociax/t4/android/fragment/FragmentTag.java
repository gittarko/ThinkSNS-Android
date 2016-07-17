package com.thinksns.sociax.t4.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterMyTag;
import com.thinksns.sociax.t4.adapter.AdapterTagList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.component.GridViewMyTag;
import com.thinksns.sociax.t4.model.ModelAllTag;
import com.thinksns.sociax.t4.model.ModelMyTag;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelUserTagandVerify;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FragmentTag extends FragmentSociax implements
        AdapterView.OnItemClickListener, AdapterTagList.onItemTagClickListener {

    private GridViewMyTag gv_my_tag;
    private ArrayList<ModelAllTag> list_all = null;
    private ArrayList<ModelMyTag> list_my = null;
    private AdapterMyTag adapterMyTag = null;
    private TextView tv_no_tags = null;
    ModelUser user = null;
    Thinksns app = null;
    private ScrollView sc_tags;
    private ExecutorService single;
    private boolean isDothing = false;
    private LinearLayout layout;
    private EmptyLayout emptyLayout;
    private ArrayList<String> ids_list=null;
    private StringBuffer buffer = null;
    private int i=1;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tag;
    }

    @Override
    public void initView() {
        gv_my_tag = (GridViewMyTag) findViewById(R.id.gv_my_tag);

        tv_no_tags = (TextView) findViewById(R.id.tv_no_tags);

        emptyLayout = (EmptyLayout) findViewById(R.id.empty_layout);
//        emptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));

        layout = (LinearLayout) findViewById(R.id.tag_person);

        sc_tags = (ScrollView) findViewById(R.id.sc_tags);

        app = (Thinksns) getActivity().getApplicationContext();
        try {
            user = app.getUserSql().getUser(Thinksns.getMy().getUid() + "");
        } catch (UserDataInvalidException e) {
            e.printStackTrace();
        }

        list_my = new ArrayList<ModelMyTag>();
        adapterMyTag = new AdapterMyTag(getActivity(), list_my);
        gv_my_tag.setAdapter(adapterMyTag);
        gv_my_tag.setOnItemClickListener(this);

        single = Executors.newSingleThreadExecutor();

        ids_list=new ArrayList<>();
        buffer = new StringBuffer();

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
                        Thinksns app = (Thinksns) getActivity().getApplicationContext();
                        msg.obj = app.getTagsApi().getMyTag();
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

    // 所有标签
    public void getAllTag() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = StaticInApp.GET_TAG_LIST;
                try {
                    msg.obj = ((Thinksns) getActivity().getApplicationContext()).getUsers().getTagList();
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
                        Thinksns app = (Thinksns) getActivity().getApplicationContext();
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

    @Override
    public void onResume() {
        super.onResume();
//        getMyTag();
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

    // 删除标签
    public void delTag(final int tag_id, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = StaticInApp.DEL_MY_TAG;
                    try {
                        Thinksns app = (Thinksns) getActivity().getApplicationContext();
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
                    if (list_my == null) {

                    } else if (list_my.size() == 0) {
                        tv_no_tags.setVisibility(View.VISIBLE);
                        gv_my_tag.setVisibility(View.GONE);
                    }
                    adapterMyTag = new AdapterMyTag(getActivity(), list_my);
                    gv_my_tag.setAdapter(adapterMyTag);

                    break;
                case StaticInApp.GET_TAG_LIST:
                    ListData<SociaxItem> list = (ListData<SociaxItem>) msg.obj;
                    if (list == null) {
                        emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    } else if (list.size() == 0) {
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    } else {
                        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        for (int i = 0; i < list.size(); i++) {
                            View view = inflater.inflate(R.layout.tag_item, null);
                            final TextView title = (TextView) view.findViewById(R.id.textView_tagtitle);
                            GridView gridView = (GridView) view.findViewById(R.id.gridView_tag);
                            gridView.setOnItemClickListener(FragmentTag.this);
                            AdapterTagList adapter = new AdapterTagList(inflater);
                            adapter.setListener(FragmentTag.this);
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
                    isDothing = false;
                    break;
                case StaticInApp.ADD_MY_TAG:
                    isDothing = false;
                    try {
                        JSONObject json_add = new JSONObject((String) (msg.obj));
                        if (json_add != null) {
                            String info = json_add.getString("info");
                            int status = json_add.getInt("status");
                            Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                JSONArray addArray = json_add.getJSONArray("data");
                                for (int i = 0; i < addArray.length(); i++) {
                                    ModelMyTag modelMyTag = new ModelMyTag(addArray.getJSONObject(i));
                                    if (list_my.size() < 5) {
                                        list_my.add(modelMyTag);
                                        if (list_my.size() > 0) {
                                            tv_no_tags.setVisibility(View.GONE);
                                            gv_my_tag.setVisibility(View.VISIBLE);
                                        }
                                        adapterMyTag.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getActivity(), "最多选5个标签哦", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (DataInvalidException e) {
                        e.printStackTrace();
                    }

                    break;
                case StaticInApp.DEL_MY_TAG:
                    isDothing = false;
                    try {
                        int position = msg.arg1;
                        JSONObject json_del = new JSONObject((String) (msg.obj));
                        if (json_del != null) {
                            String info = json_del.getString("info");
                            int status = json_del.getInt("status");
                            Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
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
    public void initIntentData() {

    }

    @Override
    public void initListener() {
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取标签
                getAllTag();
            }
        });
    }

    @Override
    public void initData() {
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//        getMyTag();
        getAllTag();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isDothing)
            return;
        switch (parent.getId()) {
            case R.id.gv_my_tag:

                if (list_my != null) {
                    if (list_my.size() == 1) {
                        Toast.makeText(getActivity(), "至少要有一个标签哦", Toast.LENGTH_SHORT).show();
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
//        ids_list.add(title);
//        getIdsFromMap(ids_list);
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
                Toast.makeText(getActivity(), "最多选5个标签哦", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity().getApplicationContext(),"不能重复选择标签",Toast.LENGTH_SHORT).show();
        }
    }

    //更新sharedpreference里的字符串
    public void setStrFromList(){
        //把tag存起来
        SharedPreferences preferences = Thinksns.getContext().getSharedPreferences(StaticInApp.TAG_CLOUD, Context.MODE_PRIVATE);
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
