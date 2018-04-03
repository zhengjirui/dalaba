package com.lechuang.dalaba.presenter.net.netApi;

import com.lechuang.dalaba.model.bean.AdvertisementBean;
import com.lechuang.dalaba.model.bean.DataBean;
import com.lechuang.dalaba.model.bean.GetHostUrlBean;
import com.lechuang.dalaba.model.bean.GetInfoBean;
import com.lechuang.dalaba.model.bean.HelpInfoNewBean;
import com.lechuang.dalaba.model.bean.KefuInfoBean;
import com.lechuang.dalaba.model.bean.LiveProductInfoBean;
import com.lechuang.dalaba.model.bean.LoadingImgBean;
import com.lechuang.dalaba.model.bean.MyIncomeBean;
import com.lechuang.dalaba.model.bean.OwnCheckVersionBean;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.model.bean.SignBean;
import com.lechuang.dalaba.model.bean.TaobaoLoginBean;
import com.lechuang.dalaba.model.bean.TaobaoUrlBean;
import com.lechuang.dalaba.model.bean.UpdataInfoBean;
import com.lechuang.dalaba.presenter.net.QUrl;

import java.util.HashMap;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;


/**
 * 作者：li on 2017/10/5 16:01
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public interface CommenApi {

    //淘宝登录
    @FormUrlEncoded
    @POST(QUrl.threeLogin)
    Observable<ResultBean<TaobaoLoginBean>> threeLogin(@Field("taobaoNumber") String phoneNumber);

    //注册发送验证码
    @FormUrlEncoded
    @POST(QUrl.sendCode)
    Observable<ResultBean<String>> threeBind(@Field("phone") String phoneNumber);

    //注册
    @FormUrlEncoded
    @POST(QUrl.register)
    Observable<ResultBean<String>> register(@FieldMap HashMap<String, String> allParamMap);


    //登录
    @FormUrlEncoded
    @POST(QUrl.login)
    Observable<ResultBean<DataBean>> login(@Field("u") String username, @Field("p") String password);

    //登出
    @GET(QUrl.logout)
    Observable<ResultBean<String>> logout();

    //获取用户收入信息
    @FormUrlEncoded
    @POST(QUrl.myIncome)
    Observable<ResultBean<MyIncomeBean>> myIncome(@Field("page") String page);

    //获取用户签到信息

    @GET(QUrl.sign)
    Observable<ResultBean<SignBean>> sign();
    //用户签到

    @GET(QUrl.signSuccess)
    Observable<ResultBean<String>> signSuccessed();

    //修改信息
    @FormUrlEncoded
    @POST(QUrl.updateInfo)
    Observable<ResultBean<UpdataInfoBean>> updataInfo(@FieldMap Map<String, String> infoMap);

    //修改手机号码发送验证码
    @FormUrlEncoded
    @POST(QUrl.bindingPhone)
    Observable<ResultBean<String>> bindPhone(@Field("phone") String phoneNumber);

    //找回密码发送验证码
    @FormUrlEncoded
    @POST(QUrl.findCode)
    Observable<ResultBean<String>> findCode(@Field("phone") String phoneNumber);

    //找回密码
    @FormUrlEncoded
    @POST(QUrl.findPwd)
    Observable<ResultBean<String>> findPwd(@FieldMap HashMap<String, String> allParamMap);


    //修改密码发送验证码
    @FormUrlEncoded
    @POST(QUrl.updatePwdCode)
    Observable<ResultBean<String>> updatePwdCode(@Field("phone") String phoneNumber);

    //修改密码
    @FormUrlEncoded
    @POST(QUrl.updatePwd)
    Observable<ResultBean<String>> changePassword(@FieldMap HashMap<String, String> allParamMap);

    //确认修改密码
    @FormUrlEncoded
    @POST(QUrl.opinion)
    Observable<ResultBean<String>> opinion(@FieldMap HashMap<String, String> allParamMap);

    //版本更新
    @FormUrlEncoded
    @POST(QUrl.updateVersion)
    Observable<ResultBean<OwnCheckVersionBean>> updataVersion(@Field("type") String phoneNumber);

    //领取积分
    @FormUrlEncoded
    @POST(QUrl.getJf)
    Observable<ResultBean<String>> getJf(@Field("orderNum") String phoneNumber);

    //第三方登录验证码
    @FormUrlEncoded
    @POST(QUrl.threeSendCode)
    Observable<ResultBean<String>> threeSendCode(@Field("phone") String phone, @Field("taobaoNumber") String taobaoNumber, @Field("photo") String photo);

    //第三方登录验证码
    @FormUrlEncoded
    @POST(QUrl.threeBinding)
    Observable<ResultBean<TaobaoLoginBean>> threeBinding(@Field("phone") String phone, @Field("taobaoNumber") String taobaoNumber, @Field("verifiCode") String verifiCode);

    //领取积分
    @GET(QUrl.loadingImg)
    Observable<ResultBean<LoadingImgBean>> loadingImg();

    //帮助信息
    @GET(QUrl.getHelpInfo)
    Observable<ResultBean<KefuInfoBean>> getHelpInfo();

    //帮助信息
    @FormUrlEncoded
    @POST(QUrl.getHelpInfoNew)
    Observable<ResultBean<HelpInfoNewBean>> getHelpInfoNew(@Field("type") int type);

    //获取淘口令和转连接的方法
    @FormUrlEncoded
    @POST(QUrl.tb_privilegeUrl)
    Observable<ResultBean<TaobaoUrlBean>> tb_privilegeUrl(
            @Field("alipayItemId") String phone, @Field("alipayCouponId") String taobaoNumber,
            @Field("img") String img, @Field("name") String name);

    //进入app时的广告图
    @POST(QUrl.advertisementInfo)
    Observable<ResultBean<AdvertisementBean>> advertisementInfo();

    //根据商品id获取商品详细信息
    @FormUrlEncoded
    @POST(QUrl.getProductInfo)
    Observable<ResultBean<LiveProductInfoBean>> getProductInfo(@Field("productId") String productId,
                                                               @Field("type") String type,
                                                               @Field("zbjId") String zbjId);

    //转分享信息
    @POST(QUrl.zhaunInfo)
    Observable<ResultBean<GetInfoBean>> zhaunInfo();

    //自动找回订单
    @FormUrlEncoded
    @POST(QUrl.autoOrder)
    Observable<ResultBean<String>> autoOrder(@Field("orderNum") String orderNum);

    //获取分享商品的域名
    @POST(QUrl.getShareProductUrl)
    Observable<ResultBean<GetHostUrlBean>> getShareProductUrl();
    //校验用户身份获取验证码
    @POST(QUrl.getCheckCode)
    Observable<ResultBean<String>> getCheckCode();

    //校验用户身份获取验证码
    @FormUrlEncoded
    @POST(QUrl.getVerifiCode)
    Observable<ResultBean<String>> getVerifiCode(@Field("verifiCode") String verifiCode);

}
