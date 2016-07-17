package com.thinksns.sociax.t4.android.fragment;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.video.ActivityVideoDetail;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 个人主页相册 本类由于ActivityUserInfo弃用而弃用，但是暂时保留11/24
 * 
 * @author wz
 * @date 2014-11-5
 * @version 1.0
 */
public class FragmentUserInfoAlbum extends FragmentUserInfo {

	private TextView tv_photo_count, tv_video_count;
	private ImageView img_photo_one, img_photo_two, img_photo_three,
			img_video_one, img_video_two, img_video_three;
	private Thinksns application;

	@Override
	public void initView() {
		application = (Thinksns) getActivity().getApplicationContext();
		
		tv_photo_count = (TextView) findViewById(R.id.tv_photo_count);
		tv_video_count = (TextView) findViewById(R.id.tv_video_count);

		img_photo_one = (ImageView) findViewById(R.id.img_photo_one);
		img_photo_two = (ImageView) findViewById(R.id.img_photo_two);
		img_photo_three = (ImageView)findViewById(R.id.img_photo_three);

		img_video_one = (ImageView) findViewById(R.id.img_vedio_one);
		img_video_two = (ImageView) findViewById(R.id.img_vedio_two);
		img_video_three = (ImageView)findViewById(R.id.img_vedio_three);
	}

	@Override
	public void initListener() {

	}

	@Override
	public void initData() {
		tv_photo_count.setText(user.getPhotoCount());
		tv_video_count.setText(user.getVdeioCount());

		if (user.getVedio() != null) {
			JSONArray vedioarray = user.getVedio();
			switch (vedioarray.length()) {
			case 1:
				try {
					
					application.displayImage(vedioarray.getJSONObject(0).getString("flashimg"),img_video_one);
					
					img_photo_one.setTag(vedioarray.getJSONObject(0)
							.getString("flashvar"));
					img_photo_one.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(getActivity(),ActivityVideoDetail.class);
							intent.putExtra("url",(String) v.getTag());
							getActivity().startActivity(intent);
						}
					});
					img_video_two.setVisibility(View.GONE);
					img_video_three.setVisibility(View.GONE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					
					application.displayImage(vedioarray.getJSONObject(0).getString("flashimg"),img_video_one);
					application.displayImage(vedioarray.getJSONObject(1).getString("flashimg"),img_video_two);
					
					img_photo_one.setTag(vedioarray.getJSONObject(0)
							.getString("flashvar"));
					img_photo_one.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(getActivity(),ActivityVideoDetail.class);
							intent.putExtra("url",(String) v.getTag());
							getActivity().startActivity(intent);
						}
					});
					img_photo_two.setTag(vedioarray.getJSONObject(0)
							.getString("flashvar"));
					img_photo_two.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(getActivity(),ActivityVideoDetail.class);
							intent.putExtra("url",(String) v.getTag());
							getActivity().startActivity(intent);
						}
					});
					img_video_three.setVisibility(View.GONE);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;

			case 3:
				try {
					
					application.displayImage(vedioarray.getJSONObject(0).getString("flashimg"),img_video_one);
					application.displayImage(vedioarray.getJSONObject(1).getString("flashimg"),img_video_two);
					application.displayImage(vedioarray.getJSONObject(2).getString("flashimg"),img_video_three);
					
					img_photo_one.setTag(vedioarray.getJSONObject(0)
							.getString("flashvar"));
					img_photo_one.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(getActivity(),ActivityVideoDetail.class);
							intent.putExtra("url",(String) v.getTag());
							getActivity().startActivity(intent);
						}
					});img_photo_two.setTag(vedioarray.getJSONObject(0)
							.getString("flashvar"));
					img_photo_two.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(getActivity(),ActivityVideoDetail.class);
							intent.putExtra("url",(String) v.getTag());
							getActivity().startActivity(intent);
						}
					});img_photo_three.setTag(vedioarray.getJSONObject(0)
							.getString("flashvar"));
					img_photo_three.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(getActivity(),ActivityVideoDetail.class);
							intent.putExtra("url",(String) v.getTag());
							getActivity().startActivity(intent);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			default:
				try {
					
//					ImageLoader.getInstance().displayImage(vedioarray.getJSONObject(0)
//							.getString("flashimg"), img_video_one, Thinksns.getOptions());
//					ImageLoader.getInstance().displayImage(vedioarray.getJSONObject(1)
//							.getString("flashimg"), img_video_two, Thinksns.getOptions());
//					ImageLoader.getInstance().displayImage(vedioarray.getJSONObject(2)
//							.getString("flashimg"), img_video_three, Thinksns.getOptions());
					
					application.displayImage(vedioarray.getJSONObject(0).getString("flashimg"),img_video_one);
					application.displayImage(vedioarray.getJSONObject(1).getString("flashimg"),img_video_two);
					application.displayImage(vedioarray.getJSONObject(2).getString("flashimg"),img_video_three);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			}
		}

		if (user.getPhoto() != null) {
			JSONArray photoarray = user.getPhoto();
			switch (photoarray.length()) {
			case 1:
				try {
					
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(0)
//							.getString("image_url"), img_photo_one, Thinksns.getOptions());
					
					application.displayImage(photoarray.getJSONObject(0).getString("image_url"),img_photo_one);
					
					img_photo_two.setVisibility(View.GONE);
					img_photo_three.setVisibility(View.GONE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(0)
//							.getString("image_url"), img_photo_one, Thinksns.getOptions());
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(1)
//							.getString("image_url"), img_photo_two, Thinksns.getOptions());
					
					application.displayImage(photoarray.getJSONObject(0).getString("image_url"),img_photo_one);
					application.displayImage(photoarray.getJSONObject(1).getString("image_url"),img_photo_two);
					
					img_photo_three.setVisibility(View.GONE);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;

			case 3:
				try {
					
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(0)
//							.getString("image_url"), img_photo_one, Thinksns.getOptions());
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(1)
//							.getString("image_url"), img_photo_two, Thinksns.getOptions());
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(2)
//							.getString("image_url"), img_photo_three, Thinksns.getOptions());
					
					application.displayImage(photoarray.getJSONObject(0).getString("image_url"),img_photo_one);
					application.displayImage(photoarray.getJSONObject(1).getString("image_url"),img_photo_two);
					application.displayImage(photoarray.getJSONObject(2).getString("image_url"),img_photo_three);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			default:
				try {
					
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(0)
//							.getString("image_url"), img_photo_one, Thinksns.getOptions());
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(1)
//							.getString("image_url"), img_photo_two, Thinksns.getOptions());
//					ImageLoader.getInstance().displayImage(photoarray.getJSONObject(2)
//							.getString("image_url"), img_photo_three, Thinksns.getOptions());
					
					application.displayImage(photoarray.getJSONObject(0).getString("image_url"),img_photo_one);
					application.displayImage(photoarray.getJSONObject(1).getString("image_url"),img_photo_two);
					application.displayImage(photoarray.getJSONObject(2).getString("image_url"),img_photo_three);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			}
		}
	}


	public static FragmentUserInfoAlbum newInstance(ModelUser user) {
		FragmentUserInfoAlbum fragment = new FragmentUserInfoAlbum();
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", user);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_userinfo_album;
	}
}
