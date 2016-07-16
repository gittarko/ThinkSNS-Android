package com.thinksns.tschat.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.thinksns.tschat.R;
import com.thinksns.tschat.unit.TDevice;

import java.util.List;

/**
 * Created by hedong on 16/2/18.
 */
public class PopupWindowListDialog extends Dialog {

    public PopupWindowListDialog(Context context) {
        super(context);
    }

    public PopupWindowListDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private PopUpWindowAlertDialog dialog;
        private AdapterView.OnItemClickListener itemClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public PopUpWindowAlertDialog create(List<String> datas) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //自定义透明背景的弹出窗
            dialog = new PopUpWindowAlertDialog(context,
                    R.style.my_dialog);
            View layout = inflater.inflate(R.layout.popup_list_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setCanceledOnTouchOutside(true);
            //点击弹出床主体内容之外消失Dialog
            FrameLayout main = (FrameLayout)layout.findViewById(R.id.ll_popup);
            main.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    dialog.dismiss();
                    return false;
                }
            });

            ListView listView = (ListView)layout.findViewById(R.id.listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    itemClickListener.onItemClick(parent, view, position, id);
                }
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item_dialog_textview,
                    datas);
            listView.setAdapter(adapter);

            dialog.setContentView(layout);
            show();

            return dialog;
        }

        /**
         * 显示dialog窗口
         */
        public void show() {
            dialog.show();
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int)TDevice.getScreenWidth(context);
            params.height = (int)TDevice.getScreenHeight(context);
            window.setAttributes(params);
        }

        /**
         * 隐藏窗口
         */
        public void dimss() {
            dialog.dismiss();
        }
    }
}
