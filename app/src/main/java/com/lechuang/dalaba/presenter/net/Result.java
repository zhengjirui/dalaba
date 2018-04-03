package com.lechuang.dalaba.presenter.net;


import com.lechuang.dalaba.model.bean.ResultBean;

import rx.Observable;

/**
 * 作者：尹松涛
 * 邮箱：yinsongtaoshmily@outlook.com
 * 日期：2017/9/29
 * 时间：10:13
 */

public class Result<T> extends Observable<ResultBean<T>> {
    protected Result(OnSubscribe<ResultBean<T>> f) {
        super(f);
    }
}
