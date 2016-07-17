package com.thinksns.sociax.android;

import com.thinksns.sociax.android.R;

import uk.co.senab.photoview.PhotoView;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class ChatPicture extends Activity {
	private static final String TAG = "ChatPicture";
	private PhotoView photoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_picture);
		photoView = (PhotoView) findViewById(R.id.chat_pic);
		Log.d(TAG, "getIntent().getStringExtra(chat_pic)="
				+ getIntent().getStringExtra("chat_pic"));
		// photoView.setImageURI(Uri.parse(getIntent().getStringExtra("chat_pic")));
		Matrix matrix = new Matrix();
		matrix.postRotate(getIntent().getIntExtra("degree", 0));
		Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra(
				"chat_pic"));
		photoView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_picture, menu);
		return true;
	}

}
