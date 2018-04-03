package com.lechuang.dalaba.view.activity.own;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.model.DemoTradeCallback;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.ToastManager;
import com.lechuang.dalaba.view.activity.SigneActivity;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;
import com.lechuang.dalaba.view.defineView.MGridView;
import com.lechuang.dalaba.view.defineView.ViewHolder;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class OwnFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.iv_set)
    ImageView ivSet;
    @BindView(R.id.iv_headImg)
    XCRoundImageView iv_headImg;
    @BindView(R.id.tv_login_or_register)
    TextView tvLoginOrRegister;
//    @BindView(R.id.tv_sign)
//    TextView tv_sign;
    @BindView(R.id.gv_state)
    MGridView gvState;
    @BindView(R.id.ll_myincom)
    TextView llMyincom;
    @BindView(R.id.ll_task)
    TextView llTask;
    @BindView(R.id.ll_zhuan_money_gonglv)
    TextView llzhuanmoneygonglv;
    @BindView(R.id.ll_share)
    TextView llShare;
    Unbinder unbinder;
    @BindView(R.id.ll_jifen)
    TextView llJifen;
    @BindView(R.id.ll_myagent)
    TextView llMyagent;
    private String str[] = {"待付款", "待发货", "待收货", "售后"};
    private int images[] = {R.drawable.wode_daifukuan, R.drawable.wode_daifahuo, R.drawable.wode_daishouhuo,
            R.drawable.wode_shouhou};

    private LocalSession mSession;
    //打开页面的方法
    private AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
    private Map exParams = new HashMap<>();

    //保存用户登录信息的sp
    private SharedPreferences se;

    private int signedStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_own, container, false);
        unbinder = ButterKnife.bind(this, v);
        initView();
        return v;
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/22  20:01
     * @describe 初始化下边的淘宝订单信息
     */
    private void initView() {
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSession = LocalSession.get(getActivity());
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", images[i]);
            map.put("name", str[i]);
            list.add(map);
        }
        gvState.setAdapter(new CommonAdapter<Map<String, Object>>(getActivity(), list, R.layout.home_kinds_item) {
            @Override
            public void setData(ViewHolder viewHolder, Object item) {
                viewHolder.getView(R.id.iv_kinds_img).setVisibility(View.GONE);
                viewHolder.getView(R.id.iv_own).setVisibility(View.VISIBLE);
                HashMap<String, Object> map = (HashMap<String, Object>) item;
                String name = (String) map.get("name");
                int img = (int) map.get("image");
                viewHolder.setText(R.id.tv_kinds_name, name);
                viewHolder.setImageResource(R.id.iv_own, img);
            }
        });
        gvState.setOnItemClickListener(this);
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
    }

    @Override
    public void onStart() {
        super.onStart();
        if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false))
            getData();
        //是否登录
        mSession.setLogin(se.getBoolean("isLogin", false));
        //id
        mSession.setId(se.getString("id", ""));
        if (se.getBoolean("isLogin", false)) {
            if (!se.getString("photo", "").equals("")) {
                Glide.with(MyApplication.getInstance()).load(se.getString("photo", "")).into(iv_headImg);
            }
            //没有昵称时展示手机号
            String nick = se.getString("nickName", se.getString("phone", "----"));
            tvLoginOrRegister.setText(nick);
            tvLoginOrRegister.setEnabled(false);
            if(se.getInt("isAgencyStatus",0)==0){
                llMyagent.setText("成为合伙人");
            }else {
                llMyagent.setText("我的团队");
            }
        } else {
            tvLoginOrRegister.setText("登录/注册");
            iv_headImg.setImageResource(R.drawable.pic_morentouxiang);
            tvLoginOrRegister.setEnabled(true);
            llMyagent.setText("成为代理");
        }

    }

    //切换刷新vip和签到状态
    /*@Override
    public void onPause() {
        super.onPause();
        getData();
    }*/

    private void getData() {
//        Netword.getInstance().getApi(OwnApi.class)
//                .userInfo()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ResultBack<OwnUserInfoBean>(getActivity()) {
//                    @Override
//                    public void successed(OwnUserInfoBean result) {
//                        signedStatus = result.signedStatus;
//                        if (signedStatus == 0) {
//                            tv_sign.setText("签到");
//                        } else {
//                            tv_sign.setText("已签到");
//                        }
//                        se.edit().putInt("isAgencyStatus", result.isAgencyStatus).apply();
//                    }
//                });

    }

    @OnClick({R.id.iv_set, R.id.iv_news, R.id.tv_login_or_register, R.id.ll_order, R.id.tv_sign, R.id.ll_jifen,
            R.id.ll_myincom, R.id.ll_task, R.id.ll_zhuan_money_gonglv, R.id.ll_myagent, R.id.ll_share,  R.id.ll_help,R.id.yingxiongbang})
    public void onViewClicked(View view) {
        if (mSession.isLogin()) {
            switch (view.getId()) {
                case R.id.ll_order:   //查看全部订单
                    orderType = 0;
                    AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(orderType, true);
                    AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, null, exParams, new DemoTradeCallback());
                    break;
                case R.id.iv_set://设置
                    startActivity(new Intent(getActivity(), SetActivity.class));
                    break;
                case R.id.ll_jifen://积分
                    startActivity(new Intent(getActivity(), JinfenReflectActivity.class));
                    break;
                case R.id.iv_news://消息
                    startActivity(new Intent(getActivity(), NewsCenterActivity.class));
                    break;
                case R.id.tv_login_or_register://注册。登录
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                case R.id.tv_sign://签到
//                    startActivityForResult(new Intent(getActivity(), HelpCenterActivity.class)
//                            .putExtra("title","赚钱攻略").putExtra("type",2)
//                            , 1);
                    if (se.getBoolean(LeCommon.KEY_HAS_LOGIN, false))
                        startActivityForResult(new Intent(getActivity(), SigneActivity.class), 1);
                    else {
                        ToastManager.getInstance().showShortToast("您需要先登陆");
                    }
                    break;
                case R.id.yingxiongbang:
                    startActivity(new Intent(getActivity(), HeroActivity.class));
                    break;
                case R.id.ll_myincom://我的收益
                    startActivity(new Intent(getActivity(), MyIncomeActivity.class));
                    break;
                case R.id.ll_task://任务中心
                    startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                    break;
                case R.id.ll_zhuan_money_gonglv://赚钱攻略
                    startActivity(new Intent(getActivity(), ZhuanQianGlvActivity.class)
                            .putExtra("title","赚钱攻略").putExtra("type",1));
//                    AlibcBasePage alibcBase = new AlibcMyCartsPage();
//                    AlibcTaokeParams taokeParams = new AlibcTaokeParams(Constants.PID, "", "");
//                    AlibcTrade.show(getActivity(), alibcBase, alibcShowParams, taokeParams, exParams, new DemoTradeCallback());
                    break;
                case R.id.ll_myagent://我的合伙人
                    if (se.getInt("isAgencyStatus", 0) == 1) {
                        startActivity(new Intent(getActivity(), MyTeamActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(), ApplyAgentActivity.class));
                    }
                    break;
                case R.id.ll_share://我的分享
                    startActivity(new Intent(getActivity(), ShareMoneyActivity.class));
                    break;
                case R.id.ll_help:// 帮助中心
                    startActivity(new Intent(getActivity(), KeFuCenterActivity.class)
                            .putExtra("title","客服中心").putExtra("type",1));
                    break;
            }
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    int orderType = 0;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mSession.isLogin()) {
            switch (images[position]) {
                case R.drawable.wode_daifukuan://代付款
                    orderType = 1;
                    break;
                case R.drawable.wode_daifahuo://待发货
                    orderType = 2;
                    break;
                case R.drawable.wode_daishouhuo://待收货
                    orderType = 3;
                    break;
                case R.drawable.wode_shouhou://售后
                    orderType = 4;
                    break;
            }
            AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(orderType, true);
            AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, null, exParams, new DemoTradeCallback());
        } else {

            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
        unbinder.unbind();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == 1) {
//            if (data.getIntExtra("sign", 0) == 1) {
//                tv_sign.setText("已签到");
//            } else {
//                tv_sign.setText("签到");
//            }
//        }
//    }
}