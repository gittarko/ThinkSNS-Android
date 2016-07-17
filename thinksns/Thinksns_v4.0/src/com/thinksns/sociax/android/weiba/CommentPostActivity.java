package com.thinksns.sociax.android.weiba;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.ApiWeiba;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.modle.CommentPost;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author povol
 * @date Nov 30, 2012
 * @version 1.0
 */
public class CommentPostActivity extends ThinksnsAbscractActivity {

	private CommentPost cPost;
	private int postId;
	private String commentContent;
	private EditText etComment;

	private static Worker thread;
	private static Handler handler;

	private static final int COMMENT_POST = 1;
	private static final int REPLY_COMMENT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cPost = (CommentPost) getIntentData().getSerializable("post_comment");
		postId = getIntent().getIntExtra("post_id", -1);
		initTitle();

		TextView tv = (TextView) findViewById(R.id.word_count_limit);
		etComment = (EditText) findViewById(R.id.et_comment_content);
		SociaxUIUtils.setInputLimit(tv, etComment);

		if (cPost != null) {
			etComment.setText("回复@" + cPost.getAuthor().getUserName() + " ：");
			etComment.setSelection(etComment.getText().toString().length());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		SociaxUIUtils.hideSoftKeyboard(this, etComment);
	}

	@Override
	public void finish() {
		super.finish();
		SociaxUIUtils.hideSoftKeyboard(this, etComment);
	}

	@Override
	public int getRightRes() {
		return R.drawable.menu_send_img;
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!checkData()) {
					return;
				}
				getCustomTitle().getRight().setEnabled(false);
				SociaxUIUtils.hideSoftKeyboard(CommentPostActivity.this,
						etComment);
				Thinksns app = (Thinksns) CommentPostActivity.this
						.getApplicationContext();
				thread = new Worker(app, "Publish comment ");
				handler = new ActivityHandler(thread.getLooper(),
						CommentPostActivity.this);

				if (postId < 0) {
					Message msg = handler.obtainMessage();
					msg.what = REPLY_COMMENT;
					msg.obj = commentContent;
					handler.sendMessage(msg);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = COMMENT_POST;
					msg.obj = commentContent;
					handler.sendMessage(msg);
				}

			}
		};
	}

	private boolean checkData() {
		commentContent = etComment.getText().toString().trim();
		if (commentContent.length() == 0) {
			Toast.makeText(CommentPostActivity.this, R.string.comment_c_alert,
					Toast.LENGTH_SHORT).show();
		} else if (commentContent.length() > 140) {
			Toast.makeText(CommentPostActivity.this, R.string.word_limit,
					Toast.LENGTH_SHORT).show();
		}
		return (commentContent.length() > 0 && commentContent.length() < 140) ? true
				: false;
	}

	@Override
	public String getTitleCenter() {
		if (cPost != null) {
			return getString(R.string.weiba_replay_com);
		}
		return getString(R.string.weiba_com_post);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new RightIsButton(this, getString(R.string.comment));
	}

	@Override
	protected int getLayoutId() {
		return R.layout.comment_post;
	}

	// ////************************************//////
	UIHandler uHandler = new UIHandler();

	private final class ActivityHandler extends Handler {
		private final long SLEEP_TIME = 2000;
		private Context context;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ApiWeiba weibaApi = thread.getApp().getWeibaApi();
			Message msg1 = null;
			try {
				switch (msg.what) {
				case COMMENT_POST:
					msg1 = new Message();
					msg1.what = msg.what;
					CommentPost cPost1 = new CommentPost();
					cPost1.setPostId(postId);
					cPost1.setContent(commentContent);
					msg1.obj = weibaApi.commentPost(cPost1);
					break;
				case REPLY_COMMENT:
					msg1 = new Message();
					msg1.what = msg.what;
					CommentPost cPost2 = new CommentPost();
					cPost2.setPostId(cPost.getReplyId());
					cPost2.setContent(commentContent);
					msg1.obj = weibaApi.replyComment(cPost2);
					break;
				}
			} catch (ApiException e) {
				e.printStackTrace();
			}
			uHandler.sendMessage(msg1);
			thread.quit();
		}
	}

	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case COMMENT_POST:
				getCustomTitle().getRight().setEnabled(true);
				if ((Boolean) msg.obj) {
					sendFlag = true;
					Toast.makeText(CommentPostActivity.this,
							R.string.comment_c_success, Toast.LENGTH_SHORT)
							.show();
					CommentPostActivity.this.finish();
				} else {
					Toast.makeText(CommentPostActivity.this,
							R.string.comment_c_fail, Toast.LENGTH_SHORT).show();
				}
				break;
			case REPLY_COMMENT:
				getCustomTitle().getRight().setEnabled(true);
				if ((Boolean) msg.obj) {
					sendFlag = true;
					Toast.makeText(CommentPostActivity.this,
							R.string.comment_c_success, Toast.LENGTH_SHORT)
							.show();
					CommentPostActivity.this.finish();
				} else {
					Toast.makeText(CommentPostActivity.this,
							R.string.comment_c_fail, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	}
}
