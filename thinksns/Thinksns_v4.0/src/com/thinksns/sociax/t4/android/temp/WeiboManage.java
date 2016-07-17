package com.thinksns.sociax.t4.android.temp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.gimgutil.ImageFetcher;
import com.thinksns.sociax.gimgutil.ImageCache.ImageCacheParams;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.img.ActivityViewPager;
import com.thinksns.sociax.t4.android.video.ActivityVideoDetail;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.model.ModelVideo;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.UtilsListViewData;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.unit.TypeNameUtil;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 微博管理类
 * 
 * @author wz
 * @date 2014-9-24
 * @version 1.0
 */
public class WeiboManage {
	private Context context;
	private ModelWeibo weibo;// 被操作的微博
	private View activityView;
	private ImageFetcher imageFetcher;

	public WeiboManage() {
	}

	public WeiboManage(Context context, ModelWeibo weibo) {
		this.context = context;
		this.weibo = weibo;
		this.activityView = (View) ((Activity) context).getWindow()
				.getDecorView();

	}

	/********************************** 添加被转发的微博 *****************************/
	/**
	 * 给view添加被转发的微博
	 * 
	 * @param weibo
	 *            被转发的微博，一般是某个微博的原微博原微博
	 * @param view
	 *            添加到某个view上，该view需要包含以下内容： <LinearLayout
	 *            android:id="@+id/ll_transport"
	 *            android:layout_width="match_parent"
	 *            android:layout_height="wrap_content"
	 *            android:layout_gravity="center_vertical"
	 *            android:orientation="vertical" android:visibility="gone" >
	 * 
	 *            <TableLayout android:id="@+id/table_image"
	 *            android:layout_width="match_parent"
	 *            android:layout_height="wrap_content" android:visibility="gone"
	 *            > </TableLayout> </LinearLayout>
	 * @return
	 */
	public View appendTranspond() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout layout = new LinearLayout(activityView.getContext());
		layout.setLayoutParams(lp);

		TableLayout tableLayout = new TableLayout(context);
		tableLayout.setLayoutParams(lp);

		layout.setBackgroundResource(R.drawable.reviewboxbg);
		LinearLayout tranLayout = (LinearLayout) activityView
				.findViewById(R.id.ll_transport);
		tranLayout.setVisibility(View.VISIBLE);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpText.gravity = Gravity.CENTER;
		TextView content = null;
		if (weibo.isWeiboIsDelete() == 1) {
			content = new TextView(context);
			content.setText("该分享已删除");
		} else {
			content = UtilsListViewData
					.setWeiboTransportContent(context, weibo);
		}
		removeViews(layout);
		tranLayout.removeAllViews();
		tranLayout.addView(content, lpText);
		tranLayout.addView(tableLayout);
		if (weibo.isWeiboIsDelete() != 1) {
			android.widget.TableRow.LayoutParams imlp = new android.widget.TableRow.LayoutParams(
					SociaxUIUtils.dip2px(context, 80), SociaxUIUtils.dip2px(
							context, 80));
			imlp.setMargins(8, 0, 0, 8);
			if (weibo.hasImage() && weibo.getAttachImage() != null) {
				// LinearLayout.LayoutParams lpImage = new
				// LinearLayout.LayoutParams(
				// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpImage.setMargins(0, 0, 0, 5);

				tableLayout.setStretchAllColumns(false);
				final List<ModelPhoto> photoList = new ArrayList<ModelPhoto>();
				// 生成10行，8列的表格
				for (int row = 0; row < weibo.getAttachImage().size(); row = row + 2) {
					TableRow tableRow = new TableRow(context);
					for (int col = row; col < row + 2
							&& col < weibo.getAttachImage().size(); col++) {
						final ImageView image = new ImageView(
								activityView.getContext());

						image.setScaleType(ScaleType.FIT_XY);
						ModelPhoto p = new ModelPhoto();
						p.setId(col);
						p.setUrl(((ModelImageAttach)(weibo.getAttachImage()).get(col)).getOrigin());
						photoList.add(p);
						image.setTag(col);
						image.setId(AppConstant.IMAGE_VIEW);
						if (getmContentImageFetcher() != null) {
							getmContentImageFetcher().loadImage(((ModelImageAttach)(weibo.getAttachImage()).get(col)).getOrigin(),
									image, null);
						}

						image.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent i = new Intent(context,
										ActivityViewPager.class);
								i.putExtra("index", (image.getTag().toString()));
								i.putParcelableArrayListExtra(
										"photolist",
										(ArrayList<? extends Parcelable>) photoList);
								context.startActivity(i);

							}
						});
						// tv用于显示
						TextView tv = new TextView(context);
						tv.setText(col + "");
						tableRow.addView(image, imlp);
					}
					// 新建的TableRow添加到TableLayout
					tableLayout.addView(tableRow, new TableLayout.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
				}
			}

			if (weibo.hasFile()) {
				LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lpImage.gravity = Gravity.CENTER_VERTICAL;
				if (weibo.getAttachImage() != null) {
					for (int i = 0; i < weibo.getAttachImage().size(); i++) {
						TextView tx = new TextView(activityView.getContext());
						tx.setPadding(0, 0, 0, 0);
						tx.setGravity(Gravity.CENTER_VERTICAL);
						tx.setTextColor(activityView.getResources().getColor(
								R.color.main_link_color));
						tx.setCompoundDrawablesWithIntrinsicBounds(
								TypeNameUtil.getDomLoadImg(((ModelImageAttach)weibo.getAttachImage()
										.get(i)).getName()), 0, 0, 0);
						tx.setCompoundDrawablePadding(10);
						tx.setBackgroundResource(R.drawable.reviewboxbg);
						tx.setText(((ModelImageAttach)weibo.getAttachImage().get(i)).getName());
						tranLayout.addView(tx, lpImage);
					}
				}
			}
		}
		if (weibo.hasVideo()) {
			LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// lpImage.gravity = Gravity.CENTER_VERTICAL;
			lpImage.setMargins(12, 0, 0, 12);

			FrameLayout videoLayout = (FrameLayout) View.inflate(context,
					R.layout.weibo_video_item, null);
			ImageView videoImage = (ImageView) videoLayout
					.findViewById(R.id.iv_video);
			ImageView videoPlay = (ImageView) videoLayout
					.findViewById(R.id.iv_play);
			final ModelVideo myVideo = weibo.getAttachVideo();
			try {
				if (getmContentImageFetcher() != null
						&& myVideo.getVideoImgUrl() != null) {
					getmContentImageFetcher().loadImage(
							myVideo.getVideoImgUrl(), videoImage, null);
				}
				videoPlay.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bundle bundle = new Bundle();
						bundle.putInt("weibo_id", weibo.getWeiboId());
						bundle.putInt("video_from", 1);
						bundle.putString("str_video_url",
								myVideo.getVideoDetail());
						bundle.putInt("is_digg", weibo.getIsDigg());
						ActivityStack.startActivity((Activity) context,
								ActivityVideoDetail.class, bundle);
					}
				});
			} catch (Exception e) {
				System.err.println("add voide image errro " + e.toString());
			}
			tranLayout.addView(videoLayout, lpImage);
		}
		layout.setId(AppConstant.TRANSPOND_LAYOUT);
		return layout;
	}

	protected void removeViews(LinearLayout layout) {
		// ImageBroder image = (ImageBroder) layout.findViewById(IMAGE_VIEW);

		ImageView image = (ImageView) layout
				.findViewById(AppConstant.IMAGE_VIEW);
		LinearLayout transpond = (LinearLayout) layout
				.findViewById(AppConstant.TRANSPOND_LAYOUT);
		LinearLayout weibapost = (LinearLayout) layout
				.findViewById(AppConstant.WEIBA_VIEW);

		if (image != null) {
			layout.removeViewInLayout(image);
		}

		if (transpond != null) {
			layout.removeViewInLayout(transpond);
		}
		if (weibapost != null) {
			layout.removeViewInLayout(weibapost);
		}
	}

	public ImageFetcher getmContentImageFetcher() {
		int contentImageSize = 100;
		if (imageFetcher == null) {
			ImageCacheParams contentCacheParams = new ImageCacheParams(context,
					AppConstant.CONTET_IMAGE_CACHE_DIR);
			// Set memory cache to 25% of mem class
			contentCacheParams.setMemCacheSizePercent(context, 0.25f);
			imageFetcher = new ImageFetcher(context, contentImageSize);
			imageFetcher.setLoadingImage(R.drawable.bg_loading);
			imageFetcher.addImageCache(contentCacheParams);
			imageFetcher.setExitTasksEarly(false);
		}
		return imageFetcher;
	}

	/********************************** 添加被转发的微博 *****************************/

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ModelWeibo getWeibo() {
		return weibo;
	}

	public void setWeibo(ModelWeibo weibo) {
		this.weibo = weibo;
	}

	public View getActivityView() {
		return activityView;
	}

	public void setActivityView(View activityView) {
		this.activityView = activityView;
	}

}
