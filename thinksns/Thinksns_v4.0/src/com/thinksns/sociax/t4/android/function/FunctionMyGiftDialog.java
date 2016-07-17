package com.thinksns.sociax.t4.android.function;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.fragment.FragmentMyGift;
import com.thinksns.sociax.t4.android.gift.ActivityGiftExchange;
import com.thinksns.sociax.t4.model.ModelMyGifts;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-18
 */
public class FunctionMyGiftDialog {
    private ImageView iv_my_gift;
    private TextView tv_my_gift_say, tv_my_gift_username, tv_my_gift_time, tv_my_gift_title;
    LayoutInflater inflater;
    View view;
    Dialog dialog = null;
    private Button btn_ok;

    private ImageView iv_delete;
    private ModelMyGifts gift;
    private Context mContext;
    private Thinksns application;

    public FunctionMyGiftDialog(Context context) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dialog_my_gift, null);

        application = (Thinksns) context.getApplicationContext();

        btn_ok = (Button) view.findViewById(R.id.btn_ok);

        iv_my_gift = (ImageView) view.findViewById(R.id.iv_my_gift);
        tv_my_gift_say = (TextView) view.findViewById(R.id.tv_my_gift_say);
        tv_my_gift_username = (TextView) view.findViewById(R.id.tv_my_gift_username);
        tv_my_gift_time = (TextView) view.findViewById(R.id.tv_my_gift_time);
        tv_my_gift_title = (TextView) view.findViewById(R.id.tv_my_gift_title);
        iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        iv_delete.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.dismiss();
                return false;
            }
        });
        dialog = new Dialog(context, R.style.info_dialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
    }

    /**
     * 设置礼物基本信息
     *
     * @param gift
     */
    public void setGift(final ModelMyGifts gift, String from,String type) {
        this.gift = gift;
//		ImageLoader.getInstance().displayImage(gift.getImage(), iv_my_gift, Thinksns.getOptions());

        Log.v("giftTest","/type/"+type);

        application.displayImage(gift.getImage(), iv_my_gift);
        if (type!=null&&!type.equals("null")&&!type.equals("")){
            if (type.equals(FragmentMyGift.TYPE_GET)){
                btn_ok.setVisibility(View.VISIBLE);

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActivityGiftExchange.class);
                        intent.putExtra("modelMyGift", gift);
                        intent.putExtra("FLAG", "transfer");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });
                tv_my_gift_username.setText("来自："+gift.getOutUserName());
            }else if(type.equals(FragmentMyGift.TYPE_SEND)){
                btn_ok.setVisibility(View.GONE);
                tv_my_gift_username.setText("赠予："+gift.getInUserName());
            }
        }
        tv_my_gift_say.setText(gift.getSay());
        tv_my_gift_time.setText(gift.getDate());
        tv_my_gift_title.setText(gift.getName());
    }

    public ModelMyGifts getGift() {
        return gift;
    }

    public Dialog getDialog() {
        return dialog;
    }
}