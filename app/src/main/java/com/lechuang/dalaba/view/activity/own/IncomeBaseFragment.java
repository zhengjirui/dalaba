package com.lechuang.dalaba.view.activity.own;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
import com.lechuang.dalaba.model.bean.OwnIncomeBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的收益 底部(今日统计 ...)
 */
public class IncomeBaseFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_income_self_value)
    TextView tvSelfValue;
    @BindView(R.id.tv_income_self_rate)
    TextView tvSelfRate;
    @BindView(R.id.tv_income_agency_value)
    TextView tvAgencyValue;
    @BindView(R.id.tv_income_agency_rate)
    TextView tvAgencyRate;

    // 我的收益类型
    private int mIncomeType;

    //是否可见
    protected boolean isVisible;
    // 标志位，标志Fragment已经初始化完成。
    public boolean isPrepared = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_income_base, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        initData();
        return view;
    }

    private void initView() {

    }

    private void initData() {
        // 加载数据判断是否可见，进行懒加载
//        if (!isPrepared || !isVisible) {
//            return;
//        }

        mIncomeType = getArguments().getInt("type", 1);
        Netword.getInstance().getApi(OwnApi.class)
                .ownIncome(mIncomeType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnIncomeBean>(getActivity()) {
                    @Override
                    public void successed(OwnIncomeBean result) {
                        Log.i(mIncomeType + " ResultBack_successed", JSON.toJSONString(result));
                        tvSelfValue.setText("¥" + result.record.ownIncome);
                        tvSelfRate.setText(result.record.ownIncomeNum);
                        tvAgencyValue.setText("¥" + result.record.agencyIncome);
                        tvAgencyRate.setText(result.record.agencyIncomeNum);
                    }
                });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            //初始化完成，切处于可见状态
            onVisible();
        } else {
            isVisible = false;
            onInVisible();
        }
    }

    protected void onInVisible() {

    }

    protected void onVisible() {
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrepared = false;
        unbinder.unbind();
    }

}
