package com.lechuang.dalaba.view.activity.live;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.DialogAlertView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 方法名：直播碎片
 * 方法描述：
 * 作者：韩雪松 on 2017/8/1 11:30
 * 邮箱：15245605689@163.com
 */

public class GetIntegralFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private EditText et_orderNum; //订单编号
    private View v;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_get_integral, container, false);
        initView();
        return this.v;
    }


    public void initView() {
        v.findViewById(R.id.iv_back).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.tv_title)).setText("领取积分");
        et_orderNum = (EditText) v.findViewById(R.id.et_orderNum);

        v.findViewById(R.id.iv_get).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_get:
                if (Utils.isNetworkAvailable(getActivity())) {
                    //订单编号
                    String orderNum = et_orderNum.getText().toString().trim();
                    if (Utils.isEmpty(et_orderNum)) {
                        Utils.show(getActivity(), "订单编号不能为空");
                        return;
                    }
                    if (orderNum.contains(" ")) {
                        Utils.show(getActivity(), "订单编号不能包含空格");
                        return;
                    }
                    Netword.getInstance().getApi(CommenApi.class)
                            .getJf(orderNum)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ResultBack<String>(getActivity()) {


                                @Override
                                public void onNext(ResultBean<String> result) {
                                    if (result.moreInfo != null) {
                                        showResultDialog(getActivity(), result.moreInfo);
                                    } else {
                                        showResultDialog(getActivity(), result.data);
                                    }
                                }

                                @Override
                                public void successed(String result) {

                                }
                            });
                }
                break;
           /* case R.id.iv_back:
                finish();
                break;*/
            default:
                break;
        }

    }

    //领取积分提示
    public static void showResultDialog(Context context, String text) {
        final DialogAlertView dialog = new DialogAlertView(context, R.style.CustomDialog);
        dialog.setView(R.layout.dialog_getjifen);
        dialog.show();
        ((TextView) dialog.findViewById(R.id.txt_notice)).setText(text);
        dialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
