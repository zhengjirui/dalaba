package com.lechuang.dalaba.presenter.net;

import android.content.Context;
import android.content.Intent;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.presenter.ToastManager;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;

import java.lang.ref.WeakReference;

import rx.Observer;

/**
 * 作者：li on 2017/9/29 15:55
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public abstract class ResultBack<T> implements Observer<ResultBean<T>> {

    private WeakReference<Context> weakReference;
    public ResultBack(Context mContext) {
        weakReference = new WeakReference<>(mContext);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Context mContext = weakReference.get();
        if (mContext !=null)
            Utils.show(mContext, mContext.getString(R.string.net_error));
    }

    @Override
    public void onNext(ResultBean<T> result) {
//        Log.i("ResultBack_onNext", JSON.toJSONString(result));
        if (result.errorCode == 200) {
            successed(result.data);
        } else if (result.errorCode == 401 || result.errorCode == 303) {  //错误码401 303 登录
            handlerLogin(result.errorCode);
        } else if(result.errorCode==300){
            ToastManager.getInstance().showShortToast(result.moreInfo);
            error300(result.errorCode,result.moreInfo);
        }else{
            ToastManager.getInstance().showShortToast(result.moreInfo);
        }
    }

    /**
     * 处理 用户需要登录及未授权的问题
     *
     * @param errorCode 服务器返回的错误码
     */
    protected void handlerLogin(int errorCode) {
        final Context mContext = weakReference.get();
        if (mContext!=null)
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }
    
    protected  void error300(int errorCode,String s){

    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/29  15:59
     * @describe 处理网络请求成功的数据
     */
    //public abstract void successed(String s);
    public abstract void successed(T result);


}

