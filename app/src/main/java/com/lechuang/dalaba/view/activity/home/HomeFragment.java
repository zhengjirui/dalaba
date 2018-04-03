package com.lechuang.dalaba.view.activity.home;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseFragment;
import com.lechuang.dalaba.base.Constants;
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
import com.lechuang.dalaba.view.defineView.MGridView;
import com.lechuang.dalaba.view.defineView.SpannelTextViewGrid;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;
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
public class HomeFragment extends BaseFragment {
    protected Subscription subscription;

    @BindView(R.id.tv_search)
    TextView tvSearch;         //搜索
    @BindView(R.id.gv_kind)
    GridView gvKind;        //首页分类
    Unbinder unbinder;
    @BindView(R.id.iv_top)
    ImageView ivTop;        //回到顶部
    @BindView(R.id.gv_product)
    MGridView gvProduct;    //首页最下商品gridview
    @BindView(R.id.iv_program1)
    ImageView ivProgram1;
    @BindView(R.id.iv_program2)
    ImageView ivProgram2;
    @BindView(R.id.iv_program3)
    ImageView ivProgram3;
    @BindView(R.id.iv_program4)
    ImageView ivProgram4;
    @BindView(R.id.ll_noNet)
    LinearLayout llNoNet; //没有网络
    @BindView(R.id.iv_tryAgain)
    ImageView tryAgain;
    @BindView(R.id.tv_auto_text)
    AutoTextView tvAutoText;


    private View v;
    private View contentView;
    private int scrollY = 0;// 标记上次滑动位置
    private ArrayList<String> text = null;
    //轮播图
    private RollPagerView mRollViewPager;
    //最下栏目广告图
    private ImageView lastRollViewPager;
    //刷新重试
    //private ImageView tryAgain;
    private PullToRefreshScrollView refreshScrollView;
    private ScrollView scrollView;
    private SharedPreferences sp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, v);
        initView();
        initEvent();
        getData();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return v;
    }

    //网络获取数据
    private void getData() {
        //showWaitDialog("");
        if (Utils.isNetworkAvailable(getActivity())) {
            refreshScrollView.setVisibility(View.VISIBLE);
            llNoNet.setVisibility(View.GONE);
            //获取首页轮播图数据
            getHomeBannerData();
            //首页分类数据
            getHomeKindData();
            //首页滚动文字数据
            getHomeScrollTextView();
            //首页4个图片栏目数据
            getHomeProgram();
            //首页最下栏目
            getLastProgramData();
            //lastData();
        } else {
            //隐藏加载框
            hideWaitDialog();
            llNoNet.setVisibility(View.VISIBLE);//刷新重试
            refreshScrollView.setVisibility(View.GONE);
            showShortToast(getString(R.string.net_error));
        }

    }


    private void initView() {
        refreshScrollView = (PullToRefreshScrollView) v.findViewById(R.id.refreshScrollView);
        scrollView = refreshScrollView.getRefreshableView();
        //轮播图
        mRollViewPager = (RollPagerView) v.findViewById(R.id.rv_banner);
        lastRollViewPager = (ImageView) v.findViewById(R.id.lastRollViewPager);
        //refreshScrollView最上方显示,防止refreshScrollView初始化时不在最上方
        //获取焦点  必须先获取焦点才能在顶部  另外内部的listview gridView不能有焦点
        refreshScrollView.setFocusable(true);
        refreshScrollView.setFocusableInTouchMode(true);
        refreshScrollView.requestFocus();
        refreshScrollView.getRefreshableView().scrollTo(0, 0);
    }

    private void initEvent() {
        //tryAgain = (ImageView) v.findViewById(R.id.tryAgain);
        lastRollViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TipOffActivity.class));

            }
        });

        //刷新监听
        refreshScrollView.setOnRefreshListener(refresh);
        if (contentView == null) {
            contentView = scrollView.getChildAt(0);
        }
        //监听ScrollView滑动停止
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }

            private void handleStop(Object view) {

                ScrollView scroller = (ScrollView) view;
                scrollY = scroller.getScrollY();

                doOnBorderListener();
            }
        });
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
                    public void onError(Throwable e) {
                        super.onError(e);
                        hideWaitDialog();
                    }

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
                                } else if (programaId == 5) {
                                    //栏目5
                                    handler.sendEmptyMessage(1);
                                } else {
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
                                viewHolder.displayImage(R.id.iv_kinds_img, bean.img);
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
                                /*intent.putExtra("rootName", list1.get(position).rootName);*/
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
                                       text.add(list.get(i).name);
                                   }
                                   //自定义的滚动textview
                                   tvAutoText.setTextAuto(text);
                                   //设置时间
                                   tvAutoText.setGap(3000);
                                   tvAutoText.setOnItemClickListener(new AutoTextView.onItemClickListener() {
                                       @Override
                                       public void onItemClick(int position) {
                                           if (sp.getBoolean("isLogin", false)) {
                                               startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                                       .putExtra(Constants.listInfo, JSON.toJSONString(list.get(position))));
                                           } else {
                                               startActivity(new Intent(getActivity(), LoginActivity.class));
                                           }
                                       }
                                   });
                               }
                           }
                );
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
                        try {
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //最下栏目商品集合
    private ArrayList<HomeLastProgramBean.ListBean> lastProgramList = new ArrayList<>();
    //适配器
    private CommonAdapter<HomeLastProgramBean.ListBean> lastAdapter;

    private int headImg;

    //首页最下栏目的数据
    private void getLastProgramData() {
        subscription = Netword.getInstance().getApi(HomeApi.class)
                .homeLastPage(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<HomeLastProgramBean>(getActivity()) {
                    @Override
                    public void successed(HomeLastProgramBean result) {
                        if (result == null)
                            return;
                        //商品集合
                        List<HomeLastProgramBean.ListBean> list = result.productList;
                        for (int i = 0; i < list.size(); i++) {
                            lastProgramList.add(list.get(i));
                        }
                        refreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
                        if (page != 1 && list.toString().equals("[]")) {            //数据没有了
                            Utils.show(getActivity(), "亲!已经到底了");
                            hideWaitDialog();
                            refreshScrollView.onRefreshComplete();
                            return;
                        }
                        if (page == 1) {
                            //轮播图图片数据集合
                            List<HomeLastProgramBean.programaBean.ListBean> list1 = result.programa.indexBannerList;
                            /*List<String> imgList = new ArrayList<>();
                            for (int i = 0; i < list1.size(); i++) {
                                imgList.add(list1.get(i).img);
                            }*/
                            Glide.with(getActivity()).load(list1.get(0).img).into(lastRollViewPager);
                            lastAdapter = new CommonAdapter<HomeLastProgramBean.ListBean>(getActivity(), lastProgramList, R.layout.item_home_last_product) {
                                @Override
                                public void setData(ViewHolder viewHolder, Object item) {
                                    HomeLastProgramBean.ListBean bean = (HomeLastProgramBean.ListBean) item;

                                    viewHolder.displayImage(R.id.iv_img, bean.imgs);
                                    //原件
                                    TextView view = viewHolder.getView(R.id.tv_oldprice);
                                    view.setText("¥" + bean.price);
                                    view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    //销量
                                    //viewHolder.setText(R.id.tv_number, "已抢" + bean.nowNumber + "件");
                                    //商品名字
                                    //viewHolder.setText(R.id.tv_juan, "领券减" + bean.couponMoney);
                                      /*  viewHolder.setText(R.id.tv_name, bean.name);*/
                                    //新件
                                    viewHolder.setText(R.id.tv_nowprice, "¥" + bean.preferentialPrice);
                                    ((SpannelTextViewGrid) viewHolder.getView(R.id.tv_name)).setShopType(bean.shopType == null ? 1 : Integer.parseInt(bean.shopType));
                                    ((SpannelTextViewGrid) viewHolder.getView(R.id.tv_name)).setDrawText(bean.name);
                                }
                            };
                            gvProduct.setAdapter(lastAdapter);
                        } else {
                            lastAdapter.notifyDataSetChanged();
                        }
                        gvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                startActivity(new Intent(getActivity(), ProductDetailsActivity.class)
                                        .putExtra(Constants.listInfo, JSON.toJSONString(lastProgramList.get(position))));

                            }
                        });
                        //隐藏加载框
                        hideWaitDialog();
                    }
                });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int high = v.findViewById(R.id.iv_jump).getTop();
                            refreshScrollView.getRefreshableView().smoothScrollTo(0, high);
                        }
                    }).start();
                    break;
                default:
                    break;
            }

        }
    };

    @OnClick({R.id.tv_search, R.id.iv_left, R.id.iv_right, R.id.iv_top, R.id.iv_tryAgain})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //任务中心
            case R.id.iv_left:
                if (sp.getBoolean("isLogin", false)) {
                    startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            //搜索
            case R.id.tv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            //签到
            case R.id.iv_right:
                if (sp.getBoolean("isLogin", false)) {
                    startActivity(new Intent(getActivity(), SigneActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            //底部跳转到顶部的按钮
            case R.id.iv_top:
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        //  scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
                        //  scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
                        //  需要注意的是，该方法不能直接被调用
                        //  因为Android很多函数都是基于消息队列来同步，所以需要异步操作，
                        //  addView完之后，不等于马上就会显示，而是在队列中等待处理，如果立即调用fullScroll， view可能还没有显示出来，所以会失败
                        //  应该通过handler在新线程中更新
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                ivTop.setVisibility(View.GONE);
                break;
            case R.id.iv_tryAgain:
                showWaitDialog("");
                //刷新数据
                page = 1;
                //清空商品集合
                if (lastProgramList != null)
                    lastProgramList.clear();
                getData();
                break;
            default:
                break;
        }
    }


    /**
     * ScrollView 的顶部，底部判断：
     * 其中getChildAt表示得到ScrollView的child View， 因为ScrollView只允许一个child
     * view，所以contentView.getMeasuredHeight()表示得到子View的高度,
     * getScrollY()表示得到y轴的滚动距离，getHeight()为scrollView的高度。
     * 当getScrollY()达到最大时加上scrollView的高度就的就等于它内容的高度了
     */
    private void doOnBorderListener() {
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= scrollView.getScrollY()
                + scrollView.getHeight()) {
            ivTop.setVisibility(View.VISIBLE);
        }
        // 顶部判断
        else if (scrollView.getScrollY() == 0) {
            ivTop.setVisibility(View.GONE);
            //Log.i(TAG, "top");
        } else if (scrollView.getScrollY() > 30) {
            ivTop.setVisibility(View.VISIBLE);
        }

    }

    //分页
    private int page = 1;
    //刷新加载
    private PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {

            // 显示最后更新的时间
            //refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            page = 1;
            //清空商品集合
            if (lastProgramList != null)
                lastProgramList.clear();
            getData();
            refreshScrollView.onRefreshComplete();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            // 显示最后更新的时间
            //refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            //showWaitDialog("");
            page += 1;
            getLastProgramData();
            refreshScrollView.onRefreshComplete();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



}

