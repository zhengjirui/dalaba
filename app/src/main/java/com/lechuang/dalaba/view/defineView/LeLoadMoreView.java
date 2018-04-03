package com.lechuang.dalaba.view.defineView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ajguan.library.view.SimpleLoadMoreView;

/**
 * Author: guoning
 * Date: 2017/10/7
 * Description:
 */

public class LeLoadMoreView extends SimpleLoadMoreView  {

    public LeLoadMoreView(@NonNull Context context) {
        super(context);
    }

    public LeLoadMoreView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void loading() {
        super.loading();
    }

    @Override
    public void loadComplete() {
        super.loadComplete();
    }

    @Override
    public void loadFail() {
        super.loadFail();

    }

    @Override
    public void loadNothing() {
        super.loadNothing();
    }

    @Override
    public View getCanClickFailView() {
        return super.getCanClickFailView();
    }
}
