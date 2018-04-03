package com.lechuang.dalaba.view.defineView;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lechuang.dalaba.R;

import butterknife.ButterKnife;

/**
 * Author: guoning
 * Date: 2017/10/6
 * Description: 数据加载失败显示的view
 */

public class EmptyView extends FrameLayout implements View.OnClickListener {

    private ImageView mTry;
    private OnTryListener listener;

    public EmptyView(@NonNull Context context) {
        super(context);

    }

    /**
     * 该View在布局文件中填充时调用该构造函数
     * @param context
     * @param attrs
     */
    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View content = LayoutInflater.from(context).inflate(R.layout.activity_error,this,false);
        addView(content,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        mTry = ButterKnife.findById(this,R.id.iv_tryAgain);
        mTry.setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_tryAgain:
                if (listener!=null){
                    listener.tryAgain();
                }
                break;
            default:break;
        }
    }


    public static interface OnTryListener{
        void tryAgain();
    }

    public void setOnTryListener(OnTryListener listener) {
        this.listener = listener;
    }
}
