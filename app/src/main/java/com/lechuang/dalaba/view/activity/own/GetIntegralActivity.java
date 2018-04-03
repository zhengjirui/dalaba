package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.DialogAlertView;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/11 20:33
 * 邮箱：961567115@qq.com
 * 修改备注: 领取积分
 */
public class GetIntegralActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_orderNum; //订单编号
    private boolean _isVisible;
    protected boolean fullScreen = false;
    private FlippingLoadingDialog waitDialog;
    private Context mContext = GetIntegralActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_integral);
        initView();
    }

    public void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("找回订单");
        et_orderNum = (EditText) findViewById(R.id.et_orderNum);
        findViewById(R.id.auto_findBack).setOnClickListener(this);
        findViewById(R.id.iv_get).setOnClickListener(this);
    }




    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auto_findBack:
                startActivityForResult(new Intent(this,WebViewActivity.class),1);
                showWaitDialog("");
                break;
            case R.id.iv_get:
                if (Utils.isNetworkAvailable(mContext)) {
                    //订单编号
                    String orderNum = et_orderNum.getText().toString().trim();
                    if (Utils.isEmpty(et_orderNum)) {
                        Utils.show(mContext, "订单编号不能为空");
                        return;
                    }
                    if (orderNum.contains(" ")) {
                        Utils.show(mContext, "订单编号不能包含空格");
                        return;
                    }
                    Netword.getInstance().getApi(CommenApi.class)
                            .getJf(orderNum)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new ResultBack<String>(mContext) {
                                @Override
                                public void onNext(ResultBean<String> result) {
                                    if (result.moreInfo != null) {
                                        showResultDialog(mContext, result.moreInfo);
                                    } else {
                                        showResultDialog(mContext, result.data);
                                    }
                                }

                                @Override
                                public void successed(String result) {

                                }
                            });
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==2){
            if(item!=null){
                item.clear();
            }
            item = data.getStringArrayListExtra("items");
            upOrder();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public ArrayList<String> item = new ArrayList<>();


    public void upOrder() {
        int size = item.size();
        String s = "";
        for (int i = 0; i < size; i++) {
            if(i!=size-1){
                s += item.get(i) + ",";
            }else {
                s+=item.get(i);
            }

        }
        if (size == 0) {
            Utils.show(this, "没有找到对应订单，试试手动找回");
        }
        Netword.getInstance().getApi(CommenApi.class)
                .autoOrder(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(GetIntegralActivity.this) {
                    @Override
                    public void successed(String result) {
                        showResultDialog(mContext, result);
                    }
                });
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


    public FlippingLoadingDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (waitDialog == null) {
                waitDialog = new FlippingLoadingDialog(this, message);
            }
            if (waitDialog != null) {
                waitDialog.setText(message);
                waitDialog.show();
            }
            return waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisible && waitDialog != null) {
            try {
                if (waitDialog.isShowing())
                    waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
