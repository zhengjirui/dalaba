package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.UpdataInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.PhotoUtil;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.ClearEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.dalaba.utils.PhotoUtil.t;

/**
 * 作者：li on 2017/10/8 17:11
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class ChangePhoneNumberActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private Context mContext = ChangePhoneNumberActivity.this;
    private TextView tv_code;
    private ClearEditText et_change_phone, et_change_code;//手机号,验证码
    //保存用户登录信息的sp
    private SharedPreferences.Editor se;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        se = PreferenceManager.getDefaultSharedPreferences(this).edit();

        findViewById(R.id.iv_back).setOnClickListener(this);
        tv_code = (TextView) findViewById(R.id.tv_code);
        //手机号
        et_change_phone = (ClearEditText) findViewById(R.id.et_change_phone);
        ((TextView)findViewById(R.id.tv_title)).setText("更换手机号");
        //验证码
        et_change_code = (ClearEditText) findViewById(R.id.et_change_code);
        tv_code.setOnClickListener(this);
        findViewById(R.id.btn_change_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_code:
                if (!Utils.isTelNumber(et_change_phone.getText().toString())) {
                    Utils.show(mContext, "请输入正确的手机号");
                    return;
                }
                sendCode(et_change_phone.getText().toString().trim());
                break;
            case R.id.btn_change_ok:
                if (!Utils.isTelNumber(et_change_phone.getText().toString())) {
                    Utils.show(mContext, "请输入正确的手机号");
                    return;
                }
                if (Utils.isEmpty(et_change_code)) {
                    Utils.show(mContext, "请输入验证码");
                    return;
                }
                if (Utils.isNetworkAvailable(mContext)) {

                    updataInfoPhone(et_change_phone.getText().toString().trim(), et_change_code.getText().toString().trim());
                } else {
                    Utils.show(mContext, getString(R.string.net_error1));
                }
                break;
        }
    }

    /**
     * 发送验证码
     */
    private void sendCode(String phone) {
        Netword.getInstance().getApi(CommenApi.class)
                .bindPhone(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
                        Utils.show(mContext,result);
                        PhotoUtil.getCode(tv_code);
                        PhotoUtil.handler.post(t);
                        tv_code.setEnabled(false);
                    }
                });

    }

    /**
     * 修改用户电话
     */
    private void updataInfoPhone(final String phone, String verifiCode) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("verifiCode", verifiCode);
        Netword.getInstance().getApi(CommenApi.class)
                .updataInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<UpdataInfoBean>(mContext) {
                    @Override
                    public void successed(UpdataInfoBean result) {
                        se.putString("phone", phone).commit();
                        Utils.show(mContext,"修改成功");
                        finish();
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
    }
}
