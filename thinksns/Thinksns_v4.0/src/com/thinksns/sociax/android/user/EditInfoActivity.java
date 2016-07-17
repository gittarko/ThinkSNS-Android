package com.thinksns.sociax.android.user;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.unit.ImageUtil;
import com.thinksns.sociax.android.R;

public class EditInfoActivity extends ThinksnsAbscractActivity {

	private static final String TAG = "EditInfoActivity";
	private ImageView headArrow;
	private TextView userName;
	private TextView userPhone;
	private TextView userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		headArrow = (ImageView) findViewById(R.id.img_head_arrow);
		userName = (TextView) findViewById(R.id.user_name);

		userName.setText(getIntentData().getString("uname"));
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);

		userPhone = (TextView) findViewById(R.id.text_edit_phone);
		userInfo = (TextView) findViewById(R.id.text_edit_info);

		headArrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(EditInfoActivity.this)
						.setTitle("选择头像")
						.setItems(R.array.camera, new headImageChangeListener())
						.show();
			}
		});

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton rb = (RadioButton) EditInfoActivity.this
						.findViewById(checkedId);
				Log.d(TAG, "id ...." + checkedId + "text" + rb.getText());
			}
		});

		userPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		userInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	class headImageChangeListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				cameraImage();
				break;
			case 1:
				locationImage();
				break;
			default:
				dialog.dismiss();
			}
		}

		private void locationImage() {
			Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
			getImage.addCategory(Intent.CATEGORY_OPENABLE);
			getImage.setType("image/*");
			startActivityForResult(Intent.createChooser(getImage, "选择照片"), 1);

		}

		// 获取相机拍摄图片
		private void cameraImage() {
			if (!ImageUtil.isHasSdcard()) {
				// Toast.makeText(this.ThinksnsCreate,"" ,T );//.show();
				Toast.makeText(EditInfoActivity.this, "使用相机前先插入SD卡",
						Toast.LENGTH_LONG).show();
				return;
			}
			// 启动相机
			Intent myIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			String picName = System.currentTimeMillis() + ".jpg";
			try {
				String path = ImageUtil.saveFilePaht(picName);
				File file = new File(path);
				Uri uri = Uri.fromFile(file);
				// image.setImagePath(path);
				myIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "file saving...");
			}
			startActivityForResult(myIntent, 0);
		}
	}

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return R.drawable.menu_send_img;
	}

	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		};
	}

	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return getString(R.string.edit_info);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return new RightIsButton(this, "保存");
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.editinfo;
	}

}
