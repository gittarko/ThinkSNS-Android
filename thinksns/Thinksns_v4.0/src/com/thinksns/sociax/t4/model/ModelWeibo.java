package com.thinksns.sociax.t4.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.modle.Posts;
import com.thinksns.sociax.modle.UserApprove;
import com.thinksns.sociax.t4.android.alipay.Base64;
import com.thinksns.sociax.t4.exception.WeiboDataInvalidException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelWeibo extends SociaxItem {
	private static final long serialVersionUID = 1L;

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public String getAddress() {
		if(address == null || address.equals("null"))
			return null;
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLongitude() {
		if(longitude == null || longitude.isEmpty() || longitude.equals("null"))
			return null;
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		if(latitude == null || latitude.isEmpty() || latitude.equals("null"))
			return null;
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Override
	public int compareTo(SociaxItem another) {
		return 0;
	}

	public static enum From {
		WEB, PHONE, ANDROID, IPHONE, IPAD, WINDOWSPHONE
	}

	public static final String WEIBA_POST = "weiba_post";
	public static final String WEIBA_REPOST = "weiba_repost";
	private static final String POSTIMAGE = "postimage";
	private static final String POSTIFILE = "postfile";
	public static final String POSTVIDEO = "postvideo";
	private int appRowId;
	private UserApprove userApprove;// 用户认证信息
	private long timeLine;

	public ModelWeibo() {
	}

	public boolean isInvalidWeibo() {
		return !"".equals(this.content);
	}

	public long getTimeLine() {
		return timeLine;
	}

	public void setTimeLine(long timeLine) {
		this.timeLine = timeLine;
	}

	public ModelWeibo(JSONObject weiboData, String str)
			throws WeiboDataInvalidException {
		try {
			this.setContent(weiboData.optString("feed_content"));
			this.setCtime(weiboData.optString("publish_time"));
			this.setWeiboId(Integer.parseInt(weiboData.getString("feed_id")));

			JSONObject userInfo = weiboData.getJSONObject("user_info");
			this.setUid(Integer.parseInt(userInfo.getString("uid")));
			this.setFrom(weiboData.getString("from"));
			if (weiboData.has("type") && weiboData.getString("type") != null) {
				this.setType(weiboData.getString("type"));
			}
			this.setTranspondCount(weiboData.getInt("repost_count"));
			if (!this.isNullForTranspondId()) {
				ModelWeibo transpond = new ModelWeibo(
						weiboData.getJSONObject("transpond_data"));
				this.setSourceWeibo(transpond);
			}

			this.weiboJsonString = weiboData.toString();

		} catch (JSONException e) {
			throw new WeiboDataInvalidException(e.getMessage());
		}
	}

	public void setUsApprove(UserApprove userApprove) {
		this.userApprove = userApprove;
	}

	public UserApprove getUsApprove() {
		return userApprove;
	}

	@Override
	public String toString() {
		return "ModelWeibo{" +
				"appRowId=" + appRowId +
				", userApprove=" + userApprove +
				", timeLine=" + timeLine +
				", posts=" + posts +
				", videoPath='" + videoPath + '\'' +
				", uid=" + uid +
				", username='" + username + '\'' +
				", content='" + content + '\'' +
				", isDigg=" + isDigg +
				", diggUsers=" + diggUsers +
				", diggNum=" + diggNum +
				", isDel=" + isDel +
				", can_comment=" + can_comment +
				", sid=" + sid +
				", isFavorited=" + isFavorited +
				", userface='" + userface + '\'' +
				", weiboJsonString='" + weiboJsonString + '\'' +
				", timeStamp=" + timeStamp +
				", cTime='" + cTime + '\'' +
				", weiboId=" + weiboId +
				", from='" + from + '\'' +
				", sourceWeibo=" + sourceWeibo +
				", type='" + type + '\'' +
				", commentCount=" + commentCount +
				", transpondCount=" + transpondCount +
				", attachs=" + attachs +
				", channel_category_id=" + channel_category_id +
				", channel_name='" + channel_name + '\'' +
				", title='" + title + '\'' +
				", source_name='" + source_name + '\'' +
				", isTop=" + isTop +
				", attachVideo=" + attachVideo +
				", is_repost=" + is_repost +
				", comments=" + comments +
				", tempJsonString='" + tempJsonString + '\'' +
				'}';
	}

	public ModelWeibo(JSONObject weiboData, int type)
			throws WeiboDataInvalidException {
		try {
			if (weiboData.has("comment_count"))
				this.setCommentCount(weiboData.getInt("comment_count"));
			this.setContent(weiboData.getString("source_content"));
			this.setCtime(weiboData.getString("ctime"));
			this.setWeiboId(weiboData.getInt("source_id"));
			this.setUid(weiboData.getInt("uid"));
			this.setFrom(weiboData.getString("from"));
			if (weiboData.has("feedtype"))
				this.setType(weiboData.getString("feedtype"));
			if (weiboData.has("comment_count"))
				this.setTranspondCount(weiboData.getInt("repost_count"));
			if (weiboData.has("transpond_id"))
				this.setTranspondId(weiboData.getInt("transpond_id"));
			this.setUsername(weiboData.getString("source_title"));
			if (!this.isNullForTranspondId()) {
				ModelWeibo transpond = new ModelWeibo(
						weiboData.getJSONObject("transpond_data"));
				this.setSourceWeibo(transpond);
			}
			this.weiboJsonString = weiboData.toString();
		} catch (JSONException e) {
			throw new WeiboDataInvalidException(e.getMessage());
		}
	}

	@Override
	public boolean checkValid() {
		boolean result = true;
		/*
		 * if(this.hasImage()){ result = result &&
		 * !(this.isNullForThumbMiddleUrl() || this.isNullForThumbUrl() ||
		 * this.isNullForPic()); }
		 * 
		 * if(!this.isNullForTranspondId()){ result = result &&
		 * !this.isNullForTranspond(); }
		 * 
		 * result = result && !(this.isNullForContent() || this.isNullForCtime()
		 * || this.isNullForUid() || this.isNullForTimestamp() ||
		 * this.isNullForUserFace() || this.isNullForWeiboId() ||
		 * this.isNullForUserName());
		 */
		return result;
	}

	public int getAppRowId() {
		return appRowId;
	}

	public void setAppRowId(int appRowId) {
		this.appRowId = appRowId;
	}

	private Posts posts;

	public Posts getPosts() {
		return posts;
	}

	public void setPosts(Posts posts) {
		this.posts = posts;
	}

	String videoPath;

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String staticVideoPath) {
		this.videoPath = staticVideoPath;
	}

	/******************************* 1.29 wz整理微博 *************************/
	/**
	 * 打印Log的时候使用的TAG,建议其他类使用TSTAG_XXX，方便筛选
	 */
	public static final String TAG = "TSTAG_ModelWeibo";
	/**
	 * 发布微博的用户id
	 */
	private int uid;
	/**
	 * 发布微博的用户名字
	 */
	private String username;
	/**
	 * 微博内容
	 */
	private String content;

	/**
	 * 微博是否已经点赞 1已赞 0 未赞
	 */
	private int isDigg;

    /**
     * 点赞用户列表
     */
    private ListData<ModelUser> diggUsers = new ListData<ModelUser>();
	/**
	 * 微博赞数目
	 */
	private int diggNum;
	/**
	 * 微博是否已经删除 1 删除 0 未删除
	 */
	private int isDel;
	/**
	 * 是否能对微博进行评论，默认可以
	 */
	private boolean can_comment = true;
	/**
	 * 原微博的id，如果有原微博的话，否则为1
	 */
	private int sid;
	/**
	 * 微博是否已经收藏 1 已收藏 0未收藏
	 */
	private boolean isFavorited;
	/**
	 * 微博的用户头像地址
	 */
	private String userface;

    /**
     * 是否关注
     */
	private int following = -1;

	/**
	 * 微博的json字符串，最原始的用户数据，从网页端获取下来的user数据优先保存成此字符串
	 */
	private String weiboJsonString;
	/**
	 * 发布的的毫秒时间戳，同Ctime 需要根据服务端返回哪个取
	 */
	private int timeStamp;
	/**
	 * 发布的毫秒时间戳，同timeStamp,需要根据服务端返回哪个取
	 */
	private String cTime;
	/**
	 * 微博的feed_id
	 */
	private int weiboId;
	/**
	 * 微博来源，一般为来自xxx
	 */
	private String from;

	/**
	 * 原来的微博
	 */
	private ModelWeibo sourceWeibo;
	/**
	 * 微博的类型 一.普通微博 post; 二.图片微博 postimage ; 三.视频微博 postvideo ; 四.文件微博postfile;
	 * 五.转发生成的微博repost,这时候需要根据sourceWeibo来判断类型: 此时根据source_info的type可以把被转发微博分为:
	 * 1换发普通微博 post 2 转发图片微博postimage 3 转发视频微博postvideo 4 文件微博转发postfile
	 * 六.帖子/知识转发过来的微博 weiba_post/blog_post; 七.转发帖子/知识再转发生成的的微博weiba_repost
	 * blog_repost;
	 */
	private String type = "";
	/**
	 * 微博评论数
	 */
	private int commentCount;
	/**
	 * 微博转发数目
	 */
	private int transpondCount;

	/**
	 * 附件列表集合，如果type为postimage时候这里是图片集合，如果是postfile时候为文件集合
	 */
	private ListData<ModelImageAttach> attachs;
	/**
	 * 推荐列表的微博，必定来自频道，并且带有频道id和频道名字
	 */
	private int channel_category_id;

	/**
	 * 推荐列表的微博，必定来自频道，并且带有频道id和频道名字
	 */
	private String channel_name;
	/**
	 * 如果type=weiba_post,也就是来自帖子的微博，这个是帖子标题，
	 */
	private String title;
	/**
	 * 如果type=weiba_post 这个是微吧名字
	 */
	private String source_name;
	/**
	 * 微博是否置顶
	 */
	private boolean isTop = false;

	/**
	 * 如果type=weiba_post,也就是来自帖子的微博，这个是帖子标题，
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置帖子的标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 如果type=weiba_post 这个是微吧名字
	 */
	public String getSource_name() {
		return source_name;
	}

	/**
	 * 设置微吧名字
	 * 
	 * @param source_name
	 */
	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}

	/**
	 * 单个视频附件，当type问postvideo的时候有，否则为null
	 */
	private ModelVideo attachVideo;

	/**
	 * 地理信息
	 */
	private String address;

	/**
	 * 经度
	 */
	private String longitude;

	/**
	 * 纬度
	 */
	private String latitude;

	//微博图片集合
	private ArrayList<String> photoList;

	public ArrayList<String> getPhotoList() {
		return photoList;
	}

	public void setPhotoList(ArrayList<String> photoList) {
		if(this.photoList == null)
			this.photoList = new ArrayList<String>();
		this.photoList = photoList;
	}

	/**
	 * json数据转换成微博
	 * 
	 * @param weiboData
	 *            jsonObject数据
	 * @throws WeiboDataInvalidException
	 */
	public ModelWeibo(JSONObject weiboData) throws WeiboDataInvalidException {
		// 将微博json数据保存
		this.setWeiboJsonString(weiboData.toString());
		// 解析成微博
		try {
			if (weiboData.has("is_del")) {
				// 如果微博有已经删除字段，表示此微博已经被删除
				try {
					this.setWeiboIsDelelet(weiboData.getInt("is_del"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				/******************** 用户信息 ***********************/

				if (weiboData.has("user_info")) {
					try {
						JSONObject jo = weiboData.getJSONObject("user_info");
						if (jo.has("uid")) {// 用户id
							try {
								this.setUid(Integer.parseInt(jo
										.getString("uid")));
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
						if (jo.has("uname")) {// 用户名
							try {
								this.setUsername(jo.getString("uname"));
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						if (jo.has("avatar")) {// 用户头像
							try {
								this.setUserface(jo.getJSONObject("avatar")
										.getString("avatar_middle"));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (jo.has("user_group")) {// 用户认证信息
							try {
								this.setUsApprove(new UserApprove(jo));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (jo.has("follow_state")) {
							this.setFollowing(jo.getJSONObject("follow_state").getInt("following"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("is_top")) {
					this.isTop = (weiboData.getInt("is_top") == 1);
				}
				if (weiboData.has("sid")) {
					// 原始微博id
					try {
						this.setSid(weiboData.getInt("sid"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("can_comment")) {// 是否有回复权限
					try {
						this.setCan_comment(weiboData.getString("can_comment")
								.equals("1") ? true : false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("feed_id")) {// 微博id
					try {
						this.setWeiboId(Integer.parseInt(weiboData
								.getString("feed_id")));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}

				if (weiboData.has("content")) {// 微博内容
					try {
						Pattern pattern = Pattern
								.compile("\\[emoji\\:(.*?)\\]");
						String content = weiboData.getString("content");
						Matcher matcher = pattern.matcher(content);
						while (matcher.find()) {
							content = content.replace(matcher.group(), new String(Base64.decode(matcher.group())));
						}

						this.setContent(content);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("publish_time")) {// 发布时间戳 同下，有时候会用下面那个返回
					try {
						this.setTimestamp(weiboData.getInt("publish_time"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("ctime")) {// 发布时间戳 同上，有时候会用上面那个返回
					try {
						this.setCtime(weiboData.getString("ctime"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("from")) {// 微博来源
					try {
						this.setFrom(weiboData.getString("from"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("type")) {// 微博类型
					try {
						this.setType(weiboData.getString("type"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("title")) {// 来自微吧的时候帖子的标题
					this.setTitle(weiboData.getString("title"));
				}
				if (weiboData.has("source_name")) {// 来自微吧的时候帖子所在的微吧
					this.setSource_name(weiboData.getString("source_name"));
				}
				if (weiboData.has("is_repost")) {// 是否是转发微博
					try {
						this.setTranspondId(Integer.parseInt(weiboData
								.getString("is_repost")));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (weiboData.has("is_favorite")) { // 是否收藏
					try {
						int isFav = Integer.parseInt(weiboData.getString("is_favorite"));
						this.setFavorited(isFav == 1 ? true : false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("is_digg")) { // 是否已赞
					try {
						this.setIsDigg(weiboData.getInt("is_digg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("comment_count")) { // 评论数
					try {
						this.setCommentCount(Integer.parseInt(weiboData.getString("comment_count")));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("repost_count")) {
					try {
						this.setTranspondCount(weiboData.getInt("repost_count"));// 转发数
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("digg_count")) { // 赞的数量
					try {
						this.setDiggNum(weiboData.getInt("digg_count"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("channel_category_id")) {// 频道的微博携带字段
					try {
						this.setChannel_category_id(weiboData
								.getInt("channel_category_id"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (weiboData.has("channel_category_name")) {// 频道的微博携带字段
					try {
						this.setChannel_name(weiboData
                                .getString("channel_category_name"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (weiboData.has("attach_info")) {// 附件
					try {
						JSONArray ja = weiboData.optJSONArray("attach_info");
						if (this.hasImage() || this.hasFile()
								&& ja.length() > 0) {
							// 图片或者文件附件
							ListData<ModelImageAttach> attachs = new ListData<ModelImageAttach>();
							try {
								for (int i = 0; i < ja.length(); i++) {
									ModelImageAttach attach = new ModelImageAttach();
									JSONObject temp = (JSONObject) ja.get(i);
									attach.setWeiboId(this.getWeiboId());
									attach.setId(Integer.parseInt(temp
											.getString("attach_id")));
									attach.setName(temp
											.getString("attach_name"));

									if (temp.has("attach_middle"))
										attach.setMiddle(temp
												.getString("attach_middle"));

									if (temp.has("attach_origin"))
										attach.setOrigin(temp.getString("attach_origin"));
									if(temp.has("attach_small"))
										attach.setSmall(temp.getString("attach_small"));
									/**
									 * 获取图片的尺寸
									 */
									if (temp.has("attach_origin_height")) {
										attach.setAttach_origin_height(temp
												.getString("attach_origin_height"));
									}

									if (temp.has("attach_origin_width")) {
										attach.setAttach_origin_width(temp
												.getString("attach_origin_width"));
									}

									attachs.add(attach);
								}
								if (attachs.size() > 0) {
									this.setAttachImage(attachs);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (this.hasVideo()) {
							// 视频类型的附件
							JSONObject videoJson = null;
							if(ja == null) {
								videoJson = weiboData.optJSONObject("attach_info");
							}else {
								videoJson = ja.getJSONObject(0);
							}

							try {
								ModelVideo video = new ModelVideo(videoJson);
								this.setAttachVideo(video);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				if (this.hasVideo()) {
					// 视频类型的附件
					try {
						JSONArray array = weiboData.optJSONArray("attach_info");
						if(array != null) {
							ModelVideo video = new ModelVideo(array.optJSONObject(0));
							this.setAttachVideo(video);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (weiboData.has("comment_info")) {// 评论列表
					try {
						JSONArray commentJa = weiboData.getJSONArray("comment_info");
						for (int i = 0; i < commentJa.length(); i++) {
							try {
//								Comment comment = new Comment(
//										commentJa.getJSONObject(i));
//								comments.add(comment);
								ModelComment mc = new ModelComment(commentJa.getJSONObject(i));
								commentList.add(mc);
							} catch (DataInvalidException e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

                if (weiboData.has("digg_users")) {
					// 点赞用户列表
                    try {
                        JSONArray diggUsersJa = weiboData.getJSONArray("digg_users");
                        for (int i = 0;i < diggUsersJa.length(); i++) {
                            ModelUser user = new ModelUser();
                            JSONObject jo = diggUsersJa.getJSONObject(i);
                            user.setUid(Integer.parseInt(jo.getString("uid")));
                            user.setUserName(jo.getString("uname"));
							user.setFace(jo.optString("avatar"));
							diggUsers.add(user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

				if (weiboData.has("source_info")
						&& weiboData.getString("source_info").toString().indexOf("{") != -1
						&& weiboData.getString("source_info").toString().indexOf("}") != -1) {
					try {
						ModelWeibo transpond = new ModelWeibo(weiboData.getJSONObject("source_info"));
						this.setSourceWeibo(transpond);
					} catch (Exception e) {
						Log.v("weiboString","----------source_infoException----------"+ e.getMessage());
					}
				} else if (weiboData.has("feed_info")) {
					try {
						ModelWeibo transpond = new ModelWeibo(weiboData.getJSONObject("feed_info"));
						this.setSourceWeibo(transpond);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (weiboData.has("address")) {
					setAddress(weiboData.getString("address"));
				}

				if (weiboData.has("latitude")) {
					String latitude = weiboData.getString("latitude");
					setLatitude(latitude);
				}

				if (weiboData.has("longitude")) {
					String longitude = weiboData.getString("longitude");
					setLongitude(longitude);
				}
			}
		} catch (JSONException e) {
			throw new WeiboDataInvalidException(e.getMessage());
		}
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	/**
	 * 是否转发的微博 1 转发微博 其他不是
	 */
	private int is_repost = -1;

	/**
	 * 是否转发的微博 返回1表示转发微博 其他不是
	 * 
	 * @return
	 */
	public int getIsRepost() {
		return is_repost;
	}

	/**
	 * 设置是否转发的微博，1表示转发微博 其他不是
	 * 
	 * @param transpondId
	 */
	public void setTranspondId(int transpondId) {
		this.is_repost = transpondId;
	}

	/**
	 * 获取原微博的id
	 * 
	 * @return
	 */
	public int getSid() {
		return sid;
	}

	/**
	 * 设置原微博的id
	 * 
	 * @param sid
	 */
	public void setSid(int sid) {
		this.sid = sid;
	}

	/**
	 * 获取赞的数目
	 * 
	 * @return
	 */
	public int getDiggNum() {
		return diggNum;
	}

	/**
	 * 设置赞的数目
	 * 
	 * @param diggNum
	 */
	public void setDiggNum(int diggNum) {
		this.diggNum = diggNum;
	}

	/**
	 * 判断微博是否已经被删除
	 * 
	 * @return >0表示微博已经删除，0表示未删除
	 */
	public int isWeiboIsDelete() {
		return isDel;
	}

	/**
	 * 是否已经删除
	 * 
	 * @param isDel
	 */
	public void setWeiboIsDelelet(int isDel) {
		this.isDel = isDel;
	}

	/**
	 * 是否已收藏
	 * 
	 * @return true 已经收藏，false 未收藏
	 */
	public boolean isFavorited() {
		return isFavorited;
	}

	/**
	 * 设置微博是否已经收藏
	 * 
	 * @param favorited
	 */
	public void setFavorited(boolean favorited) {
		this.isFavorited = favorited;
	}

	/**
	 * 微博内容，一般还需要UnitSociax.showLinkView处理一下内容里面的表情、连接和@用户
	 * 
	 * @return
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 微博内容，@用户的时候需要把@XXX后面添加空格
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取用户头像地址,默认是middle大小
	 */
	@Override
	public String getUserface() {
		return userface;
	}

	/**
	 * 设置用户头像地址
	 * 
	 * @param userface
	 *            头像地址url
	 */
	public void setUserface(String userface) {
		this.userface = userface;
	}

	/**
	 * 设置微博的字符串
	 * 
	 * @param userJsonString
	 */
	public void setWeiboJsonString(String userJsonString) {
		this.weiboJsonString = userJsonString;
	}

	/**
	 * 获取微博的json字符串
	 * 
	 * @return
	 */
	public String getWeiboJsonString() {
		return this.weiboJsonString;
	}

	/**
	 * 是否有评论权限
	 * 
	 * @return true 是
	 */
	public boolean isCan_comment() {
		return can_comment;
	}

	/**
	 * 设置是否可以评论
	 * 
	 * @param can_comment
	 */
	public void setCan_comment(boolean can_comment) {
		this.can_comment = can_comment;
	}

	/**
	 * 微博的id，也就是feed_id
	 * 
	 * @return
	 */
	public int getWeiboId() {
		return weiboId;
	}

	/**
	 * 设置微博的id
	 * 
	 * @param weiboId
	 */
	public void setWeiboId(int weiboId) {
		this.weiboId = weiboId;
	}

	/**
	 * 发布的毫秒时间戳， 同ctime，需要根据服务端返回来取值
	 * 
	 * @return
	 */
	public int getTimestamp() {
		return timeStamp;
	}

	/**
	 * 设置发布的毫秒时间戳
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(int timestamp) {
		this.timeStamp = timestamp;
	}

	/**
	 * 发布的毫秒时间戳，同timeStamp，需要根据服务端返回来取值
	 * 
	 * @return
	 */
	public String getCtime() {
		return cTime;
	}

	/**
	 * 设置发布的时间戳
	 * 
	 * @param cTime
	 */
	public void setCtime(String cTime) {
		this.cTime = cTime;
	}

	/**
	 * 微博来源
	 * 
	 * @return
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * 设置微博来源，这里一边是安卓客户端
	 * 
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * 获取原来微博
	 * 
	 * @return 如果没有反回null，否则返回原微博
	 */
	public ModelWeibo getSourceWeibo() {
		return sourceWeibo;
	}

	/**
	 * 设置原微博
	 * 
	 * @param transpond
	 */
	public void setSourceWeibo(ModelWeibo transpond) {
		this.sourceWeibo = transpond;
	}

	/**
	 * 
	 * 获取微博的类型
	 * 
	 * @return 一.普通微博 post; 二.图片微博 postimage ; 三.视频微博 postvideo ;
	 *         四.文件微博postfile; 五.转发生成的微博repost,这时候需要根据sourceWeibo来判断类型:
	 *         此时根据source_info的type可以把被转发微博分为: 1换发普通微博 post 2 转发图片微博postimage 3
	 *         转发视频微博postvideo 4 文件微博转发postfile 六.帖子/知识转发过来的微博
	 *         weiba_post/blog_post; 七.转发帖子/知识再转发生成的的微博weiba_repost blog_repost;
	 *         八.未知类型 return "";
	 */
	public String getType() {
		if (type == null) {
			return "";
		}
		return type;
	}

	/**
	 * 设置转发类型
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 发布微博的用户的id
	 * 
	 * @return
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * 设置发布微博的用户的id
	 * 
	 * @param uid
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * 发布微博的用户的名字
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置发布微博的用户的名字
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 是否已经赞 1表示已经赞 0表示未赞
	 * 
	 * @return
	 */
	public int getIsDigg() {
		return isDigg;
	}

	/**
	 * 设置是否已赞
	 * 
	 * @param isDigg
	 */
	public void setIsDigg(int isDigg) {
		this.isDigg = isDigg;
	}

	/**
	 * 获取微博评论数目
	 * 
	 * @return
	 */
	public int getCommentCount() {
		return commentCount;
	}

	/**
	 * 设置微博评论数目
	 * 
	 * @param comment
	 */
	public void setCommentCount(int comment) {
		this.commentCount = comment;
	}

	/**
	 * 微博转发数目
	 * 
	 * @return
	 */
	public int getTranspondCount() {
		return transpondCount;
	}

	/**
	 * 设置微博转发数目
	 * 
	 * @param transpondCount
	 */
	public void setTranspondCount(int transpondCount) {
		this.transpondCount = transpondCount;
	}

	/**
	 * 是否带有图片
	 * 
	 * @return
	 */
	public boolean hasImage() {
		return this.type.equals(ModelWeibo.POSTIMAGE);
	}

	/**
	 * 是否带有文件
	 */
	public boolean hasFile() {
		return this.type.equals(ModelWeibo.POSTIFILE);
	}

	/**
	 * 是否带有视频
	 * 
	 * @return
	 */
	public boolean hasVideo() {
		return this.type.equals(ModelWeibo.POSTVIDEO);
	}

	/**
	 * 获取附件列表如果type为postimage时候这里是图片集合，如果是postfile时候为文件集合
	 * 
	 * @return ListData<ImageAttach>
	 */
	public ListData<ModelImageAttach> getAttachImage() {
		return attachs;
	}

	/**
	 * 设置附件列表
	 * 
	 * @param attachs
	 */
	public void setAttachImage(ListData<ModelImageAttach> attachs) {
		this.attachs = attachs;
	}

	private ListData<Comment> comments = new ListData<Comment>();// 评论信息
	private ListData<SociaxItem> commentList = new ListData<SociaxItem>();

	public ListData<SociaxItem> getCommentList() {
		return commentList;
	}

	public void setCommentList(ListData<SociaxItem> commentList) {
		this.commentList = commentList;
	}

	public ListData<Comment> getComments() {
		return comments;
	}

	public void setComments(ListData<Comment> comments) {
		this.comments = comments;
	}

	/**
	 * 设置视频附件
	 * 
	 * @return
	 */
	public ModelVideo getAttachVideo() {
		return attachVideo;
	}

	/**
	 * 视频附件，当type为postvideo的时候有否则返回null
	 * 
	 * @param video
	 */
	public void setAttachVideo(ModelVideo video) {
		this.attachVideo = video;
	}

	/**
	 * 暂时不知道作用，
	 */
	private String tempJsonString;

	/**
	 * 暂时不知道作用
	 * 
	 * @return
	 */
	public String getTempJsonString() {
		return tempJsonString;
	}

	/**
	 * 暂时不知道作用
	 * 
	 * @param tempJsonString
	 */
	public void setTempJsonString(String tempJsonString) {
		this.tempJsonString = tempJsonString;
	}

	public static final int MAX_CONTENT_LENGTH = 140;

	/**
	 * 检测微博长度
	 * 
	 * @return
	 */
	public boolean checkContent() {
		return this.content.length() <= MAX_CONTENT_LENGTH;
	}

	/**
	 * 检测微博长度
	 * 
	 * @param content
	 * @return
	 */
	public boolean checkContent(String content) {
		return content.length() <= MAX_CONTENT_LENGTH;
	}

	/**
	 * 评论是否为空
	 * 
	 * @return
	 */
	public boolean isNullForComment() {
		return this.commentCount == 0;
	}

	/**
	 * 检测内容是否为空
	 * 
	 * @return
	 */
	public boolean isNullForContent() {
		return this.content == null || this.content.equals("");
	}

	/**
	 * 检测提交时间是否为空
	 * 
	 * @return
	 */
	public boolean isNullForCtime() {
		return this.cTime == null || this.cTime.equals("");
	}

	/**
	 * 检测微博id是否为空
	 * 
	 * @return
	 */
	public boolean isNullForWeiboId() {
		return this.weiboId == 0;
	}

	/**
	 * 检测uid是否为空
	 * 
	 * @return
	 */
	public boolean isNullForUid() {
		return this.uid == 0;
	}

	/**
	 * 检测时间戳是否为空
	 * 
	 * @return
	 */
	public boolean isNullForTimestamp() {
		return this.timeStamp == 0;
	}

	/**
	 * 检测转发是否转发的微博
	 * 
	 * @return true 是转发，false 不是转发
	 */
	public boolean isNullForTranspond() {
		if (this.isNullForTranspondId())
			return true;
		else
			return false;
	}

	/**
	 * 检测是转发 1 转发 0 不是转发
	 * 
	 * @return true 转发，false非转发
	 */
	public boolean isNullForTranspondId() {
		return this.is_repost == 1;
	}

	/**
	 * 检测转发数是否为空
	 * 
	 * @return
	 */
	public boolean isNullForTranspondCount() {
		return this.transpondCount == 0;
	}

	/**
	 * 检测用户头像是否为空
	 * 
	 * @return
	 */
	public boolean isNullForUserFace() {
		return this.userface == null || this.userface.equals(NULL);
	}

	/**
	 * 检测用户名字是否为空
	 * 
	 * @return
	 */
	public boolean isNullForUserName() {
		return this.username == null || this.username.equals(NULL);
	}

	/**
	 * 微博是否已赞
	 * 
	 * @return
	 */
	public boolean isDigg() {
		return getIsDigg() == 1;
	}

	/**
	 * 设置微博是否已赞
	 * 
	 * @param isdigg
	 */
	public void setIsDigg(boolean isdigg) {
		if (isdigg) {
			setIsDigg(1);
		} else {
			setIsDigg(0);
		}
	}

	/**
	 * 推荐频道的微博必定带有频道id
	 * 
	 * @return
	 */
	public int getChannel_category_id() {
		return channel_category_id;
	}

	/**
	 * 设置频道微博带有的频道id
	 * 
	 * @param channel_category_id
	 */
	public void setChannel_category_id(int channel_category_id) {
		this.channel_category_id = channel_category_id;
	}

	/**
	 * 推荐微博列表里面的微博必定带有频道名称
	 * 
	 * @return
	 */
	public String getChannel_name() {
		return channel_name;
	}

	/**
	 * 微博所在频道名称
	 * 
	 * @param channel_name
	 */
	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

    public ListData<ModelUser> getDiggUsers() {
        return diggUsers;
    }

    public void setDiggUsers(ListData<ModelUser> diggUsers) {
        this.diggUsers = diggUsers;
    }
}
