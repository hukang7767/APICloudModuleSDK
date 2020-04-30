package com.alpha.modulegnoga.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alpha.modulegnoga.R;


public class CommonAlertDialog extends Dialog {

    public CommonAlertDialog(Context context, int titleId, int contentId,
                             View.OnClickListener onClickListener) {
        super(context, false, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_common_alert);
        TextView confirmBtn = (TextView) findViewById(R.id.dialog_alert_ok);
        TextView alertTitle = (TextView) findViewById(R.id.dialog_alert_title);
        TextView alertContent = (TextView) findViewById(R.id.dialog_alert_content);
        alertTitle.setText(titleId);
        alertContent.setText(contentId);
        confirmBtn.setOnClickListener(onClickListener);
    }

    public CommonAlertDialog(Context context, String title, String content, String okStr,
                             View.OnClickListener onClickListener) {
        super(context, false, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_common_alert);
        TextView confirmBtn = (TextView) findViewById(R.id.dialog_alert_ok);
        TextView alertTitle = (TextView) findViewById(R.id.dialog_alert_title);
        TextView alertContent = (TextView) findViewById(R.id.dialog_alert_content);
        alertTitle.setText(title);
        alertContent.setText(content);
        if (okStr != null) {
            confirmBtn.setText(okStr);
        }
        confirmBtn.setOnClickListener(onClickListener);
    }
}