package com.lechuang.dalaba.view.defineView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.lechuang.dalaba.model.LeCommon;

/**
 * Author: 谷长友
 * Date: 2017/12/4
 * Description: SwipeLayout 刷新控件冲突解决
 */

public class CustomSwipeLayout extends SwipeRefreshLayout {
    float downY, movY;
    public CustomSwipeLayout(Context context) {
        super(context);
    }

    public CustomSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                movY = ev.getRawY() - downY;
                if (movY >100 && LeCommon.isTop) {
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
