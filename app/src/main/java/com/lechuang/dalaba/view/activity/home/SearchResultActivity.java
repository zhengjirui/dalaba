package com.lechuang.dalaba.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.dalaba.mine.adapter.OnItemClick;
import com.lechuang.dalaba.mine.adapter.ViewHolderRecycler;
import com.lechuang.dalaba.mine.view.XRecyclerView;
import com.lechuang.dalaba.model.bean.HomeSearchResultBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.HomeApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.GridItemDecoration;
import com.lechuang.dalaba.view.defineView.WiperSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yrj on 2017/8/17.
 * 搜索结果页面
 */


public class SearchResultActivity extends BaseActivity implements OnItemClick {

    //搜索结果展示的方式  0:按销量展示  1.按好评展示 2.按价格展示  3.按新品展示
    private int showStyle = 0;
    private ImageView iv_price;
    private Context mContext = SearchResultActivity.this;
    //展示在搜索框上的搜索内容
    private String rootName;
    //入参的搜索内容
    private String productName;
    /**
     * 入参的排序方式
     * isVolume 1代表按销量排序从高到底
     * isAppraise 1好评从高到底
     * isPrice  1价格从低到高排序
     * isPrice  2价格从高到低排序
     * isNew    1新品商品冲最近的往后排序
     */
    private String style = "isVolume=1";
    //入参 页数
    private int page = 1;
    //
    //上个界面传递过来的值,用来判断是从分类还是搜索跳过来的 1:分类 2:搜索界面
    private int productstyle = 1;
    //可以刷新的gridview
    private XRecyclerView gv_search;
    private CommonRecyclerAdapter mAdapter;
    //无网络状态
    private LinearLayout ll_notNet;
    // 没有搜索到商品
    private LinearLayout nothingAll;
    private TextView tvRemind;
    //拼接完的参数
    private String allParameter;
    //刷新重试按钮
    private ImageView iv_tryAgain;
    // 没有商品展示默认图片
//    private RelativeLayout nothingData;
    //商品头图片 淘宝或天猫
    private int headImg;
    //参数map
    private HashMap<String, String> allParamMap;
    private WiperSwitch wiperSwitch;
    //是否人工筛选
    private boolean isPeopleChange = true;

    //保存用户登录信息的sp
    private SharedPreferences se;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this);
        //商品集合
        productList = new ArrayList<>();
        allParamMap = new HashMap<>();

        ll_notNet = (LinearLayout) findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) findViewById(R.id.iv_tryAgain);
        iv_tryAgain.setOnClickListener(this);
//        nothingData = (RelativeLayout) findViewById(R.id.common_nothing_data);
//        nothingData.setOnClickListener(this);

        nothingAll = (LinearLayout) findViewById(R.id.search_result_nothing);
        tvRemind = (TextView) findViewById(R.id.tv_remind_nothing);

        iv_price = (ImageView) findViewById(R.id.iv_price);
        gv_search = (XRecyclerView) findViewById(R.id.gv_search);
        wiperSwitch = (WiperSwitch) findViewById(R.id.wiperSwitch);
        wiperSwitch.setChecked(true);
        //设置监听
        wiperSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isPeopleChange = checkState;
//                if(isPeopleChange){
//                    findViewById(R.id.ll_type).setVisibility(View.VISIBLE);
//                }else{
//                    findViewById(R.id.ll_type).setVisibility(View.GONE);
//                }
//                getData();
            }
        });
        wiperSwitch.setOnChangedListener(new WiperSwitch.OnChangedListener() {
            @Override
            public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
                isPeopleChange = checkState;
                if (productList != null) {
                    productList.clear();
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }

                if (isPeopleChange) {
                    findViewById(R.id.ll_type).setVisibility(View.VISIBLE);
                    tvRemind.setText(getResources().getString(R.string.remind_nothing_mine));
                } else {
                    findViewById(R.id.ll_type).setVisibility(View.GONE);
                    tvRemind.setText(getResources().getString(R.string.remind_nothing_all));
                }
                page = 1;
                getData();
            }
        });
    }

    @Override
    protected void initData() {
        productstyle = getIntent().getIntExtra("type", 1);
        if (productstyle == 1) {//1:分类
            productName = "&classTypeId=" + getIntent().getStringExtra("rootId");
        } else {  //2 搜索
            productName = "&name=" + getIntent().getStringExtra("rootId");
        }
        rootName = getIntent().getStringExtra("rootName");
        findViewById(R.id.ll_sale).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.ll_like).setOnClickListener(this);
        findViewById(R.id.ll_price).setOnClickListener(this);
        findViewById(R.id.ll_new).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_search)).setText(rootName);

        gv_search.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (null != productList) {
                    productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
                getData();

            }

            @Override
            public void onLoadMore() {
                page += 1;
                getData();
            }
        });

        getData();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (null != productList) {
//            productList.clear();
//        }
//        getData();
//    }


    private List<HomeSearchResultBean.ProductListBean> productList;

    //网络请求获取数据
    private void getData() {
        if (allParamMap != null) {
            allParamMap.clear();
        }
        if (Utils.isNetworkAvailable(mContext)) {
            ll_notNet.setVisibility(View.GONE);
            //区分搜索还是分类跳转
            productstyle = getIntent().getIntExtra("type", 1);
            //拼接之后的参数
            //allParameter = "?page=" + page + productName + "&" + style;
            allParamMap.put("page", page + "");
            if (productstyle == 1) {
                //分类
                allParamMap.put("classTypeId", getIntent().getStringExtra("rootId"));
            } else {
                //搜索
                allParamMap.put("name", getIntent().getStringExtra("rootId"));
            }
            //是否人工筛选
            allParamMap.put("flag", isPeopleChange ? 1 + "" : 0 + "");

            if (showStyle == 0) {
                //按销量
                allParamMap.put("isVolume", 1 + "");
            } else if (showStyle == 1) {
                //按好评排序
                allParamMap.put("isAppraise", 1 + "");
            } else if (showStyle == 2) {
                //按价格排序
                /**
                 * isPrice 1 价格从低到高排序
                 * isPrice 2 价格从高到低排序
                 */
                if (isHighToDown) {
                    //价格从高到低
                    allParamMap.put("isPrice", 2 + "");
                } else {
                    allParamMap.put("isPrice", 1 + "");
                }
            } else if (showStyle == 3) {
                //按新品排序
                allParamMap.put("isNew", 1 + "");
            }
            Netword.getInstance().getApi(HomeApi.class)
                    .searchResult(allParamMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<HomeSearchResultBean>(mContext) {
                        @Override
                        public void successed(HomeSearchResultBean result) {
                            if (result == null) {
                                nothingAll.setVisibility(View.VISIBLE);
                                return;
                            }

                            List<HomeSearchResultBean.ProductListBean> list = result.productList;
                            if (page == 1 && (list.toString().equals("[]") || list.size() <= 0)) {
                                nothingAll.setVisibility(View.VISIBLE);
                                return;
                            }
                            nothingAll.setVisibility(View.GONE);
//                            nothingData.setVisibility(View.GONE);
                            if (productList.size() > 0 && list.toString().equals("[]")) {
                                showShortToast("亲!已经到底了");
                                return;
                            }
                            for (int i = 0; i < list.size(); i++) {
                                productList.add(list.get(i));
                            }
                            //只有page=1 的时候设置适配器 下拉刷新直接调用notifyDataSetChanged()
                            if (1 == page) {
                                if (mAdapter == null) {
                                    mAdapter = new CommonRecyclerAdapter<HomeSearchResultBean.ProductListBean>(mContext, R.layout.item_home_last_product, productList) {

                                        @Override
                                        public void convert(ViewHolderRecycler viewHolder, HomeSearchResultBean.ProductListBean bean) {
                                            try {
                                                //原价
                                                TextView tvOldPrice = viewHolder.getView(R.id.tv_oldprice);
                                                tvOldPrice.setText("¥" + bean.price);
                                                //销量
                                                viewHolder.setText(R.id.tv_xiaoliang, "已抢" + bean.nowNumber + "件");
                                                //优惠券
                                                if (Utils.isZero(bean.couponMoney)) {
                                                    tvOldPrice.getPaint().setFlags(0);
                                                } else {
                                                    tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                                                }
                                                viewHolder.setText(R.id.coupon_money, bean.couponMoney + "元");
                                                //券后价
                                                String[] nowPrices = bean.preferentialPrice.split("\\.");
                                                String p1 = nowPrices[0];
                                                String p2;
                                                if (nowPrices.length == 1) {
                                                    p2 = ".00";
                                                } else {
                                                    if (nowPrices[1].length() == 1) {
                                                        p2 = "." + nowPrices[1] + "0";
                                                    } else {
                                                        p2 = "." + nowPrices[1];
                                                    }
                                                }
                                                viewHolder.setText(R.id.tv_nowprice1, p1);
                                                viewHolder.setText(R.id.tv_nowprice2, p2);
                                                //赚
                                                LinearLayout ll_zhuan_money = viewHolder.getView(R.id.ll_zhuan_money);
                                                if (Utils.isZero(bean.zhuanMoney)) {
                                                    ll_zhuan_money.setVisibility(View.GONE);
                                                } else {
                                                    String zhuanMoney = bean.zhuanMoney;
                                                    viewHolder.setText(R.id.zhuan, zhuanMoney.substring(0, 1));
                                                    viewHolder.setText(R.id.zhuan_money, zhuanMoney.substring(1, zhuanMoney.length()));
                                                    ll_zhuan_money.setVisibility(View.VISIBLE);
                                                }

                                                //图片
                                                viewHolder.displayImage(R.id.iv_img, bean.imgs, R.drawable.zhuan_shangpinjiazai);

                                                //商品名字
                                                viewHolder.setTextViewImageSpan(R.id.tv_name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    GridLayoutManager mLinearLayout = new GridLayoutManager(SearchResultActivity.this, 2);
                                    mLinearLayout.setSmoothScrollbarEnabled(true);

                                    gv_search.addItemDecoration(new GridItemDecoration(
                                            new GridItemDecoration.Builder(SearchResultActivity.this)
                                                    .margin(8, 8)
                                                    .horSize(10)
                                                    .verSize(10)
                                                    .showLastDivider(true)
                                                    .showHeadDivider(true)
                                    ));


                                    gv_search.setNestedScrollingEnabled(false);
                                    gv_search.setLayoutManager(mLinearLayout);
                                    gv_search.setAdapter(mAdapter);
                                    mAdapter.setOnItemClick(SearchResultActivity.this);
                                }
                            } else {

                            }

                            mAdapter.notifyDataSetChanged();
                            gv_search.refreshComplete();
                        }

                    });
        } else {
            ll_notNet.setVisibility(View.VISIBLE);
        }

    }


    //价格从高到底
    private boolean isHighToDown = true;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.ll_search: //搜索
                startActivity(new Intent(mContext, SearchActivity.class));
                finish();
                break;
            case R.id.ll_sale: //按销量排序
                //style = "isVolume=1";
                showStyle = 0;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.ll_like://按好评排序
                //style = "isAppraise=1";
                showStyle = 1;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.ll_price: //按价格排序

                /**
                 * isPrice 1 价格从低到高排序
                 * isPrice 2 价格从高到低排序
                 */
                showStyle = 2;
                selectShowStyle(showStyle);
                if (isHighToDown) {
                    //价格从高到低
                    //style = "isPrice=2";
                    iv_price.setImageResource(R.drawable.shousuohou_jiage_shang);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    if (productList != null)
                        productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    getData();

                } else {
                    // style = "isPrice=1";
                    iv_price.setImageResource(R.drawable.sousuohou_jiage_xia);
                    isHighToDown = !isHighToDown;
                    page = 1;
                    if (productList != null)
                        productList.clear();
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    getData();
                }
                break;
            case R.id.ll_new:  //按新品排序
                //style = "isNew=1";
                showStyle = 3;
                selectShowStyle(showStyle);
                page = 1;
                if (productList != null)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;
            case R.id.common_nothing_data:
                page = 1;
                if (null != productList)
                    productList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                getData();
                break;

            default:
                break;
        }
    }

    //选择展示的方式
    private void selectShowStyle(int showStyle) {

        if (showStyle == 0) {
            changeStyle(showStyle);
        } else if (showStyle == 1) {
            changeStyle(showStyle);
        } else if (showStyle == 2) {
            changeStyle(showStyle);
        } else if (showStyle == 3) {
            changeStyle(showStyle);
        }
    }

    private void changeStyle(int showStyle) {

        View[] v = {findViewById(R.id.tv_sale), findViewById(R.id.tv_like),
                findViewById(R.id.tv_price), findViewById(R.id.tv_new)};
        View[] v1 = {findViewById(R.id.v_sale), findViewById(R.id.v_like)
                , findViewById(R.id.v_price), findViewById(R.id.v_new)};
        for (int i = 0; i < v.length; i++) {
            ((TextView) v[i]).setTextColor(getResources().getColor(R.color.black));
        }
        for (int i = 0; i < v1.length; i++) {
            v1[i].setVisibility(View.GONE);
        }

        ((TextView) v[showStyle]).setTextColor(getResources().getColor(R.color.main));
        v1[showStyle].setVisibility(View.VISIBLE);
    }


    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(mContext, ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(productList.get(position))));
    }
}
