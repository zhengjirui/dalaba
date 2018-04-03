package com.lechuang.dalaba.presenter.net.netApi;

import com.lechuang.dalaba.model.bean.GetBean;
import com.lechuang.dalaba.model.bean.HomeBannerBean;
import com.lechuang.dalaba.model.bean.HomeGunDongTextBean;
import com.lechuang.dalaba.model.bean.HomeKindBean;
import com.lechuang.dalaba.model.bean.HomeLastProgramBean;
import com.lechuang.dalaba.model.bean.HomeProgramBean;
import com.lechuang.dalaba.model.bean.HomeProgramDetailBean;
import com.lechuang.dalaba.model.bean.HomeScrollTextViewBean;
import com.lechuang.dalaba.model.bean.HomeSearchResultBean;
import com.lechuang.dalaba.model.bean.ResultBean;
import com.lechuang.dalaba.presenter.net.QUrl;

import java.util.HashMap;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author yrj
 * @date 2017/9/29
 * @E-mail 1422947831@qq.com
 * 首页api
 */
public interface HomeApi {
    /**
     * 首页轮播图接口
     * 入参 无
     */
    @POST(QUrl.homePageBanner)
    Observable<ResultBean<HomeBannerBean>> homeBanner();

    /**
     * 分类接口
     * 入参 无
     */
    @POST(QUrl.home_classify)
    Observable<ResultBean<HomeKindBean>> homeClassify();


    /**
     * 滚动条接口(滚动的文字)
     * 入参 无
     */
    @POST(QUrl.home_scrollTextView)
    Observable<ResultBean<HomeScrollTextViewBean>> homeScrollTextView();

    /**
     * 首页四个栏目图片接口
     * 入参 无
     */
    @POST(QUrl.home_programaImg)
    Observable<ResultBean<HomeProgramBean>> homeProgramaImg();

    /**
     * 首页最下栏目接口
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.home_lastPage)
    Observable<ResultBean<HomeLastProgramBean>> homeLastPage(@Field("page") int page);

    /**
     * 首页栏目1详情接口
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.recommend1)
    Observable<ResultBean<HomeProgramDetailBean>> program1(@Field("page") int page);

    /**
     * 首页栏目2详情接口
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.recommend2)
    Observable<ResultBean<HomeProgramDetailBean>> program2(@Field("page") int page);

    /**
     * 首页栏目3详情接口
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.recommend3)
    Observable<ResultBean<HomeProgramDetailBean>> program3(@Field("page") int page);

    /**
     * 首页栏目4详情接口
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.recommend4)
    Observable<ResultBean<HomeProgramDetailBean>> program4(@Field("page") int page);

    /**
     * 首页栏目4详情接口
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.recommend5)
    Observable<ResultBean<HomeProgramDetailBean>> program5(@Field("page") int page);
    /**
     * 过夜单
     *
     * @param page 分页加载页数
     */
    @FormUrlEncoded
    @POST(QUrl.overnightBill)
    Observable<ResultBean<HomeSearchResultBean>> overnightBill(@Field("page") int page);

    /**
     * 搜索结果接口
     *
     * @param allParamMap 所有参数
     *                     因为参数名会变化 全部拼接到allParameter中
     *
     *                     拼接内容 page + 搜索的种类 + 商品排序方式
     *                              product 搜索的种类,分类页面传递的是classTypeId = 分类的id,搜索页面传递的参数是name = 用户输入的搜索内容
     *                     商品排序方式
     *                              isVolume 1代表按销量排序从高到底
     *                              isAppraise 1好评从高到底
     *                              isPrice  1价格从低到高排序
     *                              isPrice  2价格从高到低排序
     *                              isNew    1新品商品冲最近的往后排序
     */
    @FormUrlEncoded
    @POST(QUrl.home_product)
    Observable<ResultBean<HomeSearchResultBean>> searchResult(@FieldMap HashMap<String, String> allParamMap);



    /**
     *
     * 首页最下栏目分类商品集合接口
     * 入参  page  分页    classTypeId  分类id,全部栏目不传        is_official   全部传1,其他不传
     *
     */
    @FormUrlEncoded
    @POST(QUrl.home_lastPage)
    Observable<ResultBean<HomeLastProgramBean>> homeLastProgram(@FieldMap HashMap<String, String> allParamMap);

    /**
     * 首页最下栏目 标题数据
     *
     * @return
     */
    @POST(QUrl.getTopTabList)
    Observable<ResultBean<GetBean>> lastTabList();
    /**
     * 首页最下栏目 标题数据
     *
     * @return
     */
    @POST(QUrl.gunDongText)
    Observable<ResultBean<HomeGunDongTextBean>> gunDongText();



}
