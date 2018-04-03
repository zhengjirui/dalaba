package com.lechuang.dalaba.base;


import android.support.v4.app.Fragment;
import android.webkit.JavascriptInterface;

import com.lechuang.dalaba.presenter.ToastManager;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;



/**
 * 作者：尹松涛
 * 邮箱：yinsongtaoshmily@outlook.com
 * 日期：2017/9/26
 * 时间：14:12
 */

public abstract class BaseFragment extends Fragment {
    private boolean _isVisible = true;
    private FlippingLoadingDialog waitDialog;
   // protected Subscription subscription;
    private BaseActivity baseActivity;

    public void setBaseActivity(BaseActivity activity) {
        baseActivity = activity;
    }


    public FlippingLoadingDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (waitDialog == null) {
                waitDialog = new FlippingLoadingDialog(getContext(),message);
            }
            if (waitDialog != null) {
                waitDialog.setText(message);
                waitDialog.show();
            }
            return waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisible && waitDialog != null) {
            try {
                if (waitDialog.isShowing())
                    waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 显示一个时间较长的toast信息
     *
     * @param id
     */
    public void showLongToast(int id) {
        ToastManager.getInstance().showLongToast(id);
    }

    /**
     * 显示一个时间较长的toast信息
     *
     * @param msg
     */
    @JavascriptInterface
    public void showLongToast(String msg) {
        ToastManager.getInstance().showLongToast(msg);
    }

    /**
     * 显示一个时间较短的toast信息
     *
     * @param id
     */
    public void showShortToast(int id) {
        ToastManager.getInstance().showShortToast(id);
    }


    /**
     * 显示一个时间较短的toast信息
     *
     * @param msg
     */
    @JavascriptInterface
    public void showShortToast(String msg) {
        ToastManager.getInstance().showShortToast(msg);
    }

    @Override
    public void onDestroy() {
       /* if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }*/
        super.onDestroy();
    }

}


