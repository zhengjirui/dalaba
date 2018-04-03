package com.lechuang.dalaba.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;


import com.lechuang.dalaba.presenter.ToastManager;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;

import butterknife.ButterKnife;
import rx.Subscription;


/**
 * 作者：尹松涛
 * 邮箱：yinsongtaoshmily@outlook.com
 * 日期：2017/9/26
 * 时间：11:11
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean _isVisible;
    protected Subscription subscription;
    protected boolean fullScreen = false;
    private FlippingLoadingDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initField();
        if (fullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        _isVisible = true;
        setContentView(getContentView());
        ButterKnife.bind(this);
        //对标题栏进行封装
        initTitle();
        initView(savedInstanceState);
        initData();
    }

    /**
     * 初始化成员变量
     */
    protected void initField() {
    }


    /**
     * 设置View
     *
     * @return View ID
     */
    protected abstract int getContentView();

    /**
     * 初始化标题
     */
    protected abstract void initTitle();

    /**
     * 初始化布局
     */

    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

    }

    // 只要xml布局中的控件添加了onClick属性为finish，即可调用此方法
    public void finish(View view) {
        finish();
    }

    /**
     * 显示一个时间较长的toast信息
     *
     * @param msg
     */

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
    public void showShortToast(String msg) {
        ToastManager.getInstance().showShortToast(msg);
    }

    public FlippingLoadingDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (waitDialog == null) {
                waitDialog = new FlippingLoadingDialog(this, message);
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

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    protected void setFullScreenEnable(boolean enable) {
        if (enable) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }
}
