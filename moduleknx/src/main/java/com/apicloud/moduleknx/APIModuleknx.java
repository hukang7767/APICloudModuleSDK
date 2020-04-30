package com.apicloud.moduleknx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.blb.ecg.axd.lib.collect.bean.EcgUserInfo;
import com.blb.ecg.axd.lib.collect.userInterface.EcgCollectActivity;
import com.blb.ecg.axd.lib.playback.userInterface.EcgPlaybackActivity;
import com.blb.ecg.axd.lib.settings.ECGGlobalSettings;
import com.blb.ecg.axd.lib.upload.bean.UploadEcgResponse;
import com.blb.ecg.axd.lib.upload.userInterface.EcgUploadActivity;
import com.google.gson.Gson;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class APIModuleknx extends UZModule {
    private int WRITE_EXTERNAL_CODE = 123, BT_ADMIN_CODE = 124, UPLOAD_ECG_CODE = 125, ECG_COLLECT_INTENT_FLAG = 126, ECG_BT_UPGRADE_CODE = 127,
            BINDING_ACTIVITY_RESULT = 128, GENERATE_LOCAL_REPORT = 129, HOLTER_PLAYBACK_CODE = 130, GET_ON_LINE_REPORT = 131;
    private EcgUserInfo mEcgUserInfo;
    //mac binding properties
    private ArrayList<String> mEcgBindMacList;
    private final static int PERMISSION_CODE = 189;
    private ArrayList<UploadEcgResponse> mUploadEcgResponses = new ArrayList<>();
    private UZModuleContext mJsCallback;
    private final static String mTagServerResponse = "server response:";

    public APIModuleknx(UZWebView webView) {
        super(webView);
    }

    //采集上传心电数据
    public void jsmethod_collection(final UZModuleContext moduleContext) {
        mReceiveActionFlag = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ECGGlobalSettings.ECG_UPLOAD_RESULT_ACTION);
        context().registerReceiver(mEcgUploadResultReceiver, intentFilter);
        mJsCallback = moduleContext;
        Intent intent = new Intent(context(), EcgCollectActivity.class);
        EcgUserInfo ecgUserInfo = new Gson().fromJson(moduleContext.get().toString(), EcgUserInfo.class);
        JSONObject result = new JSONObject();
        JSONObject err = new JSONObject();
        if (TextUtils.isEmpty(ecgUserInfo.getUserId())){
            try {
                err.put("code", 1);
                result.put("msg","用户ID为空");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            moduleContext.error(result,err,true);
            return;
        }
        if (TextUtils.isEmpty(ecgUserInfo.getName())){
            try {
                err.put("code", 1);
                result.put("msg","用户name为空");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            moduleContext.error(result,err,true);
            return;
        }
        if (TextUtils.isEmpty(ecgUserInfo.getSex())){
            try {
                err.put("code", 1);
                result.put("msg","用户性别为空");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            moduleContext.error(result,err,true);
            return;
        }
        if (TextUtils.isEmpty(ecgUserInfo.getBirthday())){
            try {
                err.put("code", 1);
                result.put("msg","用户出生日期为空");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            moduleContext.error(result,err,true);
            return;
        }
        if (TextUtils.isEmpty(ecgUserInfo.getPhoneNumber())){
            try {
                err.put("code", 1);
                result.put("msg","用户电话为空");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            moduleContext.error(result,err,true);
            return;
        }
        if (TextUtils.isEmpty(ecgUserInfo.getUserId())){
            try {
                err.put("code", 1);
                result.put("msg","用户ID为空");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            moduleContext.error(result,err,true);
            return;
        }
        intent.putExtra("ecg_user_info", ecgUserInfo);
        intent.putStringArrayListExtra("device_mac_list", mEcgBindMacList);
        startActivityForResult(intent,1001);
    }

    //回放心电数据
    public void jsmethod_playback(final UZModuleContext moduleContext) {
        Intent intent = new Intent(context(), EcgPlaybackActivity.class);
        String mCurrentEcgId = moduleContext.optString("ecg_id");
        Log.i("hukang", "jsmethod_playback: "+mCurrentEcgId);
        intent.putExtra("ecg_id", mCurrentEcgId);
        startActivity(intent);
    }
    private boolean mReceiveActionFlag = false;
    //this receiver to receiver ecg upload notice
    private BroadcastReceiver mEcgUploadResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mReceiveActionFlag){
                String action = intent.getAction();
                if (ECGGlobalSettings.ECG_UPLOAD_RESULT_ACTION.equalsIgnoreCase(action)){
                    mUploadEcgResponses = intent.getParcelableArrayListExtra("ecg_upload_finish_notice");
                    if (null != mUploadEcgResponses && mUploadEcgResponses.size() != 0){
                        mReceiveActionFlag = false;
                        String ecgId = "";
                        for (int i = 0; i < mUploadEcgResponses.size(); i++) {
                            ecgId = ecgId + mUploadEcgResponses.get(i).getEcgId() + ",";
                        }
                        ecgId = ecgId.substring(0, ecgId.length() - 1);
                        Log.i("hukang", "onReceive: "+ecgId);
                        mJsCallback.success(ecgId,true,true);
                    }
                }
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
    //get person info
    public EcgUserInfo getPersonInfo(){
        if (null == mEcgUserInfo){
            mEcgUserInfo = new EcgUserInfo();
            try{
                mEcgUserInfo.setUserId("bailuobo" + (new Random().nextInt(10) + 1) * (new Random().nextInt(9) + 1));
                mEcgUserInfo.setName("白萝卜"+ new Random().nextInt(100));
                mEcgUserInfo.setSex("1");
                mEcgUserInfo.setBirthday("1990-1-12");
                mEcgUserInfo.setIdCard("");
                mEcgUserInfo.setPhoneNumber("130011996" + (new Random().nextInt(89) + 10));
                mEcgUserInfo.setPacemaker_ind(-1);
                mEcgUserInfo.setPhysSign("no problem");
            }catch (Exception e){
                Log.i("blb", "assign user info error");
            }

            String info = "";
            info = "userId:" + mEcgUserInfo.getUserId() +", name:" + mEcgUserInfo.getName()+ ", sex:" + mEcgUserInfo.getSex()+ ", birthday:" + mEcgUserInfo.getBirthday() +  ", idCard:" + mEcgUserInfo.getIdCard()+", phoneNumber:" + mEcgUserInfo.getPhoneNumber()+", pacemaker_ind:" + mEcgUserInfo.getPacemaker_ind()+ ", physSign:" + mEcgUserInfo.getPhysSign();
            Log.i("hukang", "getPersonInfo: "+info);
        }
        return mEcgUserInfo;
    }
}
