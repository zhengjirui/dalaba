package com.lechuang.dalaba.view.activity.own;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ali.auth.third.login.callback.LogoutCallback;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Extra;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.presenter.ToastManager;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.ui.ChangePwdActivity;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zhf 2017/08/14
 *         【设置】
 */
public class SetActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_set_head)
    XCRoundImageView ivSetHead;
    @BindView(R.id.tv_set_name)
    TextView tvSetName;
    @BindView(R.id.tv_set_no)
    TextView tvSetNo;
    @BindView(R.id.line_user)
    LinearLayout lineUser;
    @BindView(R.id.line_modify_pwd)
    LinearLayout lineModifyPwd;
    @BindView(R.id.line_feedback)
    LinearLayout lineFeedback;
    @BindView(R.id.line_update)
    LinearLayout lineUpdate;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    private Context mContext = SetActivity.this;
    private LocalSession mSession;
    //保存用户登录信息的sp
    private SharedPreferences se;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        mSession = LocalSession.get(mContext);
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/25  15:44
     * @describe 初始化用户信息
     */
    @Override
    protected void onStart() {
        super.onStart();
        //昵称 没有昵称时展示手机号
        tvSetName.setText(se.getString("nickName", se.getString("phone", "----")));
        //手机号
        tvSetNo.setText("账号:" + se.getString("phone", "----"));
        //头像
        if (!se.getString("photo", "").equals("")) {
            Glide.with(MyApplication.getInstance()).load(se.getString("photo", "")).into(ivSetHead);
        }
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/25  15:44
     * @describe 标题栏设置
     */
    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("设置");
    }


    @OnClick({R.id.iv_back, R.id.line_user, R.id.line_modify_pwd, R.id.line_feedback, R.id.line_update, R.id.tv_exit,R.id.line_helpCenter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.line_user://用户详情
                startActivity(new Intent(this, UserCenterActivity.class));
                break;
            case R.id.line_modify_pwd://密码修改
                //type 判断是找回密码还是修改密码    1  找回    2 修改
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
            case R.id.line_feedback: //意见补充
                startActivity(new Intent(this, FeedBackActivity.class));
                break;
            case R.id.line_update:  //检查版本更新
                startActivity(new Intent(this, VersionUpdateActivity.class));
                break;
            case R.id.tv_exit:
                loginOut();
                break;
            case R.id.line_helpCenter:
                startActivity(new Intent(this, HelpCenterActivity.class));
                break;
        }
    }

    private void loginOut() {
        Netword.getInstance().getApi(CommenApi.class)
                .logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
//                        se.edit().clear().apply();
//                        //登出  清空数据
//                        mSession.loginOut();
                        exitTaobao(result);
                        finish();
                    }
                });

    }

    //退出淘宝
    private void exitTaobao(final String result) {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        if (alibcLogin.isLogin()) {
            alibcLogin.logout(this, new LogoutCallback() {
                @Override
                public void onSuccess() {
                    mSession.loginOut();
                    se.edit().clear().apply();
                    Utils.show(mContext, result);
                }

                @Override
                public void onFailure(int i, String s) {
                    ToastManager.getInstance().showShortToast(s);
                }
            });
        } else {
            mSession.loginOut();
            se.edit().clear().apply();
            // 切换到 MainActivity-->HomeFragment
            setResult(Extra.CODE_MAIN_BACK);
        }
    }





/*@Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.line_user://个人中心
                startActivity(UserCenterActivity.class);
                break;
            case R.id.line_modify_pwd://修改密码
                startActivity(ModifyPwdActivity.class);
                break;
            case R.id.line_feedback://意见反馈
                startActivity(FeedBackActivity.class);
                break;
            case R.id.line_update://版本更新
                startActivity(VersionUpdateActivity.class);
                break;
            case R.id.tv_exit:
               *//* AlibcLogin alibcLogin = AlibcLogin.getInstance();

                alibcLogin.logout(new AlibcLoginCallback() {
                    @Override
                    public void onSuccess(int i) {
                        LocalSession session = LocalSession.get();
                        session.setLogin(false);
                        session.setName(null);
                        session.setImge(null);
                        show("登出成功 ");
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        show("登录失败 ");
                    }
                });*//*
                if (Utils.isNetworkAvailable(mContext)) {
                    new LoginDaoImpl().logout(mContext, new ApiRequest() {
                        @Override
                        public void onSuccess(int status, Object... object) throws JSONException {
                            showLoadingDialog("");
                            if (status == Constants.STATE) {
                                SharedPreferences.Editor spf = getSharedPreferences(Constants.USERINFO, MODE_PRIVATE).edit();
                                spf.clear();
                                spf.commit();
                                //登出  清空数据
                                mSession.loginOut();
                                show("登出成功");
                                finish();
                            } else {
                                show("登出失败,请稍后重试");
                            }
                            dismissLoadingDialog();
                        }

                        @Override
                        public void onFailure() {
                            dismissLoadingDialog();
                            show("登出失败,请稍后重试");
                        }
                    });
                } else {
                    show(getString(R.string.net_error1));
                }
                break;
        }
    }*/

   /* //获取服务器版本号 用来判断当前版本是否最新
    private void getData() {
        new OwnDaoImpl().updataVersion(mContext, new ApiRequest() {
            @Override
            public void onSuccess(int status, Object... object) throws JSONException {
                if (status == Constants.STATE) {
                    JSONObject obj1 = (JSONObject) object[1]; //最新版本的数据
                    if (obj1.has("versionNumber")) {
                        if (!Utils.getAppVersionName(mContext).equals(obj1.getString("versionNumber"))) {
                            findViewById(R.id.iv_version).setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Utils.show(mContext, getString(R.string.net_error));
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }*/
}
