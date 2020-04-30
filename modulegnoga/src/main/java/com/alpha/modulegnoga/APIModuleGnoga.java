package com.alpha.modulegnoga;


import android.content.Intent;

import com.alpha.modulegnoga.device.DeviceActivity;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class APIModuleGnoga extends UZModule {

    public static UZModuleContext mModuleContext;
    public APIModuleGnoga(UZWebView webView) {
        super(webView);
    }

    public void jsmethod_toHome(final UZModuleContext moduleContext) {
        mModuleContext = moduleContext;
        startActivity(new Intent(context(),DeviceActivity.class));
    }

    public void jsmethod_getData(final UZModuleContext moduleContext) {



    }
}
