package com.lechuang.dalaba.presenter.net.netApi;

import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.model.bean.SunDetailBean;
import com.lechuang.dalaba.model.bean.SunDetailListBean;
import com.lechuang.dalaba.model.bean.SunShowBean;
import com.lechuang.dalaba.model.bean.UpFileBean;
import com.lechuang.dalaba.presenter.net.QUrl;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * @author yrj
 * @date 2017/10/4
 * @E-mail 1422947831@qq.com
 * @desc 晒单接口
 */
public interface TheSunApi {
    /**
     * 晒单接口
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.theSun)
    Observable<ResultBean<SunShowBean>> getSun(@Field("page") int page);

    //晒单详情
    @FormUrlEncoded
    @POST(QUrl.sunSquare)
    Observable<ResultBean<SunDetailBean>> sunDetail(@Field("id") String id);

    //晒单的评论列表
    @FormUrlEncoded
    @POST(QUrl.sunCommentList)
    Observable<ResultBean<SunDetailListBean>> sunCommentList(@Field("id") String id, @Field("page") int page);

    //订单校验
    @FormUrlEncoded
    @POST(QUrl.isRightOrder)
    Observable<ResultBean<String>> isRightOrder(@Field("orderNum") String oderNum);

    //上传图片
    @Multipart
    @POST(QUrl.fileUpload)
    Observable<ResultBean<UpFileBean>> fileUpload(@Part() List<MultipartBody.Part> parts);

    //上传内容
    @FormUrlEncoded
    @POST(QUrl.sunComment)
    Observable<ResultBean<String>> sunComment(@Field("content") String content, @Field("img") String imgUrl, @Field("starLevel") int startLevel, @Field("orderNumber") String orderNumber);
}
