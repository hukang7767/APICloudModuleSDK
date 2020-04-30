package com.alpha.modulegnoga.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alpha.modulegnoga.R;


public class DeviceVersionDialog extends Dialog {

    public DeviceVersionDialog(Context context) {
        super(context, false, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_device_version);
        TextView confirmBtn = (TextView) findViewById(R.id.dialog_alert_ok);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
