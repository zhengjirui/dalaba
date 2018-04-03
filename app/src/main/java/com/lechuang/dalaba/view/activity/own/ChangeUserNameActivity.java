package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.UpdataInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.ClearEditText;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zhf 2017/08/14
 *         【设置用户名】
 */
public class ChangeUserNameActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_ok)
    TextView tv_ok;
    @BindView(R.id.et_change_name)
    ClearEditText et_change_name;
    private Context mContext = ChangeUserNameActivity.this;
    private TextView tv_complete;//确定
    private LocalSession mSession;
    //保存用户登录信息的sp
    private SharedPreferences.Editor se;
    private SharedPreferences sepe;

    public FlippingLoadingDialog mLoadingDialog;  //加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);
        ButterKnife.bind(this);
        mSession = LocalSession.get(mContext);
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this).edit();
        sepe = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
    }


    public void initView() {
        mLoadingDialog = new FlippingLoadingDialog(this, null);
        ((TextView) findViewById(R.id.tv_title)).setText("修改昵称");
        et_change_name.setText(sepe.getString("nickName", ""));
    }

    @OnClick({R.id.iv_back, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_ok:
                if (Utils.isEmpty(et_change_name)) {
                    Utils.show(this, "用户名不能为空!");
                    return;
                }
                if (et_change_name.getText().toString().trim().length() < 4 || et_change_name.getText().length() > 20) {
                    Utils.show(this, "用户名长度4～20位");
                    return;
                }
                if (et_change_name.getText().toString().contains(" ")) {
                    Utils.show(this, "用户名不能包含空格");
                    return;
                }
                if (Utils.containsEmoji(et_change_name.getText().toString())) {
                    Utils.show(this, "用户名不能包含表情等特殊字符");
                    return;
                }
                if (Utils.isNetworkAvailable(mContext)) {
                    String name = et_change_name.getText().toString().trim();
                    mSession.setName(name);
                    se.putString("nickName", name);
                    updateInfo(name);
                } else {
                    Utils.show(this, getString(R.string.net_error1));
                }
                break;
        }
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/25  17:03
     * @describe 修改信息
     */
    private void updateInfo(final String params) {
        showLoadingDialog("");
        Map<String, String> map = new HashMap<>();
        map.put("nickName", params);
        Netword.getInstance().getApi(CommenApi.class)
                .updataInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<UpdataInfoBean>(mContext) {
                    @Override
                    public void successed(UpdataInfoBean result) {
                        se.putString("nickName", params).commit();
                        mSession.setName(params);
                        Utils.show(mContext, "修改成功!");
                        finish();
                    }
                });
        dismissLoadingDialog();

    }

    // 显示加载框
    public void showLoadingDialog(String text) {
        if (text != null) {
            mLoadingDialog.setText(text);
        }

        mLoadingDialog.show();
    }

    // 关闭加载框
    public void dismissLoadingDialog() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
