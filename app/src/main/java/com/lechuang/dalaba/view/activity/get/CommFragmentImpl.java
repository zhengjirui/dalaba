package com.lechuang.dalaba.view.activity.get;

import android.support.v7.util.DiffUtil;

import com.ajguan.library.EasyRefreshLayout;
import com.ajguan.library.LoadModel;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.model.bean.GetBean;
import com.lechuang.dalaba.presenter.ToastManager;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.netApi.GetApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.LeLoadMoreView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: guoning
 * Date: 2017/10/2
 * Description:
 */

public class CommFragmentImpl extends CommFragment  {


//    private List<GetBean.ListInfo> productList = new ArrayList<>();

    private  Subscription subscribe;

    public CommFragmentImpl(){
        super();
    }


    private void getInfoList() {
//@Field("name") String name,@Field("page") int page,@Field("classTypeId") int classTypeId
        Map<String,Object> map = new HashMap<>();
        map.put("page",page);
        if (rootId!=-1){
            map.put("classTypeId",rootId);
        }

        subscribe = Netword.getInstance().getApi(GetApi.class)
                .listInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<GetBean>(getActivity()) {
                    @Override
                    public void successed(GetBean result) {
                        if (result == null) return;
                        final List<GetBean.ListInfo> temp = result.productList;
                        if (page > 1 && temp.isEmpty()) {            //数据没有了
                            mRefreshLayout.loadNothing();
                            return;
                        }

                        if (!mRefreshLayout.isLoading()) {
                            if (!mFirstLoadData) {
                                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                                    @Override
                                    public int getOldListSize() {
                                        return productList.size();
                                    }

                                    @Override
                                    public int getNewListSize() {
                                        return temp.size();
                                    }

                                    @Override
                                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                                        return productList.get(oldItemPosition).id.equals(temp.get(newItemPosition).id);
                                    }

                                    @Override
                                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                                        return productList.get(oldItemPosition).id.equals(temp.get(newItemPosition).id);
                                    }
                                });
                                diffResult.dispatchUpdatesTo(infoAdapter);
                            } else {
                                infoAdapter.addData(0, temp);
                                mFirstLoadData = false;
                            }
                            mRefreshLayout.refreshComplete();
                        } else {
                            infoAdapter.addData((page - 1) * PAGE_COUNT, temp);
                            mRefreshLayout.loadMoreComplete();
                        }
                    }

                    @Override
                    protected void error300(int errorCode, String s) {
                        super.error300(errorCode, s);
                        ToastManager.getInstance().showShortToast(s);
                        doError();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        doError();
                    }
                });
    }

    private void doError() {
        if (!mRefreshLayout.isLoading()) {
            mRefreshLayout.refreshComplete();
        } else {
            mRefreshLayout.loadMoreComplete();
        }
//        if (infoAdapter!=null&&mTopBannerView!=null)
//            infoAdapter.removeHeaderView(mTopBannerView);
        showEmptyView();
    }


    @Override
    protected void onFragmentInvisible() {
        mFirstLoadData = true;

        productList.clear();
       if (subscribe!=null&& !subscribe.isUnsubscribed())
            subscribe.unsubscribe();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_comment;
    }

    @Override
    protected void onFragmentVisible() {
        rootId = getArguments().getInt(LeCommon.KEY_ROOT_ID);
        if (Utils.isNetworkAvailable(getActivity())) {
            hideEmptyView();
//
            initEvent();

//

        }else {
            hideWaitDialog();
            showEmptyView();
        }

    }
    private void initEvent() {
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setLoadMoreModel(LoadModel.COMMON_MODEL);
        mRefreshLayout.addEasyEvent(easyEvent);
        mRefreshLayout.setLoadMoreView(new LeLoadMoreView(getActivity()));

    }


    EasyRefreshLayout.EasyEvent easyEvent = new EasyRefreshLayout.EasyEvent(){

        @Override
        public void onLoadMore() {
            if (!Utils.isNetworkAvailable(getActivity())){
                mRefreshLayout.loadMoreFail();
                return;
            }
            page++;
            getInfoList();
        }

        @Override
        public void onRefreshing() {
            page =1;
            getInfoList();
        }
    };

    @Override
    public void tryAgain() {
        page = 1;
        productList.clear();
        onFragmentVisible();
    }




}

