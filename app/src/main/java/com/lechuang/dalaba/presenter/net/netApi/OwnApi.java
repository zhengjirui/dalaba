package com.lechuang.dalaba.presenter.net.netApi;

import com.lechuang.dalaba.model.bean.HeroBean;
import com.lechuang.dalaba.model.bean.OrderBean;
import com.lechuang.dalaba.model.bean.OwnIncomeBean;
import com.lechuang.dalaba.model.bean.OwnJiFenInfoBean;
import com.lechuang.dalaba.model.bean.OwnMyAgentBean;
import com.lechuang.dalaba.model.bean.OwnNewsBean;
import com.lechuang.dalaba.model.bean.OwnBean;
import com.lechuang.dalaba.model.bean.OwnNewsListBean;
import com.lechuang.dalaba.model.bean.OwnUserInfoBean;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.model.bean.ShareMoneyBean;
import com.lechuang.dalaba.model.bean.TeamBean;
import com.lechuang.dalaba.model.bean.TeamNextBean;
import com.lechuang.dalaba.presenter.net.QUrl;

import java.util.HashMap;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Author: guoning
 * Date: 2017/10/8
 * Description:
 */

public interface OwnApi {


    @POST("user/appUsers/agencyDetail")
    Observable<ResultBean<OwnBean>> agency();

    /**
     * 申请成为代理时发起支付
     *
     * @return
     */
    @POST("agency/agencySendOrder")
    Observable<ResultBean<OwnBean.Pay>> pay();
    /**
     * 申请成功回调
     *
     * @return
     */
    @FormUrlEncoded
    @POST(QUrl.aplySuccess)
    Observable<ResultBean<String>> paySuccess(@FieldMap HashMap<String, String> allParamMap);

    /**
     * 积分提现时积分信息
     * 参数无
     */
    @POST(QUrl.txInfo)
    Observable<ResultBean<OwnJiFenInfoBean>> jifenInfo();

    /**
     * 积分提现
     *
     * @withdrawPrice 提现金额
     */
    @FormUrlEncoded
    @POST(QUrl.tx)
    Observable<ResultBean<String>> jifenTx(@Field("withdrawPrice") Double withdrawPrice);

    /**
     * 判断是否有未读消息
     * 出参 status  是否显示小红点   0：不显示   1：显示
     */
    @POST(QUrl.isUnread)
    Observable<ResultBean<OwnNewsBean>> isUnread();

    /**
     * 消息列表
     *
     * @param page 分页
     */
    @FormUrlEncoded
    @POST(QUrl.allNews)
    Observable<ResultBean<OwnNewsListBean>> allNws(@Field("page") int page);
    /**
     * 用户信息
     *
     */
    @POST(QUrl.userInfo)
    Observable<ResultBean<OwnUserInfoBean>> userInfo();

    /**
     * 我的代理信息
     *
     */
    @FormUrlEncoded
    @POST(QUrl.agent)
    Observable<ResultBean<OwnMyAgentBean>> agentInfo(@Field("page") int page);   /**
     * 我的代理信息
     *
     */
    @POST(QUrl.shareMoneyInfo)
    Observable<ResultBean<ShareMoneyBean>> shareMoneyInfo();
    /**
     * 英雄榜信息
     *
     */
    @FormUrlEncoded
    @POST(QUrl.heroAgent)
    Observable<ResultBean<HeroBean>> heroAgentInfo(@FieldMap HashMap<String, String> allParamMap);
    /**
     *  自动成为代理
     *
     */
    @POST(QUrl.autoAgent)
    Observable<ResultBean<String>> autoAgent();

    /**
     * 获取订单信息 (LGH:2017/11/18)
     */
    @FormUrlEncoded
    @POST(QUrl.orderDetails)
    Observable<ResultBean<OrderBean>> orderDetails(@Field("type") int type, @Field("page") int page);

    /**
     * 我的收益 (LGH:2017/11/18)
     * @param type 1 今日收益 2 昨日统计 3 近7日统计 4 本月统计 5 上月统计
     * @return
     */
    @FormUrlEncoded
    @POST(QUrl.ownIncome)
    Observable<ResultBean<OwnIncomeBean>> ownIncome(@Field("type") int type);

    /**
     * 我的团队 (LGH:2017/11/20)
     * @param page
     * @return
     */
    @FormUrlEncoded
    @POST(QUrl.mineTeam)
    Observable<ResultBean<TeamBean>> mineTeam(@Field("page") int page);

    /**
     * 我的团队 (LGH:2017/11/20)
     * @param page
     * @return
     */
    @FormUrlEncoded
    @POST(QUrl.nextTeam)
    Observable<ResultBean<TeamNextBean>> nextTeam(@Field("userId") String userId, @Field("page") int page);


}
