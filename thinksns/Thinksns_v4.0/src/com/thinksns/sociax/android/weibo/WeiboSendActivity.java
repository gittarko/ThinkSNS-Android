package com.thinksns.sociax.android.weibo;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.thinksns.sociax.api.ApiGroup;
import com.thinksns.sociax.api.ApiMessage;
import com.thinksns.sociax.api.ApiStatuses;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.exception.WeiboDataInvalidException;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.utils.WordCount;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.android.R;

public class WeiboSendActivity extends ThinksnsAbscractActivity {
	private static ModelWeibo weibo;
	private static EditText edit;
	private static CheckBox checkBox;
	private static Worker thread;
	private static Handler handler;
	private static LoadingView loadingView;
	private int replyCommentId = -1;
	private int comment_uid = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		edit = (EditText) findViewById(R.id.send_content);
		checkBox = (CheckBox) findViewById(R.id.isComment);
		loadingView = (LoadingView) findViewById(LoadingView.ID);

		this.setTextForCheckBox(getIntentData().getInt("send_type"));

		if (this.getIntentData().containsKey("commentId")) {
			replyCommentId = this.getIntentData().getInt("commentId");
			comment_uid = this.getIntentData().getInt("uid");
			edit.setText("回复@" + getIntentData().getString("username") + "：");
			edit.setSelection(edit.getText().length());
		} else {
			replyCommentId = -1;
		}
		if (getIntentData().getInt("send_type") != AppConstant.REPLY_MESSAGE
				&& getIntentData().getInt("send_type") != AppConstant.CREATE_MESSAGE) {
			try {
				if (getIntentData().get("commenttype") != null) {
					weibo = new ModelWeibo(new JSONObject(getIntentData().getString(
							"data")), 1);
				} else {
					weibo = new ModelWeibo(new JSONObject(getIntentData().getString(
							"data")));
				}
			} catch (WeiboDataInvalidException e) {
				Log.d(AppConstant.APP_TAG,
						"ThinksnsSend ---> wm " + e.toString());
				WeiboSendActivity.this.finish();
			} catch (JSONException e) {
				WeiboSendActivity.this.finish();
			}
		}
		this.setInputLimit(getIntentData().getInt("send_type"));
	}

	@Override
	public void finish() {
		if (edit != null)
			SociaxUIUtils.hideSoftKeyboard(getApplicationContext(), edit);
		super.finish();
	}

	private void setInputLimit(int type) {

		TextView overWordCount = (TextView) findViewById(R.id.overWordCount);

		if (type != AppConstant.REPLY_MESSAGE
				|| type != AppConstant.CREATE_MESSAGE) {
			if (type == AppConstant.TRANSPOND) {
				String tran = "";
				if (weibo.getSourceWeibo() != null) {
					tran = "//@" + weibo.getUsername() + "："
							+ weibo.getContent();
				} else if (weibo.getPosts() != null && weibo.getType() != null
						&& weibo.getType().equals("weiba_repost")) {
					tran = "//@" + weibo.getUsername() + "："
							+ weibo.getContent();
				}
				WordCount wordCount = new WordCount(edit, overWordCount, tran);
				edit.addTextChangedListener(wordCount);
			} else {
				WordCount wordCount = new WordCount(edit, overWordCount);
				overWordCount.setText(wordCount.getMaxCount() + "");
				edit.addTextChangedListener(wordCount);
			}
		} else {
			overWordCount.setVisibility(View.GONE);
		}
	}

	private void setTextForCheckBox(int type) {
		switch (type) {
		case AppConstant.TRANSPOND:
			checkBox.setText(R.string.transpond_checkbox);
			break;
		case AppConstant.COMMENT:
			checkBox.setText(R.string.comment_checkbox);
			break;
		case AppConstant.REPLY_MESSAGE:
			checkBox.setVisibility(View.GONE);
		case AppConstant.CREATE_MESSAGE:
			checkBox.setVisibility(View.GONE);
			break;
		}
	}

	private final class ActivityHandler extends Handler {
		@SuppressWarnings("unused")
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			loadingView.show(edit);

			// 获取数据
			Thinksns app = thread.getApp();
			ApiStatuses statuses = app.getStatuses();
			ApiMessage message = app.getMessages();
			ApiGroup groupStatuses = app.getGroupApi();

			try {
				switch (msg.what) {
				case AppConstant.COMMENT:
					if (edit.getText().toString().trim().length() == 0) {
						loadingView.showInfo("评论不能为空", edit);
						loadingView.hide(edit);
					} else {
						String editContent = edit.getText().toString();

						Comment comment = new Comment();
						comment.setContent(editContent);
						comment.setStatus(weibo);
						comment.setType(checkBox.isChecked() ? Comment.Type.WEIBO
								: Comment.Type.COMMENT);

						if (replyCommentId != -1) {
							Comment recomment = new Comment();
							recomment.setComment_id(replyCommentId);
							recomment.setUid(comment_uid + "");
							comment.setReplyComment(recomment);
						}
						if (getIntentData().containsKey("app")) {
							if (groupStatuses.commentStatuses(comment)) {
								sendFlag = true;
								loadingView.showInfo("评论成功", edit);
								setResult(AppConstant.COMMENT_SUCCESS);
								WeiboSendActivity.this.finish();
							} else {
								loadingView.showInfo("评论失败", edit);
								loadingView.hide(edit);
							}
						} else {
							if (statuses.comment(comment) == 1) {
								sendFlag = true;
								setResult(AppConstant.COMMENT_SUCCESS);
								loadingView.showInfo("评论成功", edit);
								WeiboSendActivity.this.finish();
							} else {
								loadingView.showInfo("评论失败", edit);
								loadingView.hide(edit);
							}
						}
					}
					break;
				// 微博转发
				case AppConstant.TRANSPOND:
					String editContent = edit.getText().toString().trim()
							.length() > 0 ? edit.getText().toString().trim()
							: "转发微博";

					ModelWeibo newWeibo = new ModelWeibo();
					newWeibo.setContent(editContent);
					newWeibo.setSourceWeibo(weibo);
					if (getIntentData().containsKey("app")) {
						if (groupStatuses.repostStatuses(newWeibo,
								checkBox.isChecked())) {
							loadingView.showInfo("分享成功", edit);
							Log.d(AppConstant.APP_TAG,
									"weibo transpond success...");
							WeiboSendActivity.this.finish();
						} else {
							loadingView.showInfo("分享失败", edit);
							loadingView.hide(edit);
							Log.d(AppConstant.APP_TAG,
									"weibo transpond fail ...");
						}
					} else {
						if (statuses.repost(newWeibo, checkBox.isChecked())) {
							loadingView.showInfo("分享成功", edit);
							Log.d(AppConstant.APP_TAG,
									"weibo transpond success...");
							WeiboSendActivity.this.finish();
						} else {
							loadingView.showInfo("分享失败", edit);
							loadingView.hide(edit);
							Log.d(AppConstant.APP_TAG,
									"weibo transpond fail ...");
						}
					}
					break;
				case AppConstant.REPLY_MESSAGE:
					com.thinksns.sociax.modle.Message replyMessage = new com.thinksns.sociax.modle.Message();
					replyMessage.setListId(getIntentData().getInt("messageId"));
					replyMessage.setContent(edit.getText().toString());
					// messageObj.setSourceMessage(replyMessage);

					if (message.reply(replyMessage)) {
						getIntentData().putString(TIPS, "回复消息成功");
						WeiboSendActivity.this.finish();
					} else {
						loadingView.showInfo("回复消息失败", edit);
					}
					break;
				case AppConstant.CREATE_MESSAGE:
					com.thinksns.sociax.modle.Message createMessage = new com.thinksns.sociax.modle.Message();
					createMessage.setTo_uid(getIntentData().getInt("to_uid"));
					String content = edit.getText().toString().trim();
					if (content.length() <= 0) {
						loadingView.error("请输入内容");
						loadingView.hide(edit);
						return;
					} else if (content.length() > AppConstant.weiboLenght) {
						loadingView.error(getString(R.string.word_limit));
						loadingView.hide(edit);
						return;
					}
					createMessage.setContent(content);
					// createMessage.setTitle("new message");
					Log.e("uid",
							"getIntentData" + getIntentData().getInt("to_uid"));
					Log.e("content", "content" + edit.getText().toString());
					if (message.createNew(createMessage)) {
						loadingView.showInfo("发送成功", edit);
						getIntentData().putString(TIPS, "发送成功");
						WeiboSendActivity.this.finish();
					} else {
						loadingView.showInfo("发送失败", edit);
					}
					break;
				}

			} catch (VerifyErrorException e) {
				// clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.showInfo(e.getMessage(), edit);
			} catch (ApiException e) {
				// clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.showInfo(e.getMessage(), edit);
			} catch (UpdateException e) {
				// clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.showInfo(e.getMessage(), edit);
			} catch (DataInvalidException e) {
				// clearSendingButtonAnim(getCustomTitle().getRight());
				loadingView.showInfo(e.getMessage(), edit);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			thread.quit();
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.transpond;
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				// sendingButtonAnim(v);
				if (isHasEmailList) {

				}
				Thinksns app = (Thinksns) WeiboSendActivity.this
						.getApplicationContext();
				thread = new Worker(app, "Publish data");
				handler = new ActivityHandler(thread.getLooper(),
						WeiboSendActivity.this);
				Message msg = handler.obtainMessage(getIntentData().getInt(
						"send_type"));
				handler.sendMessage(msg);
			}
		};
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new RightIsButton(this, this.getString(R.string.sendMessage));
	}

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return R.drawable.button_send;
	}

	@Override
	public String getTitleCenter() {
		switch (getIntentData().getInt("send_type")) {
		case AppConstant.TRANSPOND:
			return this.getString(R.string.transpond);
		case AppConstant.COMMENT:
			return this.getString(R.string.comment);
		case AppConstant.REPLY_MESSAGE:
			return this.getString(R.string.private_letter);
		case AppConstant.CREATE_MESSAGE:
			return this.getString(R.string.private_letter);
		}
		return null;
	}
}
