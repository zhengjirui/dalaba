package com.lechuang.dalaba.presenter;

/**
 * 作者：li on 2017/9/30 09:32
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public abstract class ResultBackApi {
    //访问结束
    public abstract void onCompleted();
    //错误
    public abstract void onError(Throwable e);
    //正常
    public abstract void onNext(String s);
}
