package com.thinksns.tschat.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.R;

/**
 * Created by hedong on 16/2/17.
 * 弹出窗
 */
public class PopUpWindowAlertDialog extends Dialog {

    public PopUpWindowAlertDialog(Context context) {
        super(context);
    }

    PopUpWindowAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private int titleSize, messageSize, positiveButtonTextSize, negativeButtonTextSize;

        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message, int messageSize) {
            this.message = message;
            if(messageSize > 0) {
                this.messageSize = messageSize;
            }
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * 设置标题文字和文字的大小
         * @param title
         * @param tv_size 单位是dp
         * @return
         */
        public Builder setTitle(String title, int tv_size) {
            this.title = title;
            if(tv_size > 0)
                titleSize = tv_size;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public PopUpWindowAlertDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //自定义透明背景的弹出窗
            final PopUpWindowAlertDialog dialog = new PopUpWindowAlertDialog(context,
                    R.style.my_dialog);
            View layout = inflater.inflate(R.layout.popup_alert_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // 设置弹出窗标题
            if(title != null) {
                TextView tvTitle = (TextView) layout.findViewById(R.id.tv_title);
                tvTitle.setText(title);
                if(titleSize > 0)
                    tvTitle.setTextSize(titleSize);
                tvTitle.setVisibility(View.VISIBLE);
            }else {
                layout.findViewById(R.id.tv_title).setVisibility(View.GONE);
            }
            // 设置确定按钮文字
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // 设置取消按钮文字显示
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }

            if (message != null) {
                TextView tvMessage = (TextView) layout.findViewById(R.id.tv_message);
                tvMessage.setText(message);
                if(messageSize > 0)
                    tvMessage.setTextSize(messageSize);
            } else if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                ViewGroup.LayoutParams.FILL_PARENT));
            }else {
                ((LinearLayout) layout.findViewById(R.id.content)).setVisibility(View.GONE);
            }

            dialog.setContentView(layout);
            show(dialog);

            return dialog;
        }

        private void show(PopUpWindowAlertDialog dialog) {
            dialog.show();
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int)(UnitSociax.getWindowWidth(context)*0.9);
            params.height = (int)(UnitSociax.getWindowHeight(context)*0.7);
            window.setAttributes(params);
        }
    }
    }
