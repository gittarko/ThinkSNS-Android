package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thinksns.sociax.t4.model.ModelTopic;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hedong on 16/4/14.
 * 最新话题和热门话题缓存表
 */
public class TopicListSqlHelper extends ThinksnsTableSqlHelper{
    private static TopicListSqlHelper instance;
    private static final String topicType = "topicList";
    private static final String topicJson = "topicJson";

    public TopicListSqlHelper(Context context) {
        super(context, null);
        TABLE_NAME = "topicList";
        onCreate(getWritableDatabase());
    }

    public static TopicListSqlHelper getInstance(Context context) {
        if(instance == null) {
            instance = new TopicListSqlHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建话题列表数据表
        String sql = "create table if not exists " + TABLE_NAME + "(" + topicType + " varchar, " + topicJson + " TEXT)";
        db.execSQL(sql);
    }

    /**
     * 保存最新话题列表
     * @param text
     * @return
     */
    public long saveCacheData(String text) {
        ContentValues values = new ContentValues();
        values.put(topicJson, text);
        long update = getWritableDatabase().update(TABLE_NAME, values, topicType + " = ? ", new String[]{topicType});
        if(update > 0)
            return update;
        return getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    /**
     * 获取本地最新、最热话题
     * @return
     */
    public ListData<ModelTopic> getTopicList() {
        ListData<ModelTopic> returnlist = new ListData<ModelTopic>();
        JSONArray commends = null, lists = null;// 推荐话题、普通话题

        SQLiteDatabase database = getReadableDatabase();
        String sql = "select * from " + TABLE_NAME;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String data = cursor.getString(cursor.getColumnIndex(topicJson));
            try {
                JSONObject response = new JSONObject(data);
                if (response.has("commends")) {
                    commends = response.getJSONArray("commends");
                    for (int i = 0; i < commends.length(); i++) {
                        ModelTopic mdi = new ModelTopic(
                                commends.getJSONObject(i));
                        if (i == 0) {// 只有第一页才显示标题
                            mdi.setFirst(true);
                        }
                        returnlist.add(mdi);
                    }
                }
                if (response.has("lists")) {
                    lists = response.getJSONArray("lists");
                    for (int i = 0; i < lists.length(); i++) {
                        ModelTopic mdi = new ModelTopic(lists.getJSONObject(i));
                        if (i == 0) {
                            mdi.setFirst(true);
                        }
                        returnlist.add(mdi);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return returnlist;
    }
}
