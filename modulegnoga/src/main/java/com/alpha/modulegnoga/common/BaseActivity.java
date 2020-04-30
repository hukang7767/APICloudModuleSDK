package com.alpha.modulegnoga.common;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alpha.modulegnoga.utils.CommonAlertDialog;
import com.alpha.modulegnoga.utils.LoadingDialog;

/**
 * Safety smart watch client base activity
 *
 * @author 001254
 */
public class BaseActivity extends FinalActivity {

    private static final String TAG = "BaseActivity";

    /**
     * for upload date
     */
    public static final String DATE_INTERVAL = "-";

    /**
     * for display date
     */
    private static final String DATE_SHOW_INTERVAL = "/";

    /**
     * for change alpha transparent
     */
    private static final float ALPHA_TRANSPARENT = 0.5f;

    /**
     * for change alpha non transparent
     */
    private static final float ALPHA_NON_TRANSPARENT = 1f;

    private Toast mToast;

    protected LoadingDialog mLoadingDialog;

    private CommonAlertDialog mAlertDialog;

    private CommonAlertDialog mCommonAlertDialog;

    private View.OnClickListener mAlertOnclickListener;

    private View.OnClickListener mCommonAlertOnclickListener;

    private boolean mNeedFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SysApplication.getInstance().addActivity(this);
        initData();
        initViews();
        initListeners();
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissAlertDialog();
        dismissLoadingDialog();
        SysApplication.getInstance().removeActivity(this);
    }

    /**
     * show alert Dialog
     *
     * @param titleId    dialog title
     * @param contentId  dialog content
     * @param needFinish need finish activity
     */
    protected void showAlertDialog(int titleId, int contentId, boolean needFinish) {
        dismissAlertDialog();
        mAlertDialog = null;
        mNeedFinish = false;
        if (mAlertOnclickListener == null) {
            mAlertOnclickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAlertDialog();
                    if (mNeedFinish) {
                        finish();
                    }
                }
            };
        }
        mAlertDialog = new CommonAlertDialog(this, titleId, contentId, mAlertOnclickListener);
        mAlertDialog.show();
        mNeedFinish = needFinish;
    }

    /**
     * show alert Dialog
     *
     * @param title
     * @param content
     * @param msg
     * @param needFinish
     */
    protected void showAlertDialog(String title, String content, String msg, boolean needFinish) {
        dismissCommonAlertDialog();
        mNeedFinish = false;
        if (mCommonAlertDialog != null) {
            mCommonAlertDialog.dismiss();
            mCommonAlertDialog = null;
        }
        if (mCommonAlertOnclickListener == null) {
            mCommonAlertOnclickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissCommonAlertDialog();
                    if (mNeedFinish) {
                        finish();
                    }
                }
            };
        }
        mCommonAlertDialog = new CommonAlertDialog(this, title, content, msg,
                mCommonAlertOnclickListener);
        mCommonAlertDialog.show();
        mNeedFinish = needFinish;
    }

    /**
     * dismissDialog
     */
    protected void dismissAlertDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        dismissLoadingDialog();
    }

    /**
     * dismissDialog
     */
    protected void dismissCommonAlertDialog() {
        if (mCommonAlertDialog != null && mCommonAlertDialog.isShowing()) {
            mCommonAlertDialog.dismiss();
            mCommonAlertDialog = null;
        }
        dismissLoadingDialog();
    }

    /**
     * show progress
     *
     * @param msg        message
     * @param cancelable boolean
     */
    protected void showLoadingDialog(String msg, boolean cancelable) {
        dismissLoadingDialog();
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.setMessage(msg);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.show();
    }

    /**
     * dismiss progress
     */
    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * The override method will be called onCreate
     */
    protected void initData() {

    }

    /**
     * The override method will be called onCreate
     */
    protected void initViews() {

    }

    /**
     * The override method will be called onCreate
     */
    protected void initListeners() {

    }

    /**
     * Method to hide Input keyboard
     */
    protected void hideInput() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * format date
     *
     * @param year  year
     * @param month month
     * @param day   day
     * @return formatted date
     */
    protected String getDateString(int year, int month, int day) {
        StringBuilder date = new StringBuilder();
        date.append(month);
        date.append(DATE_SHOW_INTERVAL);
        date.append(day);
        date.append(DATE_SHOW_INTERVAL);
        date.append(year);
        return date.toString();
    }

    /**
     * setAlpha 0.5f
     */
    protected void setAlpha() {
        setBackgroundAlpha(ALPHA_TRANSPARENT);
    }

    /**
     * setAlpha 1f
     */
    protected void restoreAlpha() {
        setBackgroundAlpha(ALPHA_NON_TRANSPARENT);
    }

    /**
     * set background alpha
     *
     * @param bgAlpha alpha 0.0-1.0
     */
    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * Method to push a notification.
     *
     * @param iconId       Icon
     * @param contentTitle Title
     * @param contentText  TExt
     * @param activity     Activity
     * @param from         From
     */
    public void pushNotification(int iconId, String contentTitle, String contentText,
                                 Class<?> activity, String from) {
    }

    /**
     * Method to make a toast
     *
     * @param context Context
     * @param text    Toast text
     */
    protected void makeToast(Context context, String text) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * Method to make a toast on UI thread
     *
     * @param context Context
     * @param text    Toast text
     */
    protected void makeToastOnUiThread(final Context context, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeToast(context, text);
            }
        });
    }
}
