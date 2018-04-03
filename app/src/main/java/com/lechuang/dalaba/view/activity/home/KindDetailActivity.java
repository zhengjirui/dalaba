package com.lechuang.dalaba.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.RollPagerView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.model.bean.HomeSearchResultBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.HomeApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.MGridView;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/3
 * @E-mail 1422947831@qq.com
 * @desc 栏目详情
 */
public class KindDetailActivity extends BaseActivity {

    private Context mContext = KindDetailActivity.this;
    private PullToRefreshScrollView refreshScrollView;//刷新
    //轮播图
    private RollPagerView rollPagerView;
    private String label;
    private MGridView gv_product;
    //没有网络状态
    private LinearLayout ll_noNet;
    //刷新重试按钮
    private ImageView iv_tryAgain;
    //分页页数
    private int page = 1;
    //参数map
    private HashMap<String, String> allParamMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productList = new ArrayList<>();
        linkList = new ArrayList<>();
        imgList = new ArrayList<>();
        allParamMap = new HashMap<>();
        getData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_program_detail;
    }

    @Override
    protected void initTitle() {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.iv_back2).setVisibility(View.VISIBLE);
        //标题
        ((TextView) findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("name"));
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //没有网络时的默认图片
        ll_noNet = (LinearLayout) findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) findViewById(R.id.iv_tryAgain);
        iv_tryAgain.setOnClickListener(this);
        gv_product = (MGridView) findViewById(R.id.gv_product);
        refreshScrollView = (PullToRefreshScrollView) findViewById(R.id.refresh);//刷新
        rollPagerView = (RollPagerView) findViewById(R.id.rollPagerView);
        //没有轮播图
        rollPagerView.setVisibility(View.GONE);

    }

    @Override
    protected void initData() {
        label = DateUtils.formatDateTime(
                getApplicationContext(),
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL);

        //refreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);//两端刷新
        refreshScrollView.onRefreshComplete();
        refreshScrollView.setOnRefreshListener(refresh);
        refreshScrollView.onRefreshComplete();
        //获取焦点  必须先获取焦点才能在顶部  另外内部的listview gridView不能有焦点
        refreshScrollView.setFocusable(true);
        refreshScrollView.setFocusableInTouchMode(true);
        refreshScrollView.requestFocus();
        refreshScrollView.getRefreshableView().smoothScrollTo(0, 0);
    }

    //商品集合
    private List<HomeSearchResultBean.ProductListBean> productList;
    //轮播图链接
    private List<String> linkList;
    //图片集合
    private List<String> imgList;
    //商品适配器
    private CommonAdapter<HomeSearchResultBean.ProductListBean> productAdapter;

    private void getData() {
        allParamMap.put("page", page + "");
        allParamMap.put("type", getIntent().getIntExtra("type", 1) + "");
        if (Utils.isNetworkAvailable(mContext)) {
            //网络畅通 隐藏无网络状态
            ll_noNet.setVisibility(View.GONE);
            refreshScrollView.setVisibility(View.VISIBLE);
            showWaitDialog("");
            Netword.getInstance().getApi(HomeApi.class)
                    .searchResult(allParamMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultBack<HomeSearchResultBean>(mContext) {
                        @Override
                        public void successed(HomeSearchResultBean result) {
                            kindData(result);
                        }
                    });
        } else {
            //网络不通 展示无网络状态
            ll_noNet.setVisibility(View.VISIBLE);
            refreshScrollView.setVisibility(View.GONE);
            hideWaitDialog();
        }


    }

    //商品头图片 淘宝或天猫
    private int headImg;

    private void kindData(HomeSearchResultBean result) {
        if (result == null)
            return;
        ll_noNet.setVisibility(View.GONE);
        refreshScrollView.setVisibility(View.VISIBLE);
        //商品集合
        List<HomeSearchResultBean.ProductListBean> list = result.productList;
        for (int i = 0; i < list.size(); i++) {
            productList.add(list.get(i));
        }
        refreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        if (list.toString().equals("[]")) {            //数据没有了
            Utils.show(mContext, "亲!已经到底了");
            //hideWaitDialog();
            refreshScrollView.onRefreshComplete();
            return;
        }
        if (page == 1) {
            productAdapter = new CommonAdapter<HomeSearchResultBean.ProductListBean>(mContext, productList, R.layout.item_program_product) {
                @Override
                public void setData(ViewHolder viewHolder, Object item) {
                    HomeSearchResultBean.ProductListBean bean = (HomeSearchResultBean.ProductListBean) item;
                    viewHolder.setText(R.id.tv_xiaoliang, "销量: " + bean.nowNumber + "件");
                    viewHolder.displayImage(R.id.iv_img, bean.imgs);
                    //原件
                    viewHolder.setText(R.id.tv_oldprice, "¥" + bean.price);
                    viewHolder.setSpannelTextViewGrid(R.id.tv_name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
                    viewHolder.setText(R.id.tv_nowprice, "券后价 ¥" + bean.preferentialPrice);
                }
            };
            gv_product.setAdapter(productAdapter);
        } else {
            productAdapter.notifyDataSetChanged();
        }

        gv_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(mContext, ProductDetailsActivity.class)
                        .putExtra(Constants.listInfo, JSON.toJSONString(productList.get(position))));

            }
        });
        hideWaitDialog();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_tryAgain://刷新重试
                page = 1;
                if (imgList != null)
                    imgList.clear();
                if (linkList != null)
                    linkList.clear();
                if (productList != null)
                    productList.clear();
                getData();
                break;
            default:
                break;
        }
    }

    private PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {

            // 显示最后更新的时间
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page = 1;
            if (imgList != null)
                imgList.clear();
            if (linkList != null)
                linkList.clear();
            if (productList != null)
                productList.clear();
            getData();
            refreshScrollView.onRefreshComplete();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            // 显示最后更新的时间
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page += 1;
            getData();
            refreshScrollView.onRefreshComplete();
        }
    };
}
