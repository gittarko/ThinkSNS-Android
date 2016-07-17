package com.thinksns.sociax.t4.android.fragment;

import org.json.JSONArray;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.t4.android.user.ActivityFollowUser;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 个人主页的首页，需要传入intent user的详细信息或者只传递uid再从网上获取详细info
 * 本类由于ActivityUserInfo弃用而弃用，但是暂时保留11/24
 * 
 * @author wz
 * @date 2014-11-5
 * @version 1.0
 */
public class FragmentUserinfoHome extends FragmentUserInfo{
	protected static final String TAG = "FragmentUserinfoHome";
	private TextView tv_user_info_from, tv_user_info_intro,
			tv_user_info_weiwang, tv_user_info_xp, tv_user_info_meili,
			tv_user_info_follow, tv_user_info_following, tv_more_follow,
			tv_more_following;
	private ImageView img_follow_one, img_follow_two, img_follow_three,
			img_follow_four,img_follow_five, img_following_one, img_following_two,
			img_following_three, img_following_four,img_following_five;


	@Override
	public void initView() {
		
		tv_user_info_follow = (TextView)findViewById(R.id.tv_user_info_follow);
		tv_user_info_following = (TextView)findViewById(R.id.tv_user_info_following);
		tv_user_info_from = (TextView)findViewById(R.id.tv_user_info_from);
		tv_user_info_intro = (TextView)findViewById(R.id.tv_user_info_intro);
		tv_user_info_meili = (TextView)findViewById(R.id.tv_user_info_meili);
//		tv_user_info_weiwang = (TextView)findViewById(R.id.tv_user_info_weiwang);
		tv_user_info_xp = (TextView) findViewById(R.id.tv_user_info_xp);
//		tv_more_follow = (TextView) findViewById(R.id.tv_more_follow);
//		tv_more_following = (TextView) findViewById(R.id.tv_more_following);
		
//		img_follow_one = (ImageView)findViewById(R.id.img_follow_one);
//		img_follow_two = (ImageView)findViewById(R.id.img_follow_two);
//		img_follow_three = (ImageView)findViewById(R.id.img_follow_three);
//		img_follow_four = (ImageView)findViewById(R.id.img_follow_four);
//		img_follow_five = (ImageView)findViewById(R.id.img_follow_five);
		
//		img_following_one = (ImageView) findViewById(R.id.img_followed_one);
//		img_following_two = (ImageView)findViewById(R.id.img_followed_two);
//		img_following_three = (ImageView)findViewById(R.id.img_followed_three);
//		img_following_four = (ImageView)findViewById(R.id.img_followed_four);
//		img_following_five = (ImageView)findViewById(R.id.img_followed_five);
	}

	@Override
	public void initListener() {
		/**
		 * 我关注的人
		 */
		tv_more_following.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ActivityFollowUser.class);
				intent.putExtra("type", "following");
				intent.putExtra("uid", uid);
				startActivity(intent);
			}
		});
		/**
		 * 我的fensi
		 */
		tv_more_follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),ActivityFollowUser.class);
				intent.putExtra("type", "follow");
				intent.putExtra("uid", uid);
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {

		tv_user_info_follow.setText(user.getFollowedCount() + "");
		tv_user_info_following.setText(user.getFollowersCount() + "");
		tv_user_info_from.setText(user.getLocation());
		tv_user_info_intro.setText(user.getIntro());
		tv_user_info_meili.setText(user.getUserCredit().getCharm_value());
		tv_user_info_weiwang.setText("待完善");
		tv_user_info_xp.setText(user.getUserCredit().getExperience_value());
		JSONArray follower = user.getFollower_t4();
		final JSONArray following = user.getFollowing_t4();
		if (follower != null) {
			try {
				
//				img_follow_one.setImageUrl(follower.getJSONObject(0).getString("avatar"));
//				img_follow_two.setImageUrl(follower.getJSONObject(1).getString("avatar"));
//				img_follow_three.setImageUrl(follower.getJSONObject(2).getString("avatar"));
//				img_follow_four.setImageUrl(follower.getJSONObject(3).getString("avatar"));
				
				Glide.with(getActivity()).load(follower.getJSONObject(0).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_follow_one);
				
				Glide.with(getActivity()).load(follower.getJSONObject(1).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_follow_two);
				
				Glide.with(getActivity()).load(follower.getJSONObject(2).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_follow_three);
				
				Glide.with(getActivity()).load(follower.getJSONObject(3).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_follow_four);
				
				Glide.with(getActivity()).load(follower.getJSONObject(4).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_follow_five);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (following != null) {
			try {
//				img_following_one.setImageUrl(following.getJSONObject(0).getString("avatar"));
//				img_following_two.setImageUrl(following.getJSONObject(1).getString("avatar"));
//				img_following_three.setImageUrl(following.getJSONObject(2).getString("avatar"));
//				img_following_four.setImageUrl(following.getJSONObject(3).getString("avatar"));
				
				Glide.with(getActivity()).load(following.getJSONObject(0).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_following_one);
				
				Glide.with(getActivity()).load(following.getJSONObject(1).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_following_two);
				
				Glide.with(getActivity()).load(following.getJSONObject(2).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_following_three);
				
				Glide.with(getActivity()).load(following.getJSONObject(3).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_following_four);
				
				Glide.with(getActivity()).load(following.getJSONObject(4).getString("avatar"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.transform(new GlideCircleTransform(getActivity()))
				.crossFade()
				.into(img_following_five);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_userinfo_home;
	}
}
