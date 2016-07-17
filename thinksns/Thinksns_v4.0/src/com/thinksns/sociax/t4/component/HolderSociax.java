package com.thinksns.sociax.t4.component;

import android.view.View;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.*;

import com.thinksns.sociax.t4.android.image.SmartImageView;
import com.thinksns.sociax.t4.android.img.RoundCornerImageView;
import com.thinksns.sociax.t4.android.widget.roundimageview.RoundedImageView;

/**
 * 类说明： 所有list 的 view holder，如果当前已经有该类型的变量则直接用，否则在后面添加
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-15
 */
public class HolderSociax {

    /***********
     * weibo
     ************/
    public TextView tv_weibo_user_name;
    public RoundedImageView iv_weibo_user_head;
    public TextView tv_weibo_ctime;
    public TextView tv_weibo_from;
    public TextView tv_weibo_content;
    public ImageView iv_weibo_image;
    public TextView tv_empty_content;

    // public TextView tv_weibo_imgNum;
    public GridView gv_weibo;
    public FrameLayout rl_image;
    public ViewStub stub_image;
    public ViewStub stub_image_group;
    public ViewStub stub_digg;
    public ViewStub stub_comment;
    public ViewStub stub_weiba;
    public ViewStub stub_media;
    public ViewStub stub_transport_weibo;
    public ViewStub stub_transport_weiba;
    public ViewStub stub_file;
    public ViewStub stub_add_follow;
    public LinearLayout ll_other_files_image;
    public LinearLayout ll_media;
    public ImageView iv_dig;
    public ImageView iv_dig_icon;
    public TextView tv_dig_num;
    public RelativeLayout rl_comment;
    public TextView tv_comment_num;
    public TextView tv_add_comment;
    public ImageView img_more;
    // caoligai 修改,在“更多”外层包裹一个相对布局来接收点击事件，从而扩大点击范围
    public RelativeLayout rl_more;
    public LinearLayout ll_source_content_layout;
    public View view_weibo_divide;
    public ImageView iv_weibo_comment_bg;
    public LinearLayout ll_weibo_main;
    /**
     * 评论我的  页面视频的播放按钮 caoligai 添加
     */
    public ImageView iv_comment_play;

    /********
     * channel
     *********/

    public RoundedImageView img_channel_icon;
    public TextView tv_channel_des;
    public TextView tv_channel_name;
    public TextView tv_channel_follow;
    public SmartImageView iv_channel_image_big;
    public SmartImageView iv_channel_image_small1;
    public SmartImageView iv_channel_image_small2;
    public SmartImageView iv_channel_image_small3;
    public ImageView img_error_layout;
    public TextView tv_error_layout;

    /**********
     * comment
     *************/
    public TextView tv_comment_user_name;
    public ImageView iv_comment_user_head;
    public TextView tv_comment_ctime;
    public TextView tv_comment_from;
    public TextView tv_comment_content;
    public ImageView img_source_weibo_bg;
    public TextView tv_source_weibo_content;

    public LinearLayout ll_content, ll_empty;

    /***********
     * chat
     *************/
    public ImageView img_user_header;
    public TextView tv_chat_username;
    public TextView tv_chat_content;
    public TextView tv_chat_noticecount;
    public TextView tv_chat_ctime;
    public TextView tvLetter;
    public TextView tvTitle;
//	public TextView tv_chat_notify;

    /***********
     * chatMessage
     ********/
    public LinearLayout ll_chat_my, ll_chat_other, ll_chat_my_voice, ll_chat_from_yuyin;

    public String str_chat_msg_type;
    public ImageView img_chat_msg_my_userheader;
    public ImageView img_chat_msg_my_photo;
    //	public RoundCornerImageView img_chat_msg_my_photo;
    public TextView tv_chat_msg_time;
    public TextView tv_chat_voice_times_to;
    public TextView tv_chat_voice_times_from;
    public TextView tv_chat_msg_my_content;
    public TextView tv_chat_my_name;
    public TextView tv_chat_other_name;
    public ImageView img_chat_msg_my_yuyin;
    public ImageView img_chat_msg_other_userheader;
    public ImageView img_chat_msg_ohter_yuyin;
    //	public RoundCornerImageView img_chat_msg_other_photo;
    public ImageView img_chat_msg_other_photo;
    public TextView tv_chat_msg_other_content;
    public View ll_chat_msg_other;
    public View ll_chat_msg_my;

    /*****************************
     * 新聊天详情列表
     *************************************/
    public ImageView chat_item_from_head, chat_item_to_head;//双方头像
    public TextView tv_chat_time, tv_chat_notify;//显示的时间和通知
    public LinearLayout ll_chat_item_to, ll_chat_item_from, ll_chat_item_my;//双方的内容
    public RelativeLayout rl_chat_item_to_voice;
    public LinearLayout ll_chat_item_from_voice;//双方的内容

    //文本
    public TextView tv_chat_to_content, tv_chat_from_content;//双方的文本
    public RelativeLayout rl_chat_item_to;
    //图片
    public ImageView iv_chat_to_pic, iv_chat_from_pic, iv_chat_to_pic_bg, iv_chat_from_pic_bg;//双方的图片
    public ImageView iv_chat_to_pic_progress, iv_chat_from_pic_progress;//双方加载的图片
    public Button btn_send_error;//重发按钮
    public ProgressBar pb_send_pic;//发送图片的进度条
    //位置
    public ImageView iv_chat_to_position_pic, iv_chat_from_position_pic;//双方的位置图片
    public TextView tv_chat_to_position, tv_chat_from_position;//双方的位置文本
    public ImageView iv_chat_to_position_progress, iv_chat_from_position_progress;//双方加载的图片
    //语音
    public ImageView iv_to_voice, iv_from_voice;//双方语音的图片
    public TextView tv_to_voice_length, tv_from_voice_length;//双方语音的长度
    public RelativeLayout rl_chat_to_voice, rl_chat_from_voice;//双方的父布局
    //卡片
    public ImageView iv_card_pic_to, iv_card_pic_from;//双方的卡片头像
    public TextView tv_chat_card_uname_to, tv_chat_card_uname_from;//双方的卡片名称
    public TextView tv_chat_card_detail_to, tv_chat_card_detail_from;//双方的卡片简介
    public LinearLayout ll_chat_card_to, ll_chat_card_from;//双方的卡片父布局

    /**************
     * userlist
     ****************/
    public ImageView tv_user_photo;
    public TextView tv_user_name;
    public TextView tv_user_content;
    public TextView tv_user_add;
    public LinearLayout ll_uname_adn;
    public RelativeLayout rl_select_chat_user;
    public RelativeLayout rl_rcd_item;
    public ImageView iv_chonsed;

    /**********
     * gift
     **************/
    public ImageView img_item_pic_1;
    public TextView tv_gift_1_name;
    public TextView tv_gift_1_price;

    public LinearLayout ll_gift_info;
    public LinearLayout ll_gift_1;
    public LinearLayout ll_gift_2;
    public LinearLayout ll_gift_3;
    public LinearLayout ll_gift_4;

    /**********
     * mycenter_home
     **************/
    public RelativeLayout rl_mycenter_home_following, rl_mycenter_home_follow,
            rl_mycenter_home_userinfo;
    public LinearLayout ll_mycenter_home_following,
            ll_mycenter_home_follow;
    public LinearLayout ll_myPic, ll_myVideo;
    public LinearLayout ll_myGiftContainor;
    public LinearLayout ll_follow_info, ll_following_list;
    public LinearLayout ll_follower_info, ll_followed_list, ll_photo_list, ll_video_list;
    public View view1, view2, view3, view4;

    public ImageView img_item_pic_2;
    public TextView tv_gift_2_name;
    public TextView tv_gift_2_price;
    public ImageView img_item_pic_3;
    public TextView tv_gift_3_name;
    public TextView tv_gift_3_price;
    public ImageView img_item_pic_4;
    public TextView tv_gift_4_name;
    public TextView tv_gift_4_price;
    public CheckBox cb_select;
    public TextView tv_user_info_follow;
    public TextView tv_user_info_following;
    public TextView tv_user_info_from;
    public LinearLayout ll_edit_info;
    public TextView tv_edit_info;
    public TextView tv_user_info_intro;
    public TextView tv_user_info_meili;
    public TextView tv_user_info_weiwang;
    public TextView tv_user_info_xp;
    public TextView tv_more_follow;
    public TextView tv_more_following;
    public TextView tv_tips_nogift;
    public TextView tv_tips_nofollow;
    public TextView tv_tips_nofollower;
    public ImageView img_follow_one;
    public ImageView img_follow_two;
    public ImageView img_follow_three;
    public ImageView img_follow_four;
    public ImageView img_follow_five;
    public TextView tv_follow_name1;
    public TextView tv_follow_name2;
    public TextView tv_follow_name3;
    public TextView tv_follow_name4;
    public TextView tv_follow_name5;
    public ImageView img_following_one;
    public ImageView img_following_two;
    public ImageView img_following_three;
    public ImageView img_following_four;
    public ImageView img_following_five;
    public TextView tv_following_name1;
    public TextView tv_following_name2;
    public TextView tv_following_name3;
    public TextView tv_following_name4;
    public TextView tv_following_name5;
    public TextView tv_photo_count;
    public TextView tv_video_count;
    public ImageButton ib_photo_next,ib_video_next;
    public RelativeLayout rl_photo_more;
    public RelativeLayout rl_video_more;
    public TextView tv_tips_nopic;
    public TextView tv_tips_novedio;
    public ImageView img_photo_one;
    public ImageView img_photo_two;
    public ImageView img_photo_three;
    public ImageView img_photo_four;
    public ImageView img_video_one;
    public ImageView img_video_two;
    public ImageView img_video_three;
    public ImageView img_video_four;

    /************
     * task
     ***********/
    public TextView tv_task_name;
    public TextView tv_task_desc;
    public TextView tv_task_type;
    public TextView tv_task_status;
    public ImageView img_task_img;
    public ImageView img_delete;

    /************
     * DailyTask/MainTask
     *****************/
    public TextView tv_main_task_name, tv_main_task_complete_state, tv_main_task_detail_content, tv_main_task_detail_goal;
    public ImageView iv_task_complete, iv_task_medal;
    public View view_progress;

    /************
     * CopyTask
     *****************/
    public TextView tv_copy_task_name, tv_copy_task_complete_state, tv_copy_task_detail_content, tv_copy_task_detail_goal, tv_copy_task_last_count;
    public ImageView iv_copy_task_complete;
    public LinearLayout ll_copy_task_condition;

    /************
     * MedalPavilion
     *****************/
    public ImageView iv_medal;
    public TextView tv_medal_name;

    /************
     * AllGift
     *****************/
    public ImageView iv_all_gift;
    public TextView tv_gift_name, tv_gift_score, tv_gift_surplus;

    /************
     * MyGift
     *****************/
    public ImageView iv_my_gift;
    public TextView tv_id, tv_num, tv_my_gift_name, tv_my_gift_username, tv_my_gift_time;

    /************
     * ScoreRule
     *****************/
    public TextView tv_score_rule_name, tv_score_rule_exp, tv_score_rule_score;

    /************
     * ScoreDetail
     *****************/
    public TextView tv_my_score_detail_name, tv_my_score_detail_time, tv_my_score_detail_result;

    /************
     * TagCloud
     *****************/
    public TextView tv_tag_cloud, tv_del;

    /**********
     * topic
     ************/
    public TextView tv_topic_type;
    public TextView tv_topic_name;
    public TextView tv_topic_des;

    /************
     * weiba
     ********/
    public ViewStub stub_category;
    public ImageView img_weiba_icon1, img_weiba_icon2;
    public TextView tv_weiba_name;
    public TextView tv_weiba_des;
    public TextView tv_part_name;
    public ImageView img_posts_user;
    public TextView tv_post_uname, tv_post_ctime, tv_post_title, tv_post_info,
            tv_post_read, tv_post_comment;
    public LinearLayout ll_user_group;// 用户群组
    public ViewStub stub_uname_adn;
    public LinearLayout ll_weiba_isfollow_content, ll_weiba_notfollow_content;
    public ImageView img_weiba_bg;
    public TextView tv_weiba_title1,tv_weiba_title2;
    public TextView tv_weiba_top1;
    public TextView tv_weiba_top2;
    public TextView tv_weiba_digest;
    public TextView tv_weiba_isfollow;
    public TextView tv_member_count;    //微吧成员数或关注数
    public TextView tv_post_count;      //微吧帖子数
    public LinearLayout ll_weiba_top;
    public LinearLayout ll_weiba_digest;
    public TextView tv_weiba_intro, tv_hot_title;
    public TableLayout tl_imgs;
    public LinearLayout ll_digg, ll_digg_list;
    public TextView tv_digg_count;
    public ImageView img_comment_replay;
    public ImageView img_comment_userface;
    public LinearLayout ll_weiba_info;
    public TextView tv_last_part;
    public LinearLayout ll_part;
    public WebView wb_content;
    public TextView tv_tag;
    public TextView tv_weiba_follow;
    public LinearLayout ll_oauth_info;

    public ImageView iv_following_next,iv_followed_next;

    public TextView tv_oauth;
    public TextView tv_gift;
    public ImageView img_gift_one;
    public ImageView img_gift_two;
    public ImageView img_gift_three;
    public ImageView img_gift_four;
    public ImageView img_honner_one;
    public ImageView img_honner_two;
    public ImageView img_honner_three;
    public ImageView img_honner_four;
    public RelativeLayout rl_gift;
    public TextView tv_from_uname;
    public TextView tv_remind_new;
    public LinearLayout ll_chat_msg_my_card;
    public TextView tv_chat_msg_other_card_uname;
    public TextView tv_chat_msg_other_card_des;
    public RoundCornerImageView tv_chat_msg_other_card_userheader;
    public LinearLayout ll_chat_msg_other_card;
    public TextView tv_chat_msg_my_card_uname;
    public TextView tv_chat_msg_my_card_des;
    public RoundCornerImageView tv_chat_msg_my_card_userheader;
    public TextView tv_rank;

    public View img_vedio_one_bf;
    public View img_vedio_two_bf;
    public View img_vedio_three_bf;
    public View img_vedio_four_bf;
    public TextView tv_change_user_info;
    public LinearLayout ll_honner_info;
    public LinearLayout ll_honner;
    public TextView tv_ctime;
    public TextView tv_title;
    public TextView tv_des;
    public ImageView img_send;
    public View img_divider_footer;
    public View img_divider_header;
    public TextView tv_part_status;
    public RelativeLayout rl_task_content;
    public LinearLayout ll_hide_comment;
    public LinearLayout ll_hide_comment_list;
    public TextView tv_hide_comment_list;
    public LinearLayout ll_comment;
    public LinearLayout ll_comment_info;
    public LinearLayout ll_digg_info;
    public LinearLayout ll_textcontent;
    public LinearLayout ll_user_info;
    public RelativeLayout rl_manage;
    public LinearLayout ll_transport;
    public LinearLayout ll_from_weibo_content;
    public LinearLayout ll_from_weiba_content;
    public TextView tv_post_content;
    public TextView tv_post_from;
    public LinearLayout ll_post_no_delete;
    public TextView tv_post_is_delete;
    public ImageView img_rounduser_header;
    public ImageView img_top;

    public LinearLayout ll_weibo_content;
    public LinearLayout ll_center;
    public TextView ll_praise_list; // 点赞的布局 qcj添加
    public RelativeLayout ll_post;
    public ViewStub stub_part_name;
    public ViewStub stub_weiba_info;
    public TextView tv_date;
    public Button btn_tz;
    public RelativeLayout rl_tz;
    public ViewStub stub_address;


    //资讯
    public ImageView iv_icon;
    public TextView tv_subject;
    public TextView tv_abstract;
}
