package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.OwnNewsBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc 消息中心
 */
public class NewsCenterActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_tz)
    ImageView ivTz;
    @BindView(R.id.ll_tz)
    LinearLayout llTz;
    private Context mContext = NewsCenterActivity.this;
    //客服 官方  通知
    private LinearLayout ll_kf, ll_gf, ll_tz;
    private TextView tv_kf, tv_gf, tv_tz;
    private ImageView iv_kf, iv_gf, iv_tz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_center);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("消息中心");
        ll_kf = (LinearLayout) findViewById(R.id.ll_kf);
       /* ll_gf = (LinearLayout) findViewById(R.id.ll_gf);*/
        ll_tz = (LinearLayout) findViewById(R.id.ll_tz);
        iv_kf = (ImageView) findViewById(R.id.iv_kf);
       /* iv_gf = (ImageView) findViewById(R.id.iv_gf);*/
        iv_tz = (ImageView) findViewById(R.id.iv_tz);
    }

    //用户密码
    private String openImPassword;
    //用户账户
    private String phone;
    //客服账号
    private String customerServiceId;
    public YWIMKit mIMKit;

    //网络获取数据
    private void getData() {
        Netword.getInstance().getApi(OwnApi.class)
                .isUnread()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<OwnNewsBean>(mContext) {
                    @Override
                    public void successed(OwnNewsBean result) {
                        if (result.status == 1) {//有未读消息显示小红点
                            iv_tz.setVisibility(View.VISIBLE);
                        } else {
                            iv_tz.setVisibility(View.GONE);
                        }
                        phone = result.appUsers.phone;
                        openImPassword = result.appUsers.openImPassword;
                        customerServiceId = result.appUsers.customerServiceId;
                       /* if (phone != null && openImPassword != null && customerServiceId != null) {
                            //此实现不一定要放在Application onCreate中
                            //此对象获取到后，保存为全局对象，供APP使用
                            //此对象跟用户相关，如果切换了用户，需要重新获取
                            mIMKit = YWAPI.getIMKitInstance(phone, Constants.APP_KEY);
                            //开始登录
                            IYWLoginService loginService = mIMKit.getLoginService();
                            YWLoginParam loginParam = YWLoginParam.createLoginParam(phone, openImPassword);
                            loginService.login(loginParam, new IWxCallback() {

                                @Override
                                public void onSuccess(Object... arg0) {

                                }

                                @Override
                                public void onProgress(int arg0) {
                                    // TODO Auto-generated method stub
                                }

                                @Override
                                public void onError(int errCode, String description) {
                                    //如果登录失败，errCode为错误码,description是错误的具体描述信息
//                                    Utils.show(mContext, description);
                                }
                            });
                        }else{
                            Utils.show(mContext, getResources().getString(R.string.net_error));
                        }*/
                    }
                });
    }

    @OnClick({R.id.iv_back, R.id.ll_tz, R.id.ll_kf, R.id.ll_jifen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:  //返回
                finish();
                break;
            case R.id.ll_tz:  //
                //开启详情界面
                startActivity(new Intent(mContext, NewsActivity.class));
                break;
            case R.id.ll_kf:
                startActivity(new Intent(mContext, HelpCenterActivity.class));
                break;
            case R.id.ll_jifen:  //
                Intent intent = new Intent(this, ProfitActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
