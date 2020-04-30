package com.alpha.modulegnoga.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;

import com.alpha.modulegnoga.R;


public class LoadingDialog extends Dialog {
    private TextView mContent;

    public LoadingDialog(Context context) {
        super(context, false, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        mContent = (TextView) findViewById(R.id.dialog_loading_content);
    }

    public void setMessage(String content) {
        if (mContent != null && !TextUtils.isEmpty(content)) {
            mContent.setText(content);
        }
    }
}