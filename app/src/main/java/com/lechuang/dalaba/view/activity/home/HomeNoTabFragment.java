package com.lechuang.dalaba.view.activity.home;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.mine.adapter.CommonRecyclerAdapter;
import com.lechuang.dalaba.mine.adapter.OnItemClick;
import com.lechuang.dalaba.mine.adapter.ViewHolderRecycler;
import com.lechuang.dalaba.mine.view.XRecyclerView;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.bean.HomeBannerBean;
import com.lechuang.dalaba.model.bean.HomeGunDongTextBean;
import com.lechuang.dalaba.model.bean.HomeKindBean;
import com.lechuang.dalaba.model.bean.HomeLastProgramBean;
import com.lechuang.dalaba.model.bean.HomeProgramBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.adapter.BannerAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.HomeApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.SigneActivity;
import com.lechuang.dalaba.view.activity.own.TaskCenterActivity;
import com.lechuang.dalaba.view.activity.tipoff.TipOffActivity;
import com.lechuang.dalaba.view.activity.ui.LoginActivity;
import com.lechuang.dalaba.view.defineView.AutoTextView;
import com.lechuang.dalaba.view.defineView.GridItemDecoration;
import com.lechuang.dalaba.view.defineView.MGridView;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class HomeNoTabFragment extends BaseFragment implements OnItemClick {
    protected Subscription subscription;

    //首页分类
    Unbinder unbinder;
    //自动滚动的textview
    private AutoTextView tvAutoText;
    private ImageView ivProgram1;
    private ImageView ivProgram2;
    private ImageView ivProgram3;
    private ImageView ivProgram4;
    private ImageView lastRollViewPager;
    private MGridView gvKind;
    private View lineHome;

    //首页最下商品gridview
    @BindView(R.id.rv_home_table)
    XRecyclerView rvHome;
    @BindView(R.id.iv_top)
    ImageView ivTop;        //回到顶部

    private View v;
    private ArrayList<String> text = null;
    //轮播图
    private RollPagerView mRollViewPager;
    private SharedPreferences sp;
    public boolean isBottom = false;
    private View header;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case LeCommon.ACTION_LOGIN_SUCCESS:
                case LeCommon.ACTION_LOGIN_OUT:
                    page = 1;
                    rvHome.smoothScrollToPosition(0);
                    ivTop.setVisibility(View.GONE);
                    getData();
                    break;
            }
        }
    };
    private GridLayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_notab, container, false);
        unbinder = ButterKnife.bind(this, v);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_SUCCESS));
        getActivity().registerReceiver(receiver, new IntentFilter(LeCommon.ACTION_LOGIN_OUT));
        initView();
        initEvent();
        getData();
        return v;
    }

    //网络获取数据
    public void getData() {
        if (Utils.isNetworkAvailable(getActivity())) {

            //获取首页轮播图数据
            getHomeBannerData();
            //首页分类数据
            getHomeKindData();
            //首页滚动文字数据
            getHomeScrollTextView();
            //首页4个图片栏目数据
            getHomeProgram();
            //首页商品列表
            getProductList();

        } else {
            //隐藏加载框
            showShortToast(getString(R.string.net_error));
        }

    }


    private void initView() {
        // TODO init XRecyclerView
        header = LayoutInflater.from(getActivity()).inflate(R.layout.header_home_notab,
                (ViewGroup) getActivity().findViewById(android.R.id.content),false);
        //轮播图
        mRollViewPager = (RollPagerView) header.findViewById(R.id.rv_banner);
        tvAutoText = (AutoTextView) header.findViewById(R.id.tv_auto_text);
        ivProgram1 = (ImageView) header.findViewById(R.id.iv_program1);
        ivProgram2 = (ImageView) header.findViewById(R.id.iv_program2);
        ivProgram3 = (ImageView) header.findViewById(R.id.iv_program3);
        ivProgram4 = (ImageView) header.findViewById(R.id.iv_program4);
        lastRollViewPager = (ImageView) header.findViewById(R.id.lastRollViewPager);
        gvKind = (MGridView) header.findViewById(R.id.gv_kind);

        rvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mShouldScroll) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mToPosition);
                }
                if (mLayoutManager.findLastVisibleItemPosition() > 15) {
                    ivTop.setVisibility(View.VISIBLE);
                } else {
                    ivTop.setVisibility(View.GONE);
                }
            }
        });

        rvHome.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getData();

            }

            @Override
            public void onLoadMore() {
                isBottom = true;
                page += 1;
                getProductList();
//                mAdapter.notifyDataSetChanged();
//                rvHome.refreshComplete();
            }
        });

    }

    private void initEvent() {
        //tryAgain = (ImageView) v.findViewById(R.id.tryAgain);
    }

    private List<HomeBannerBean.IndexBannerList> bannerList;

    //获取首页轮播图数据
    private void getHomeBannerData() {
        //首页轮播图数据
        Netword.getInstance().getApi(HomeApi.class)
                .homeBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeBannerBean>(getActivity()) {

                    @Override
                    public void successed(HomeBannerBean result) {
                        if (result == null) {
                            return;
                        }
                        bannerList = result.indexBannerList0;
                        List<String> imgList = new ArrayList<>();
                        for (int i = 0; i < bannerList.size(); i++) {
                            imgList.add(bannerList.get(i).img);
                        }
                        //设置播放时间间隔
                        mRollViewPager.setPlayDelay(3000);
                        //设置透明度
                        mRollViewPager.setAnimationDurtion(500);
                        //设置适配器
                        mRollViewPager.setAdapter(new BannerAdapter(getActivity(), imgList));
                        //设置指示器（顺序依次）
                        //自定义指示器图片
                        //设置圆点指示器颜色
                        //设置文字指示器
                        //隐藏指示器
                        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
                        mRollViewPager.setHintView(new ColorPointHintView(getActivity(), getResources().getColor(R.color.main), Color.WHITE));
                        //mRollViewPager.setHintView(new TextHintView(this));
                        //mRollViewPager.setHintView(null);
                        mRollViewPager.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                //获取到点击条目
                                int programaId = bannerList.get(position).programaId;
                                if (programaId == 1) {
                                    //栏目1
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 1));
                                } else if (programaId == 2) {
                                    //栏目2
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 2));
                                } else if (programaId == 3) {
                                    //栏目3
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 3));
                                } else if (programaId == 4) {
                                    //栏目4
                                    startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                            .putExtra("programaId", 4));
                                } else if(programaId == 5){
                                    smoothMoveToPosition(2);

                                }else if(programaId > 5 && programaId <= 10){
                                    //5-10的栏目不处理
                                }else {
                                    // TODO: 2017/10/1 跳转奔溃
                                    startActivity(new Intent(getActivity(), EmptyWebActivity.class)
                                            .putExtra("url", bannerList.get(position).link));
                                }
                            }
                        });
                    }
                });
    }

    //获取首页分类数据
    private void getHomeKindData() {
        //首页分类数据
        Netword.getInstance().getApi(HomeApi.class)
                .homeClassify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeKindBean>(getActivity()) {
                    @Override
                    public void successed(HomeKindBean result) {
                        if (result == null) {
                            return;
                        }
                        List<HomeKindBean.ListBean> list = result.tbclassTypeList;
                        //取前10类
                        if (list.size() > 10) {
                            list = list.subList(0, 10);
                        }
                        gvKind.setAdapter(new CommonAdapter<HomeKindBean.ListBean>(getActivity(), list, R.layout.home_kinds_item) {
                            @Override
                            public void setData(ViewHolder viewHolder, Object item) {
                                HomeKindBean.ListBean bean = (HomeKindBean.ListBean) item;
                                //分类名称
                                viewHolder.setText(R.id.tv_kinds_name, bean.rootName);
                                //分类图片
                                viewHolder.displaySquareImageView(R.id.iv_kinds_img, bean.img);
                            }
                        });
                        final List<HomeKindBean.ListBean> list1 = result.tbclassTypeList;
                        // TODO: 2017/10/2  分类item
                        gvKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                                //传递一个值,搜索结果页面用来判断是从分类还是搜索跳过去的 1:分类 2:搜索界面
                                intent.putExtra("type", 1);
                                //rootName传递过去显示在搜索框上
                                intent.putExtra("rootName", list1.get(position).rootName);
//                                intent.putExtra("rootName", "");
                                //rootId传递过去入参
                                intent.putExtra("rootId", list1.get(position).rootId + "");
                                startActivity(intent);
                            }
                        });
                    }
                });
    }

    //首页滚动文字数据
    private void getHomeScrollTextView() {
        Netword.getInstance().getApi(HomeApi.class)
                .gunDongText()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeGunDongTextBean>(getActivity()) {
                    @Override
                    public void successed(HomeGunDongTextBean result) {
                        if (result == null) {
                            return;
                        }
                        final List<HomeGunDongTextBean.IndexMsgListBean> list = result.indexMsgList;
                        //滚动TextView
                        text = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            text.add(list.get(i).productName);
                        }
                        //自定义的滚动textview
                        tvAutoText.setTextAuto(text);
                        //设置时间
                        tvAutoText.setGap(3000);
                        tvAutoText.setOnItemClickListener(new AutoTextView.onItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                               /* if (sp.getBoolean("isLogin", false)) {*/
                                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                        .putExtra(Constants.listInfo, JSON.toJSONString(list.get(position))));
                               /* } else {
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }*/
                            }
                        });
                    }
                });
    }

    //首页4个图片栏目数据
    private void getHomeProgram() {
        Netword.getInstance().getApi(HomeApi.class)
                .homeProgramaImg()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeProgramBean>(getActivity()) {
                    @Override
                    public void successed(HomeProgramBean result) {
                        if (result == null) {
                            return;
                        }
                        final List<HomeProgramBean.ListBean> list = result.programaImgList;
                        List<String> imgList = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) != null)
                                imgList.add(list.get(i).img);
                        }
                        //栏目1
                        if (imgList.get(0) != null)
                            Glide.with(getActivity()).load(imgList.get(0)).into(ivProgram1);
                        //栏目2
                        if (imgList.get(1) != null)
                            Glide.with(getActivity()).load(imgList.get(1)).into(ivProgram2);
                        //栏目3
                        if (imgList.get(2) != null)
                            Glide.with(getActivity()).load(imgList.get(2)).into(ivProgram3);
                        //栏目4
                        if (imgList.get(3) != null)
                            Glide.with(getActivity()).load(imgList.get(3)).into(ivProgram4);
                        ivProgram1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(0).programaId));
                            }
                        });
                        ivProgram2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(1).programaId));
                            }
                        });
                        ivProgram3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(2).programaId));
                            }
                        });
                        ivProgram4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ProgramDetailActivity.class)
                                        .putExtra("programaId", list.get(3).programaId));
                            }
                        });
                    }
                });
    }

    //顶部的条目
    private ArrayList<HomeLastProgramBean.ListBean> lastProgramList = new ArrayList<>();

    @OnClick({R.id.tv_search, R.id.iv_right, R.id.iv_left, R.id.iv_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                if (sp.getBoolean("isLogin", false)) {
                    startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                //搜索
                break;
            case R.id.tv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            //签到
            case R.id.iv_right:
                if (sp.getBoolean("isLogin", false) == true) {
                    startActivity(new Intent(getActivity(), SigneActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            //底部跳转到顶部的按钮
            case R.id.iv_top:
                // 回到顶部
                rvHome.smoothScrollToPosition(0);
                ivTop.setVisibility(View.GONE);
                break;
//            case R.id.iv_tryAgain:
//                //刷新数据
//                page = 1;
//                //清空商品集合
//                if (mProductList != null)
//                    mProductList.clear();
//                getData();
//                break;
            default:
                break;
        }
    }

    //分页
    private int page = 1;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        unbinder.unbind();
    }

    // 设置Adapter
    private CommonRecyclerAdapter mAdapter;
    // 底部商品
    private ArrayList<HomeLastProgramBean.ListBean> mProductList = new ArrayList<>();

    /**
     * 底部商品数据
     */
    private void getProductList() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("classTypeId", "0");

        subscription = Netword.getInstance().getApi(HomeApi.class)
                .homeLastProgram(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeLastProgramBean>(getActivity()) {
                    @Override
                    public void successed(HomeLastProgramBean result) {
                        if (result == null) return;
                        if (page == 1 && mProductList != null) {
                            mProductList.clear();
                        }
                        List<HomeLastProgramBean.ListBean> list = result.productList;
                        if (page > 1 && list.isEmpty()) {
//                            Utils.show(getActivity(), "亲,已经到底了~");
                            rvHome.noMoreLoading();
                            return;
                        }
                        mProductList.addAll(list);
                        int isAgencyStatus = sp.getInt("isAgencyStatus", 0);
                        if (page == 1) {
                            //轮播图图片数据集合
                            final List<HomeLastProgramBean.programaBean.ListBean> list1 = result.programa.indexBannerList;
                            Glide.with(getActivity()).load(list1.get(0).img).into(lastRollViewPager);
                            lastRollViewPager.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getActivity(), TipOffActivity.class));
//                                    if (!TextUtils.isEmpty(list1.get(0).link)) {
//                                        startActivity(new Intent(getActivity(), EmptyWebActivity.class)
//                                                .putExtra("url", list1.get(0).link));
//                                    }

                                }
                            });
                            if (mAdapter == null) {
                                mAdapter = new CommonRecyclerAdapter<HomeLastProgramBean.ListBean>(getActivity(), R.layout.item_home_last_product, mProductList) {
                                    @Override
                                    public void convert(ViewHolderRecycler viewHolder, HomeLastProgramBean.ListBean bean) {
                                        try {
                                            //原价
                                            TextView tvOldPrice = viewHolder.getView(R.id.tv_oldprice);
                                            tvOldPrice.setText("¥" + bean.price);
                                            //销量
                                            viewHolder.setText(R.id.tv_xiaoliang, "已抢" + bean.nowNumber+"件");
                                            //优惠券
                                            if (Utils.isZero(bean.couponMoney)) {
                                                tvOldPrice.getPaint().setFlags(0);
                                            }  else{
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
                                                viewHolder.setText(R.id.zhuan,zhuanMoney.substring(0,1));
                                                viewHolder.setText(R.id.zhuan_money,zhuanMoney.substring(1,zhuanMoney.length()));
                                                ll_zhuan_money.setVisibility(View.VISIBLE);
                                            }

                                            //图片
                                            viewHolder.displayImage(R.id.iv_img, bean.imgs,R.drawable.zhuan_shangpinjiazai);

                                            //商品名字
                                            viewHolder.setTextViewImageSpan(R.id.tv_name, bean.name, bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };


                                mLayoutManager = new GridLayoutManager(getActivity(), 2);
                                mLayoutManager.setSmoothScrollbarEnabled(true);

                                rvHome.addItemDecoration(new GridItemDecoration(
                                        new GridItemDecoration.Builder(getActivity())
                                                .isExistHead(true)
                                                .margin(8,8)
                                                .horSize(10)
                                                .verSize(10)
                                                .showLastDivider(true)
                                ));

                                rvHome.addHeaderView(header);
                                rvHome.setNestedScrollingEnabled(false);
                                rvHome.setLayoutManager(mLayoutManager);
                                rvHome.setAdapter(mAdapter);
                                mAdapter.setOnItemClick(HomeNoTabFragment.this);
                            }
                        } else {
//                            mAdapter.notifyDataSetChanged();
//                            rvHome.refreshComplete();
                        }
                        mAdapter.notifyDataSetChanged();
                        rvHome.refreshComplete();
                        isBottom = false;
                    }

                });
    }

    @Override
    public void itemClick(View v, int position) {
        startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                .putExtra(Constants.listInfo, JSON.toJSONString(mProductList.get(position))));
    }

    /**
     * recycleview滚到指定位置
     */
    private void smoothMoveToPosition(int position){
        int lanMuHeight = lastRollViewPager.getHeight();
        // 第一个可见位置
        int firstItem = rvHome.getChildLayoutPosition(rvHome.getChildAt(0));
        // 最后一个可见位置
        int lastItem = rvHome.getChildLayoutPosition(rvHome.getChildAt(rvHome.getChildCount() - 1));

        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            rvHome.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < rvHome.getChildCount()) {
                int top = rvHome.getChildAt(movePosition).getTop();
                rvHome.smoothScrollBy(0, top - lanMuHeight);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            rvHome.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

}

