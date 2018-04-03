package com.lechuang.dalaba.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.presenter.ToastManager;
import com.lechuang.dalaba.view.defineView.EmptyView;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;

/**
 * Author: guoning
 * Date: 2017/10/6
 * Description: 懒加载的fragment
 */

public abstract class LazyBaseFragment extends Fragment implements EmptyView.OnTryListener {

    //是否完成布局填充
    protected boolean mViewCreated = false;

    private FlippingLoadingDialog waitDialog;

    private EmptyView emptyView;
    private FrameLayout content;

    public LazyBaseFragment(){

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mViewCreated&&isVisibleToUser){
//            showWaitDialog("");
            onFragmentVisible();
        }
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_base,container,false);
        emptyView = (EmptyView) root.findViewById(R.id.emptyView);
        content = (FrameLayout) root.findViewById(R.id.base_content);
        hideEmptyView();
        inflater.inflate(getLayout(),content,true);
        initView(content);
        mViewCreated = true;
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getUserVisibleHint()){
            setUserVisibleHint(true);
        }
    }

    protected void showEmptyView(){
        emptyView.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        emptyView.setOnTryListener(this);
    }

    protected void hideEmptyView(){
        emptyView.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewCreated = false;
        if (waitDialog!=null){
            waitDialog.dismiss();
            waitDialog = null;
        }
        if (!getUserVisibleHint()){
            onFragmentInvisible();
        }
    }


    public FlippingLoadingDialog showWaitDialog(String message) {
        if (getUserVisibleHint()) {
            if (waitDialog == null) {
                waitDialog = new FlippingLoadingDialog(getContext(),message);
            }
                waitDialog.setText(message);
                waitDialog.show();
            return waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (getUserVisibleHint() && waitDialog != null) {
            try {
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                    waitDialog = null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }finally {
                if(waitDialog!=null){
                    waitDialog.dismiss();
                    waitDialog = null;
                }
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



    /**
     * Fragment不可见时
     */
    protected abstract void onFragmentInvisible();


    /**
     * 布局文件
     * @return 布局文件
     */
    protected abstract int getLayout();

    /**
     * 初始化控件
     */
    protected abstract void initView(View root);

    /**
     * Fragment可见时加载数据
     */
    protected abstract   void onFragmentVisible();


}
