package com.alpha.modulegnoga.common;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alpha.modulegnoga.R;


public class CancelableAlertDialog extends Dialog {

    /**
     * PickerDialog
     * @param context context
     * @param msg msg to show
     * @param okStr ok button to show
     * @param onClickListener onclickListener
     */
    public CancelableAlertDialog(Context context, String titleStr, String msg, String okStr,
                                 String cancelStr, View.OnClickListener onClickListener) {
        super(context, false, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_cancelable_alert);
        if (msg != null) {
            TextView msgTv = (TextView) findViewById(R.id.dialog_alert_cancelable_content);
            msgTv.setText(msg);
        }

        TextView title = (TextView) findViewById(R.id.dialog_alert_cancelable_title);
        TextView ok = (TextView) findViewById(R.id.dialog_alert_cancelable_ok);
        if (titleStr != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(titleStr);
        }
        else {
            title.setVisibility(View.GONE);
        }
        if (okStr != null) {
            ok.setText(okStr);
        }
        ok.setOnClickListener(onClickListener);

        TextView cancel = (TextView) findViewById(R.id.dialog_alert_cancelable_cancel);
        if (cancelStr != null) {
            cancel.setText(cancelStr);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}