package com.lechuang.dalaba.view.activity;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.MyApplication;

/**
 * 作者：li on 2017/9/18 16:22
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public abstract class LoadingPager extends FrameLayout{
  // private Dialog mLoadingView;
    private View mErrorView;
    private View mBlankView;
public LoadingPager(Context context){
    super(context);
    initCommonVeiw();

}
/**
 *  @author li
 *  邮箱：961567115@qq.com
 *  @time 2017/9/18  17:33
 *  @describe 创建成功页面
 */
    public abstract View initSuccessed();

    /**
 *  @author li
 *  邮箱：961567115@qq.com
 *  @time 2017/9/18  16:26
 *  @describe 创建不同状态的布局
 */
    private void initCommonVeiw(){
        mBlankView=View.inflate(MyApplication.getInstance(), R.layout.activity_blank,null);
        mErrorView=View.inflate(MyApplication.getInstance(), R.layout.activity_error,null);
        this.addView(mBlankView);
        this.addView(mErrorView);
        //添加成功视图
        View view = initSuccessed();
        this.addView(view);
    }

}
