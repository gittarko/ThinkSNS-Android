package com.thinksns.sociax.t4.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.gift.ActivityGiftExchange;
import com.thinksns.sociax.t4.model.ModelMyGifts;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 礼物列表，实际上是listview，每个item显示多个礼物
 * 
 * @author wz
 * @date 2014-11-13
 * @version 1.0
 */
public class ListMyGift extends ListSociax {
	private static final String TAG = "WeiboList";
	private Context mContext;
	private ImageView iv_my_gift;
	private TextView tv_my_gift_say, tv_my_gift_username, tv_my_gift_time;
	View view;
	private AlertDialog.Builder builder = null;
	private Dialog dialog=null;
	private Button btn_ok;
	private ImageView iv_delete;
	private Thinksns application;
	
	public ListMyGift(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext=context;
		application = (Thinksns) context.getApplicationContext();
	}

	public ListMyGift(Context context) {
		super(context);
		this.mContext=context;
		application = (Thinksns) context.getApplicationContext();
	}

	@Override
	protected void onClick(View view, int position, long id) {
		if (view.getId() == R.id.footer_content) {
			ImageView iv = (ImageView) view.findViewById(R.id.anim_view);
			iv.setVisibility(View.VISIBLE);
			Anim.refresh(getContext(), iv);
			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) this.getAdapter();
			AdapterSociaxList adapter = (AdapterSociaxList) headerAdapter.getWrappedAdapter();
			adapter.animView = iv;
			adapter.doRefreshFooter();
		}else {
			final ModelMyGifts modelGetGift=(ModelMyGifts)view.getTag(R.id.my_get_gift);
			final ModelMyGifts modelSendGift=(ModelMyGifts)view.getTag(R.id.my_send_gift);
			
			builder =new AlertDialog.Builder(mContext);
			LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view1=inflater.inflate(R.layout.dialog_my_gift, null);
			builder.setView(view1);
			dialog= builder.show();
			Window window=dialog.getWindow();
			ColorDrawable colorDrawable=new ColorDrawable(0);
			window.setBackgroundDrawable(colorDrawable);
			
			btn_ok = (Button)view1.findViewById(R.id.btn_ok);
			
			iv_my_gift = (ImageView)view1.findViewById(R.id.iv_my_gift);
			tv_my_gift_say = (TextView)view1.findViewById(R.id.tv_my_gift_say);
			tv_my_gift_username = (TextView)view1.findViewById(R.id.tv_my_gift_username);
			tv_my_gift_time = (TextView)view1.findViewById(R.id.tv_my_gift_time);
			
			iv_delete = (ImageView)view1.findViewById(R.id.iv_delete);
			iv_delete.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					dialog.dismiss();
					return false;
				}
			});
			if (modelGetGift!=null) {
				
				Glide.with(mContext).load(modelGetGift.getImage())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.crossFade()
				.into(iv_my_gift);
				
	    		tv_my_gift_say.setText(modelGetGift.getSay());
	    		tv_my_gift_username.setText("赠送人："+modelGetGift.getOutUserName());
	    		tv_my_gift_time.setText(modelGetGift.getDate());
	    		
	    		if (modelGetGift!=null&&modelGetGift.getCate().equals("1")) {
	    			btn_ok.setVisibility(View.VISIBLE);
	    			
	    			btn_ok.setOnClickListener(new OnClickListener() {
	    				@Override
	    				public void onClick(View v) {
	    					Intent intent=new Intent(mContext,ActivityGiftExchange.class);
	    					modelGetGift.setMax(modelGetGift.getNum());
	    					intent.putExtra("modelMyGift", modelGetGift);
	    					intent.putExtra("FLAG", "transfer");
	    					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    					mContext.startActivity(intent);
	    					dialog.dismiss();
	    				}
	    			});
	    		}
			}else if(modelSendGift!=null){
//				ImageLoader.getInstance().displayImage(modelSendGift.getImage(), iv_my_gift, Thinksns.getOptions());
				
				application.displayImage(modelSendGift.getImage(),iv_my_gift);
				
	    		tv_my_gift_say.setText(modelSendGift.getSay());
	    		tv_my_gift_username.setText("被赠送人："+modelSendGift.getInUserName());
	    		tv_my_gift_time.setText(modelSendGift.getDate());
			}
		}
	}
}
