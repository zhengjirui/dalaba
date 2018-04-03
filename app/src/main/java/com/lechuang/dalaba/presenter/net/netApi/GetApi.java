package com.lechuang.dalaba.presenter.net.netApi;

import com.lechuang.dalaba.model.bean.GetBean;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.presenter.net.QUrl;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Author: guoning
 * Date: 2017/10/2
 * Description:
 */

public interface GetApi {

    /**
     * 顶部tab列表
     * @return
     */
    @POST(QUrl.getTopTabList)
    Observable<ResultBean<GetBean>> topTabList();

    /**
     * 顶部广告图
     * @return
     */
    @POST(QUrl.getTopBanner)
    Observable<ResultBean<GetBean>> topBanner();

    /**
     *
     * @param name 搜索框查询内容
     * @param page 页数
     * @param classTypeId 分类id(精选传空)
     * @return
     */
    @FormUrlEncoded
    @POST(QUrl.getListInfo)
    Observable<ResultBean<GetBean>> listInfo(@FieldMap Map<String,Object> map);


//    @FormUrlEncoded
    @POST("https://api.m.taobao.com/h5/mtop.taobao.detail.getdetail/6.0/")
//    Observable<ResultBean<GetBean>> item(@Field("api") String api,
//                                         @Field("v")String v,
//                                         @Field("ttid")String ttid,
//                                         @Field("type")String type,
//                                         @Field("dataType")String dataType,
//                                         @Field("data")String data
//                                         );
    Observable<ResultBean<GetBean>> item();
//    /**
//     * 首页轮播图接口
//     * 入参 无
//     */
//    @POST(QUrl.homePageBanner)
//    Observable<String> homeBanner();
//
//    /**
//     * 分类接口
//     * 入参 无
//     */
//    @POST(QUrl.home_classify)
//    Observable<String> homeClassify();
//
//
//    /**
//     * 滚动条接口(滚动的文字)
//     * 入参 无
//     */
//    @POST(QUrl.home_scrollTextView)
//    Observable<String> homeScrollTextView();
//
//    /**
//     * 首页四个栏目图片接口
//     * 入参 无
//     */
//    @POST(QUrl.home_programaImg)
//    Observable<String> homeProgramaImg();
//
//    /**
//     * 首页最下栏目接口
//     *zai tiao
//     * duan
//     * @param page 分页加载页数
//     */
//    @FormUrlEncoded
//    @POST(QUrl.home_lastPage)
//    Observable<ResultBean<GetBean>> homeLastPage(@Field("page") int page);

}
