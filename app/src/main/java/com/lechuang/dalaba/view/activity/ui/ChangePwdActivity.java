package com.lechuang.dalaba.view.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.PhotoUtil;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.ClearEditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.dalaba.utils.PhotoUtil.t;

/**
 * 作者：li on 2017/10/6 08:58
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class ChangePwdActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phoneNumber)
    ClearEditText etPhoneNumber;
    //    @BindView(R.id.tv_search)
//    ImageView tvSearch;
    @BindView(R.id.et_good)
    ClearEditText etGood;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.et_mima)
    ClearEditText etMima;
    @BindView(R.id.btn_complete)
    TextView btnComplete;
    //    @BindView(R.id.iv_yanjing)
//    ImageView ivYanjing;
    //type 判断是找回密码还是修改密码    1  找回    2 修改
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_find_back);
        ButterKnife.bind(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_change_pwd;
    }

    @Override
    protected void initTitle() {
        type = 2;
        //type 判断是找回密码还是修改密码    1  找回    2 修改
        tvTitle.setText("修改密码");

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_code, R.id.btn_complete, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_code:
                if (!Utils.isTelNumber(etPhoneNumber.getText().toString())) {
                    Utils.show(this, "请输入正确的手机号");
                    return;
                }
                if (Utils.isNetworkAvailable(this)) {
                    String s = etPhoneNumber.getText().toString();

                    schangePwdCode(s);

                    tvCode.setEnabled(false);
                } else {
                    Utils.show(this, "亲！您的网络开小差了哦");
                }
                break;
            case R.id.btn_complete:
                if (!Utils.isTelNumber(etPhoneNumber.getText().toString())) {
                    Utils.show(this, "请输入正确的手机号");
                    return;
                }
                if (Utils.isEmpty(etGood)) {
                    Utils.show(this, "验证码不能为空!");
                    return;
                }
                if (Utils.isEmpty(etMima)) {
                    Utils.show(this, "密码不能为空!");
                    return;
                }
                HashMap map = new HashMap();
                map.put("phone", etPhoneNumber.getText().toString());
                map.put("password", Utils.getMD5(etMima.getText().toString()));
                map.put("verifiCode", etGood.getText().toString());

                //修改密码
                updatePwd(map);

                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 找回密码
     *
     * @param map
     */
    private void findPwd(HashMap map) {
        Netword.getInstance().getApi(CommenApi.class)
                .findPwd(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(ChangePwdActivity.this, result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", etPhoneNumber.getText().toString());
                        se.commit();
                        startActivity(new Intent(ChangePwdActivity.this, LoginActivity.class));
                        finish();
                    }

                });
    }

    /**
     * 修改密码
     *
     * @param map
     */
    private void updatePwd(HashMap map) {
        Netword.getInstance().getApi(CommenApi.class)
                .changePassword(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(ChangePwdActivity.this, result);
                        SharedPreferences.Editor se = getSharedPreferences("login", MODE_PRIVATE).edit();
                        se.putString("login", etPhoneNumber.getText().toString());
                        se.commit();
                        finish();
                    }

                });
    }

    /**
     * 找回密码获取验证码
     */
    private void findPwdsendCode(String phoneNumber) {
        Netword.getInstance().getApi(CommenApi.class)
                .findCode(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(ChangePwdActivity.this, result);
                        PhotoUtil.getCode(tvCode);
                        PhotoUtil.handler.post(t);

                    }
                });
    }

    /**
     * 修改密码获取验证码
     */
    private void schangePwdCode(String phoneNumber) {
        Netword.getInstance().getApi(CommenApi.class)
                .updatePwdCode(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(ChangePwdActivity.this, result);
                        PhotoUtil.getCode(tvCode);
                        PhotoUtil.handler.post(t);

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoUtil.closeCode();
    }
}
