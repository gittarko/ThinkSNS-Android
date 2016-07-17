package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelPost;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hedong on 16/4/12.
 */
public class PostSqlHelper extends ThinksnsTableSqlHelper{
    private static final String postId = "postId";
    private static final String isDigg = "isDigg";
    private static final String isRecommend = "isRecommend";
    private static final String loginUid = "loginUid";
    private static final String postJson = "postJson";

    private static PostSqlHelper instance;

    public PostSqlHelper(Context context) {
        super(context, null);
        TABLE_NAME = "tbPost";
        onCreate(getWritableDatabase());
    }

    public static PostSqlHelper getInstance(Context context) {
        if(instance == null) {
            instance = new PostSqlHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建帖子表
        String sql = "create Table if not exists " + TABLE_NAME + " ( " + postId + " integer, " + loginUid + " integer, " +
                isDigg + " boolean, " + isRecommend + " boolean, " + postJson + " text)";
        db.execSQL(sql);
    }

    /**
     * 保存一条帖子信息
     * @param post
     * @return
     */
    public long addPost(ModelPost post) {
        ContentValues values = new ContentValues();
        values.put(postId, post.getPost_id());
        values.put(isDigg, post.isDigg());
        values.put(loginUid, Thinksns.getMy().getUid());
        values.put(isRecommend, Integer.parseInt(post.getRecommend()));
        values.put(postJson, post.getPostJson());
        long update = getWritableDatabase().update(TABLE_NAME, values, postId + " = ?",
                new String[]{String.valueOf(post.getPost_id())});
        if(update > 0)
            return update;

        return getWritableDatabase().insert(TABLE_NAME, null, values);
    }


    /**
     * 删除一条帖子信息
     * @param post
     * @return
     */
    public void delPost(ModelPost post) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "delete from " + TABLE_NAME + " where " + postId + " = " + post.getPost_id(); // order by _id
        database.execSQL(sql);
        database.close();
    }

    /**
     * 更新帖子点赞状态
     * @param postId
     * @param state
     */
    public void updateDiggState(int postId, boolean state) {
        getWritableDatabase().execSQL(
                "update " + TABLE_NAME + " set " + isDigg + " = "
                        + state + " where " + this.postId + " = " + postId
                        + " and " + loginUid + " = " + Thinksns.getMy().getUid());
    }

    /**
     * 获取帖子详细信息
     * @param post_id
     * @return
     */
    public ModelPost getPostInfo(int post_id) {
        SQLiteDatabase database = getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + this.postId + " = " + post_id;
        Cursor cursor = database.rawQuery(sql, null);
        ModelPost post = null;
        if (cursor.moveToFirst()) {
            String json = cursor.getString(cursor.getColumnIndex(postJson));
            try {
                    post = new ModelPost(new JSONObject(json));
                    post.setPost_id(cursor.getInt(cursor
                            .getColumnIndex(postId)));
                    int digg = cursor.getInt(cursor.getColumnIndex(isDigg));
                    int loginUid = cursor.getInt(cursor.getColumnIndex(this.loginUid));
                    if(digg == 1 && loginUid == Thinksns.getMy().getUid())
                        post.setDigg(true);
            }catch(JSONException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return post;
    }

}
