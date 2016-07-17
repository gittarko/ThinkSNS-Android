package com.thinksns.sociax.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.ApiContact;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.gimgutil.AsyncImageLoader;
import com.thinksns.sociax.modle.Contact;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

public class ContactAdapter extends SociaxListAdapter {

	private int departId = 0;

	private SendHanler sendHandler;
	private Worker thread;
	private Thinksns app;
	private static final int ADDFOLLOW = 1;
	private static final int DELFOLLOW = 2;

	public ContactAdapter(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
		app = (Thinksns) context.getApplicationContext();
		thread = new Worker(app, "Publish data");
		sendHandler = new SendHanler(thread.getLooper(), context);
	}

	public ContactAdapter(ThinksnsAbscractActivity context,
			ListData<SociaxItem> list, int departId) {
		super(context, list);
		this.departId = departId;

		app = (Thinksns) context.getApplicationContext();
		thread = new Worker(app, "Publish data");
		sendHandler = new SendHanler(thread.getLooper(), context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ContactItem contactItem;
		if (convertView == null) {
			contactItem = new ContactItem();
			convertView = inflater.inflate(R.layout.contact_item, null);
			// contactItem.alpha = (TextView)
			// convertView.findViewById(R.id.alpha);
			contactItem.uname = (TextView) convertView
					.findViewById(R.id.contact_name);
			contactItem.userHead = (ImageView) convertView
					.findViewById(R.id.contact_head);
			contactItem.department = (TextView) convertView
					.findViewById(R.id.contact_number);
			contactItem.favoritBtn = (ImageView) convertView
					.findViewById(R.id.fav_my_contact);

			contactItem.contactLayout = (LinearLayout) convertView
					.findViewById(R.id.contact_layout);
			contactItem.categoryText = (TextView) convertView
					.findViewById(R.id.category_text);
			contactItem.categoryImg = (ImageView) convertView
					.findViewById(R.id.category_img);
			convertView.setTag(contactItem);
		} else {
			contactItem = (ContactItem) convertView.getTag();
		}

		final Contact contact = (Contact) getItem(position);

		if (contact.getType() != null && contact.getType().equals("department")) {

			contactItem.categoryText.setText(contact.getUname());

			contactItem.categoryText.setVisibility(View.VISIBLE);
			contactItem.contactLayout.setVisibility(View.GONE);
			contactItem.favoritBtn.setVisibility(View.GONE);
			contactItem.categoryImg.setVisibility(View.VISIBLE);
			contactItem.userHead.setImageResource(R.drawable.contact_head);
			return convertView;
		} else {
			contactItem.categoryText.setVisibility(View.GONE);
			contactItem.contactLayout.setVisibility(View.VISIBLE);
			contactItem.categoryImg.setVisibility(View.GONE);
			contactItem.userHead.setImageResource(R.drawable.contact_head);
			contactItem.favoritBtn.setVisibility(View.VISIBLE);
		}
		// contactItem.userHead.setImageResource(R.drawable.contact_head);
		contactItem.uname.setText(contact.getUname());
		loadImage4(contact.getuHeadUrl(), contactItem.userHead);
		contactItem.department.setText(contact.getDepartment());

		if (contact.getIsFavorite() == 1) {
			// contactItem.favoritBtn.setText(R.string.del_contact_list);
			contactItem.favoritBtn
					.setBackgroundResource(R.drawable.del_to_contact);
		} else {
			// contactItem.favoritBtn.setText(R.string.add_contact_list);
			contactItem.favoritBtn
					.setBackgroundResource(R.drawable.add_to_contact);
		}

		contactItem.favoritBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contact.getIsFavorite() == 1) {
					Message msg = sendHandler.obtainMessage();
					// contactItem.favoritBtn.setText(R.string.del_contact_list);
					contactItem.favoritBtn
							.setBackgroundResource(R.drawable.del_to_contact);
					msg.what = DELFOLLOW;

					Object[] o = new Object[] { contact, v, "" };
					msg.obj = o;
					sendHandler.sendMessage(msg);
				} else {
					Message msg = sendHandler.obtainMessage();
					// contactItem.favoritBtn.setText(R.string.add_contact_list);
					contactItem.favoritBtn
							.setBackgroundResource(R.drawable.add_to_contact);
					msg.what = ADDFOLLOW;

					Object[] o = new Object[] { contact, v, "" };
					msg.obj = o;
					sendHandler.sendMessage(msg);
				}
			}
		});

		return convertView;
	}

	private AsyncImageLoader asyncImageLoader3 = new AsyncImageLoader();

	private Drawable loadImage4(final String url, final ImageView imageView) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		Drawable cacheImage = asyncImageLoader3.loadDrawable(url,
				new AsyncImageLoader.ImageCallback() {
					// 请参见实现：如果第一次加载url时下面方法会执行
					@Override
					public void imageLoaded(Drawable imageDrawable) {
						imageView.setImageDrawable(imageDrawable);
						// cacheImage = imageDrawable;
					}

					@Override
					public Drawable returnImageLoaded(Drawable imageDrawable) {
						// TODO Auto-generated method stub
						return imageDrawable;
					}
				});
		if (cacheImage != null) {
			imageView.setImageDrawable(cacheImage);
		}
		return cacheImage;
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		// TODO Auto-generated method stub
		return getApiStatuses().getContactListFooter(((Contact) obj),
				PAGE_COUNT);
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		if (departId > 0) {
			return getApiStatuses().getDataByDepartment(departId, 1);
		}
		return getApiStatuses().getAllContactList();
	}

	@Override
	public ListData<SociaxItem> searchNew(String key) throws ApiException {
		return getApiStatuses().searchColleague(key);
	};

	UIHandler uiHandler = new UIHandler();

	class SendHanler extends Handler {

		private Context context;

		public SendHanler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Message uiMsg = uiHandler.obtainMessage();
			Object[] o = (Object[]) msg.obj;
			Contact contact = (Contact) o[0];
			boolean result = false;
			int what = 0;
			switch (msg.what) {
			case ADDFOLLOW:

				try {
					result = getApiStatuses().contacterCreate(contact);
					what = 1;
					uiMsg.arg1 = ADDFOLLOW;
				} catch (ApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case DELFOLLOW:
				try {
					result = getApiStatuses().contacterDestroy(contact);
					what = 1;
					uiMsg.arg1 = DELFOLLOW;
				} catch (ApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			uiMsg.what = what;
			// uiMsg.obj = result;
			o[2] = result;
			uiMsg.obj = o;
			uiHandler.sendMessage(uiMsg);
		}
	}

	/**
	 * 更新UI
	 */

	class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			String info = "操作失败";

			Object[] o = (Object[]) msg.obj;
			Contact c = (Contact) o[0];
			ImageView b = (ImageView) o[1];

			switch (msg.what) {
			case 0:
				info = "操作异常";
				break;
			case 1:
				if ((Boolean) o[2]) {
					if (msg.arg1 == ADDFOLLOW) {
						info = "收藏成功";
						// b.setText(R.string.del_contact_list);
						b.setBackgroundResource(R.drawable.del_to_contact);
						c.setIsFavorite(1);
					} else if (msg.arg1 == DELFOLLOW) {
						info = "取消成功";
						// b.setText(R.string.add_contact_list);
						b.setBackgroundResource(R.drawable.add_to_contact);
						c.setIsFavorite(0);
					}
				} else {
					info = "操作失败";
				}
				break;
			}
			Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
		}
	}

	private ApiContact getApiStatuses() {
		Thinksns app = thread.getApp();
		return app.getContact();
	}

	private static class ContactItem {
		// ImageView uname;
		TextView uname;
		TextView department;
		ImageView contactArrow;
		ImageView userHead;
		ImageView favoritBtn;

		LinearLayout contactLayout;
		ImageView categoryImg;
		TextView categoryText;

	}

}
