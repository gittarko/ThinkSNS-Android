package com.thinksns.sociax.db;

import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ThinksnsTableSqlHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "thinksns";
	public static final int DB_VERSION = 12;
	protected String TABLE_NAME;

	public ThinksnsTableSqlHelper(Context context, CursorFactory factory) {
		super(context, DB_NAME, factory, DB_VERSION);
	}

	public static final String TAG = ThinksnsTableSqlHelper.class.getSimpleName();
	
	public static final String tableName = "User";
	public static final String id = "uid";
	public static final String uname = "uname";
	public static final String token = "token";
	public static final String secretToken = "secretToken";
	public static final String province = "province";		//省
	public static final String city = "city";
	public static final String location = "location";		//地区
	public static final String face = "face";				//头像
	public static final String sex = "sex";					//性别
	public static final String department = "department";
	public static final String usertel = "usertel";
	public static final String userEmail = "userEmail";		//邮箱地址
	public static final String userPhone = "userPhone";
	public static final String QQ = "QQ";
	public static final String userInfo = "userInfo";
	public static final String userTag = "userTag";
	public static final String weiboCount = "weiboCount";
	public static final String followersCount = "followersCount";
	public static final String followedCount = "followedCount";
	public static final String isFollowed = "isFollowed";
	public static final String lastWeiboId = "lastWeibo";
	public static final String isLogin = "login";
	public static final String myLastWeibo = "myLastWeibo";
	public static final String userJson = "userJson";
	public static final String weiboTable = "home_weibo";
	public static final String weiboId = "weiboId";
	public static final String uid = "uid";
	public static final String userName = "userName";
	public static final String content = "content";
	public static final String cTime = "cTime";
	public static final String from = "weiboFrom";
	public static final String timeStamp = "timestamp";
	public static final String comment = "comment";
	public static final String type = "type";
	public static final String attach = "attach";
	public static final String picUrl = "picUrl";
	public static final String thumbMiddleUrl = "thumbMiddleUrl";
	public static final String thumbUrl = "thumUrl";
	public static final String transpond = "transpone";
	public static final String transpondCount = "transpondCount";
	public static final String userface = "userface";
	public static final String cover = "userCover";		//用户封面
	public static final String transpondId = "transpondId";
	public static final String favorited = "favorited";
	public static final String digg = "isdigg";
	public static final String isdel = "isdel";
	public static final String diggnum = "diggnum";
	public static final String weiboJson = "weiboJson";

	public static final String atMeTable = "at_me";

	public static final String myCommentTable = "my_comment";

	public static final String myMessageTable = "user_message";

	public static final String siteList = "sites";

	public static final String remindTable = "remind_message";

	public static final String attachTable = "attach";

	public static final String appInfo = "app_info";

	public static final String tbWeiba = "tb_weiba";

	public static final String tbSiteUser = "tb_site_uname";

	public static final String tbChatMsg = "tb_chat_detal";

	public static final String tbChannel = "tb_channel";

	/***************** t4 *****************/
	public static final String tbChatList = "tb_chat_List";// 聊天列表
	public static final String tbRoomList = "tb_room_List";// 聊天房间
	public static final String tbWeiboDraft = "tb_weibo_draft";// 微博草稿箱

	protected static SQLiteDatabase db = null;
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		ThinksnsTableSqlHelper.db = db;
		String sql = "Create Table if not exists " + tableName + " ( " + id
				+ " integer," + uname + " varchar not null," + token
				+ " varchar," + secretToken + " varchar," + province
				+ " varchar," + city + " varchar," + location + " varchar,"
				+ face + " varchar," + cover + " vachar, " + sex + " varchar," + department
				+ " text," + usertel + " text," + userEmail + " text,"
				+ userPhone + " text," + QQ + " text," + userInfo + " text,"
				+ userTag + " text," + weiboCount + " integer not null,"
				+ followersCount + " integer not null," + followedCount
				+ " integer not null," + isFollowed + " boolean," + lastWeiboId
				+ " integer," + isLogin + " boolean," + myLastWeibo + " text,"
				+ userJson + " text)";
		db.execSQL(sql);

		db.execSQL("Create Table if not exists my_comment (uid integer,userface text,timestamp integer,status text,replyUid integer,replyCommentId integer,"
				+ "weiboId integer,content text,uname text,type text,replyComment text,cTime text,commentId integer,commentUser text,site_id integer,my_uid integer)");

		db.execSQL("Create Table if not exists at_me (weiboId integer,uid integer,"
				+ "userName varchar,content text,cTime varchar,weiboFrom integer,timestamp integer,"
				+ "comment integer,type integer,picUrl text,thumbMiddleUrl text,thumUrl text,transpone text,transpondCount integer,"
				+ "userface text,transpondId integer,favorited integer, isdigg integer,  diggnum integer,isdel integer, weiboJson text,site_id integer,my_uid integer)");

		db.execSQL("Create Table if not exists fav_weibo (weiboId integer,uid integer,"
				+ "userName varchar,content text,cTime varchar,weiboFrom text,timestamp integer,"
				+ "comment integer,type text,attach INETGER,picUrl text,thumbMiddleUrl text,thumUrl text,transpone text,transpondCount integer,"
				+ "userface text,transpondId integer,favorited integer, isdigg integer, diggnum integer, isdel integer, weiboJson text,site_id integer,my_uid integer)");

		db.execSQL("Create Table if not exists user_message(list_id integer,member_uid integer,new integer,message_num integer,ctime text,list_ctime text, from_uid integer, "
				+ "type integer, title text,member_num integer,min_max integer,mtime integer,content text,from_uname text,to_uname text,to_uid integer,from_face text,to_face text,last_message text, site_id integer, my_uid integer)");

		db.execSQL("Create Table if not exists sites (site_id integer,name text,logo text,uid integer,status_mtime text,url text,description text,email text,"
				+ "phone text,ctime text,status integer,denied_reason text,isused integer)");

		db.execSQL("CREATE TABLE if not exists remind_message(_id integer PRIMARY KEY AUTOINCREMENT, name text,type text,icon text , counts integer,data text,ctime text,my_uid integer)");

		db.execSQL("CREATE TABLE if not exists attach(_id integer PRIMARY KEY AUTOINCREMENT, weiboId integer, type integer, name text,small text,middle text ," +
				"normal text, attach_width integer, attach_height integer, my_uid integer,site_id integer)");

		db.execSQL("CREATE TABLE if not exists app_info(_id integer PRIMARY KEY AUTOINCREMENT, appid integer, name text,type text,icon text ,link text,tag text,my_uid integer)");
		// weiba table
		db.execSQL("CREATE TABLE if not exists tb_weiba(_id INTEGER PRIMARY KEY AUTOINCREMENT, weiba_id INTEGER, name TEXT,intro TEXT,icon_url TEXT,icon_data BLON ,f_count INTEGER,t_count INTEGER, f_state INTEGER,post_permission INTEGER, site_id integer, my_uid integer)");

		// chat msg table
		db.execSQL("CREATE TABLE if not exists tb_chat_detal(_id INTEGER PRIMARY KEY AUTOINCREMENT,list_id INTEGER,content TEXT, message_id INTEGER ,from_client_id INTEGER, from_uid INTEGER,from_uname TEXT,min_max INTEGER,msgtype TEXT,room_id INTEGER, time INTEGER, "
				+ "to_client_id TEXT, to_uid INTEGER,to_uname TEXT,type TEXT,degree INTEGER,longitude INTEGER,latitude INTEGER ,site_id INTEGER,my_uid INTEGER,fromFace TEXT,forNew TEXT)");

		db.execSQL("CREATE TABLE if not exists tb_channel(channel_id integer, title TEXT, sort_id integer, icon_url text, site_id integer, my_uid integer)");

		db.execSQL("CREATE TABLE if not exists tb_site_uname(u_name TEXT)");

		db.execSQL("CREATE TABLE if not exists " + ThinksnsTableSqlHelper.weiboTable + "(" + ThinksnsTableSqlHelper.uid + " integer, "
				+ ThinksnsTableSqlHelper.weiboId + " integer, " + ThinksnsTableSqlHelper.cTime + " varchar, "
				 +  ThinksnsTableSqlHelper.content + " TEXT, " + ThinksnsTableSqlHelper.weiboJson + " TEXT)");

		/******************** t4 *****************************/
		/**
		 * 聊天信息列表，参数详情见ModelChatMessage wz 10.21 loginUser_id chatUser_id type
		 * from_client_id to_client_id room_id min_max msgtype from_uid
		 * from_uname fromFace to_uid to_uname toFace content attach_id time
		 * imageUrl isNew
		 */

		/**
		 * 微博草稿箱列表
		 */
		db.execSQL("CREATE TABLE if not exists tb_weibo_draft(_id integer PRIMARY KEY AUTOINCREMENT, " +
				"feed_id integer, content text, time text, my_uid integer,"
				+ "has_image boolean,has_video boolean, title varchar," +
				"image_list text,video_path text, type intener, channel_id integer, " +
				"is_send boolean, latitude varchar, longitude varchar, address varchar)");

	}
	
	/**
	 * 更新数据库的时候用下面的方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE User ADD COLUMN myLastWeibo text");
		// db.execSQL("ALTER TABLE home_weibo ADD COLUMN weiboJson text");
		// db.execSQL("ALTER TABLE User ADD COLUMN userJson text");
	}
	
	
	public void createRoomTable() {
		this.getWritableDatabase().execSQL("CREATE TABLE if not exists "
				+ tbRoomList + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,room_id INTEGER,master_uid INTEGER ,is_group INTEGER,title TEXT,"
				+ " mtime INTEGER,self_index INTEGER,content TEXT,type TEXT,from_uid INTEGER,from_uname TEXT,from_uface TEXT,from_uface_url TEXT,to_name TEXT,to_uid INTEGER,member_num INTEGER,isNew INTEGER)");
	}

}
