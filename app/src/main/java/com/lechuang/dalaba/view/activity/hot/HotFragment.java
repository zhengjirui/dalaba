package com.lechuang.dalaba.view.activity.hot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
import com.lechuang.dalaba.model.bean.HomeProgramDetailBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.adapter.BannerAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.HomeApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.home.EmptyWebActivity;
import com.lechuang.dalaba.view.activity.home.ProductDetailsActivity;
import com.lechuang.dalaba.view.defineView.MGridView;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author yrj
 * @date 2017/10/3
 * @E-mail 1422947831@qq.com
 * @desc 栏目详情
 */
public class HotFragment extends BaseFragment{

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
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_program_detail, container, false);
        productList = new ArrayList<>();
        linkList = new ArrayList<>();
        imgList = new ArrayList<>();
        initView();
        getData();
        return v;
    }


    private void initView() {
        //标题
        ((TextView) v.findViewById(R.id.tv_title)).setText("人气榜");
        v.findViewById(R.id.iv_back).setVisibility(View.GONE);
        //没有网络时的默认图片
        ll_noNet = (LinearLayout) v.findViewById(R.id.ll_noNet);
        //刷新重试
        iv_tryAgain = (ImageView) v.findViewById(R.id.iv_tryAgain);
        iv_tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                if (imgList != null)
                    imgList.clear();
                if (linkList != null)
                    linkList.clear();
                if (productList != null)
                    productList.clear();
                getData();
            }
        });
        gv_product = (MGridView) v.findViewById(R.id.gv_product);
        refreshScrollView = (PullToRefreshScrollView) v.findViewById(R.id.refresh);//刷新
        rollPagerView = (RollPagerView) v.findViewById(R.id.rollPagerView);

        label = DateUtils.formatDateTime(
                getActivity(),
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
    private List<HomeProgramDetailBean.ProductListBean> productList;
    //轮播图链接
    private List<String> linkList;
    //图片集合
    private List<String> imgList;
    //商品适配器
    private CommonAdapter<HomeProgramDetailBean.ProductListBean> productAdapter;

    private void getData() {
        Netword.getInstance().getApi(HomeApi.class)
                .program4(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeProgramDetailBean>(getActivity()) {
                    @Override
                    public void successed(HomeProgramDetailBean result) {
                        productData(result);
                    }
                });
        /*if (Utils.isNetworkAvailable(getActivity())) {
            //网络畅通 隐藏无网络状态
            ll_noNet.setVisibility(View.GONE);
            refreshScrollView.setVisibility(View.VISIBLE);
            showWaitDialog("");
            if (programaId == 1) {
                //栏目1详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program1(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            } else if (programaId == 2) {
                //栏目2详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program2(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            } else if (programaId == 3) {
                //栏目3详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program3(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            } else if (programaId == 4) {
                //栏目4详情
                Netword.getInstance().getApi(HomeApi.class)
                        .program4(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultBack<HomeProgramDetailBean>(mContext) {
                            @Override
                            public void successed(HomeProgramDetailBean result) {
                                productData(result);
                            }
                        });
            }
        } else {
            //网络不通 展示无网络状态
            ll_noNet.setVisibility(View.VISIBLE);
            refreshScrollView.setVisibility(View.GONE);
            hideWaitDialog();
        }*/


    }

    //商品头图片 淘宝或天猫
    private int headImg = 1;

    private void productData(HomeProgramDetailBean result) {
        if (result == null)
            return;
        ll_noNet.setVisibility(View.GONE);
        refreshScrollView.setVisibility(View.VISIBLE);
        //商品集合
        List<HomeProgramDetailBean.ProductListBean> list = result.productList;
        for (int i = 0; i < list.size(); i++) {
            productList.add(list.get(i));
        }
        refreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        if (list.toString().equals("[]")) {            //数据没有了
            Utils.show(getActivity(), "亲!已经到底了");
            //hideWaitDialog();
            refreshScrollView.onRefreshComplete();
            return;
        }
        if (page == 1) {
            //轮播图
            List<HomeProgramDetailBean.IndexBannerListBean> list1 = result.indexBannerList;
            for (int i = 0; i < list1.size(); i++) {
                //只取图片和链接
                imgList.add(list1.get(i).img);
                linkList.add(list1.get(i).link);
            }
            //设置播放时间间隔
            rollPagerView.setPlayDelay(3000);
            //设置透明度
            rollPagerView.setAnimationDurtion(500);
            //设置适配器
            rollPagerView.setAdapter(new BannerAdapter(getActivity(), imgList));
            //自定义指示器图片
            //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
            //设置圆点指示器颜色
            rollPagerView.setHintView(new ColorPointHintView(getActivity(), Color.YELLOW, Color.WHITE));
            rollPagerView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    startActivity(new Intent(getActivity(), EmptyWebActivity.class)
                            .putExtra("url", linkList.get(position)));
                }
            });
            productAdapter = new CommonAdapter<HomeProgramDetailBean.ProductListBean>(getActivity(), productList, R.layout.item_hot_product) {
                @Override
                public void setData(ViewHolder viewHolder, Object item) {
                    HomeProgramDetailBean.ProductListBean bean = (HomeProgramDetailBean.ProductListBean) item;
                    viewHolder.displayImage(R.id.iv_img, bean.imgs);
                    //原件
                    viewHolder.setText(R.id.tv_oldprice, "原价 ¥" + bean.price);
                    viewHolder.setText(R.id.tv_xiaoliang, "销量:" + bean.nowNumber + "件");
                    viewHolder.setText(R.id.tv_name, bean.name);
                    viewHolder.setText(R.id.tv_nowprice, "券后价 ¥" + bean.preferentialPrice);
                    /*if (bean.shopType != null && bean.shopType.equals("2")) {
                        headImg = R.drawable.zhuan_tianmao;
                    } else {  //shopType 1淘宝 2天猫 默认淘宝
                        headImg = R.drawable.zhuan_taobao;
                    }
                    Drawable drawable = getResources().getDrawable(headImg);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    ((TextView) viewHolder.getView(R.id.tv_name)).setCompoundDrawables(drawable, null, null, null);*/
                }
            };
            gv_product.setAdapter(productAdapter);
        } else {
            productAdapter.notifyDataSetChanged();
        }
        gv_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                        .putExtra("alipayCouponId", productList.get(position).alipayCouponId)
                        .putExtra("alipayItemId", productList.get(position).alipayItemId)
                        .putExtra("name", productList.get(position).name)
                        .putExtra("price", productList.get(position).price + "")
                        .putExtra("preferentialPrice", productList.get(position).preferentialPrice)
                        .putExtra("shareIntegral", productList.get(position).shareIntegral)
                        .putExtra("shopType", productList.get(position).shopType)
                        .putExtra("img", productList.get(position).imgs));
                        /*

                        .putExtra("alipayCouponId", productList.get(position).alipayCouponId)
                        .putExtra("alipayItemId", productList.get(position).alipayItemId)
                        .putExtra("nowPrice", productList.get(position).preferentialPrice)
                        .putExtra("price", productList.get(position).price + ""));*/
            }
        });
        hideWaitDialog();
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
