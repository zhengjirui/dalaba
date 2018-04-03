package com.lechuang.dalaba.view.activity.sun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.ShareModel;
import com.lechuang.dalaba.model.bean.SunDetailBean;
import com.lechuang.dalaba.model.bean.SunDetailListBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.adapter.BannerAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.QUrl;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.TheSunApi;
import com.lechuang.dalaba.presenter.net.netApi.TipoffShowApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.home.ProductDetailsActivity;
import com.lechuang.dalaba.view.activity.tipoff.TipOffCommentDetailsActivity;
import com.lechuang.dalaba.view.defineView.MListView;
import com.lechuang.dalaba.view.defineView.RatingBar;
import com.lechuang.dalaba.view.defineView.ViewHolder;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;
import com.lechuang.dalaba.view.dialog.FlippingLoadingDialog;
import com.lechuang.dalaba.view.dialog.ShowShareDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.dalaba.base.Constants.listInfo;

/**
 * 作者：li on 2017/10/7 17:52
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class TheSunDetails extends AppCompatActivity {
    @BindView(R.id.iv_bigImg)
    RollPagerView ivBigImg;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_details)
    TextView tvDetails;
    @BindView(R.id.tv_issuer)
    TextView tvIssuer;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rb_sun_detail)
    RatingBar rbSunDetail;
    @BindView(R.id.iv_issuerHeadImg)
    XCRoundImageView ivIssuerHeadImg;
    @BindView(R.id.tv_seeAll)
    TextView tvSeeAll;
    @BindView(R.id.lv_comment)
    MListView lvComment;
    @BindView(R.id.sv_sun)
    PullToRefreshScrollView svSun;
    @BindView(R.id.tv_user_head)
    XCRoundImageView tvUserHead;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.iv_dianzan)
    ImageView ivDianzan;
    @BindView(R.id.tv_number_dianzan)
    TextView tvNumberDianzan;
    Context mContext = this;

    private ArrayList<String> img;
    //private String id;
    private String id1;
    private int status;
    protected FlippingLoadingDialog mLoadingDialog;//加载...
    private LocalSession mSession;
    SunDetailBean resultData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_sun_details);
        ButterKnife.bind(this);
        mLoadingDialog = new FlippingLoadingDialog(this, null);
        mSession = LocalSession.get(mContext);
        svSun.setOnRefreshListener(refresh);
        getData();
    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.tv_seeAll, R.id.tv_send,R.id.iv_dianzan,R.id.gotoDetail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gotoDetail:
                startActivity(new Intent(this, ProductDetailsActivity.class)
                        .putExtra(listInfo, JSON.toJSONString(resultData.product)));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:   //分享
                gotoShare();
                break;
            case R.id.iv_dianzan://点赞// ;
                changeVeiw();
                tipHelp(id1,2);
                break;
            case R.id.tv_seeAll:
                //传入一个id入参,whichComment作用判断哪个界面的评论详情 1:爆料 2:晒单
                startActivity(new Intent(mContext, TipOffCommentDetailsActivity.class)
                        .putExtra("tipOffId", getIntent().getStringExtra("id")).putExtra("whichComment", 2));
                break;
          /*  case R.id.tv_addComment:
                Intent intent = new Intent(mContext, TipOffAddCommentActivity.class);
                intent.putExtra("tipId", getIntent().getStringExtra("id"));
                intent.putExtra("type", 2);
                startActivity(intent);
                break;*/
            case R.id.tv_send:
                if (Utils.isEmpty(etContent)) {
                    Utils.show(this, "请输入内容");
                    return;
                }
                //不能包含emoji表情
                if (Utils.containsEmoji(etContent.getText().toString())) {
                    Utils.show(this, this.getResources().getString(R.string.no_emoji));
                    return;
                }
                //添加评论
                submit();
                break;
        }
    }

    /**
     * 分享
     */
    private void gotoShare() {

        String url = QUrl.url + "appH/html/sun_share.html?t=1&i=" + id1;
        new ShowShareDialog(this,url).show();
//        new ShareModel().gotoShareText(this, url);


    }


    private SunDetailBean.BaskOrderBean baskOrder;
    private boolean flag = true;
    /**
     * 初始化数据
     */
    public void getData() {
        showLoadingDialog(null);
        String id = getIntent().getStringExtra("id");
        Netword.getInstance().getApi(TheSunApi.class)
                .sunDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<SunDetailBean>(this) {
                    @Override
                    public void successed(SunDetailBean result) {
                        resultData=result;
                        baskOrder = result.baskOrder;
                        //设置刷新方式
                        svSun.setMode(PullToRefreshBase.Mode.BOTH);
                        img = new ArrayList<>();
                        tvTitle.setText(baskOrder.title);
                        tvDetails.setText(baskOrder.content);
                        //晒单的id
                        id1 = baskOrder.id;

                        if (result.status == 0) {//表示一点赞
                            flag = false;
                            ivDianzan.setSelected(true);
                        } else {//没点赞
                            flag = true;
                            ivDianzan.setSelected(false);
                        }
                        tvNumberDianzan.setText(baskOrder.praiseCount + "次赞");//点赞
                        if (mSession.getImge() != null) {
                            Glide.with(mContext).load(mSession.getImge()).into(tvUserHead);//用户头像
                        }
                        tvIssuer.setText("发布人：" + baskOrder.nickName);
                        tvTime.setText(baskOrder.createTimeStr);
                        rbSunDetail.setClickable(false);//星级不能改变
                        rbSunDetail.setStar(baskOrder.starLevel);
                        Glide.with(MyApplication.getInstance()).load(baskOrder.photo)
                                .into(ivIssuerHeadImg);
                        status = result.status;//点赞否：0点赞，1，没点赞
                        List<SunDetailBean.BaskOrderBean.Img1Bean> img1 = baskOrder.img1;
                        int size = img1.size();
                        for (int i = 0; i < size; i++) {
                            img.add(img1.get(i).imgUrl);
                        }
                        //设置播放时间间隔
                        ivBigImg.setPlayDelay(3000);
                        //设置透明度
                        ivBigImg.setAnimationDurtion(500);
                        //设置适配器
                        ivBigImg.setAdapter(new BannerAdapter(mContext, img));
                        //设置指示器（顺序依次）
                        //自定义指示器图片
                        //设置圆点指示器颜色
                        //设置文字指示器
                        //隐藏指示器
                        //ivBigImg.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
                        ivBigImg.setHintView(new ColorPointHintView(mContext, Color.YELLOW, Color.WHITE));
                        //mRollViewPager.setHintView(new TextHintView(this));
                        //mRollViewPager.setHintView(null);

                        //获取评论列表数据
                        getCommentData();
                        svSun.onRefreshComplete();

                        dismissLoadingDialog();

                    }
                });
    }

    /**
     * 发表评论
     */
    private void submit() {

        String id = getIntent().getStringExtra("id");
        String content = etContent.getText().toString().trim();
        Netword.getInstance().getApi(TipoffShowApi.class)
                .sendContent(id, 2, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(mContext, result);
                        etContent.setText("");
                        //刷新评论列表
                        getCommentData();
                    }
                });
    }


    //点赞
    private void tipHelp(String tipId, int index) {
        Netword.getInstance().getApi(TipoffShowApi.class)
                .tipPraise(tipId, index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
                        Utils.show(mContext,result);
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        //刷新评论列表
        getCommentData();
    }

    /**
     * 改变显示状态
     *
     * @describe
     */
    public void changeVeiw() {
        if (ivDianzan.isSelected()) {
            ivDianzan.setSelected(false);
            baskOrder.praiseCount = baskOrder.praiseCount - 1;
            tvNumberDianzan.setText(baskOrder.praiseCount + "次赞");
        } else {
            ivDianzan.setSelected(true);
            baskOrder.praiseCount = baskOrder.praiseCount + 1;
            tvNumberDianzan.setText(baskOrder.praiseCount + "次赞");
        }
    }

    // 显示加载框
    public void showLoadingDialog(String text) {
        if (text != null) {
            mLoadingDialog.setText(text);
        }

        mLoadingDialog.show();
    }

    // 关闭加载框
    public void dismissLoadingDialog() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 获取评论数据
     */
    public void getCommentData() {
        String id = getIntent().getStringExtra("id");
        Netword.getInstance().getApi(TheSunApi.class)
                .sunCommentList(id, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<SunDetailListBean>(mContext) {
                    @Override
                    public void successed(SunDetailListBean result) {
                        List<SunDetailListBean.AppraiseListBean> appraiseList = result.appraiseList;
                        CommonAdapter<SunDetailListBean.AppraiseListBean> commonAdapter =
                                new CommonAdapter<SunDetailListBean.AppraiseListBean>
                                        (mContext, appraiseList, R.layout.tipoff_comment_item) {

                                    @Override
                                    public void setData(ViewHolder viewHolder, Object item) {
                                        {
                                            final SunDetailListBean.AppraiseListBean tipAppraise = (SunDetailListBean.AppraiseListBean) item;
                                            if (tipAppraise.phone != null) {
                                                viewHolder.displayImage(R.id.iv_img, tipAppraise.photo);
                                            }
                                            viewHolder.setText(R.id.tv_nickname, tipAppraise.nickName);
                                            viewHolder.setText(R.id.tv_time, tipAppraise.createTimeStr);
                                            viewHolder.setText(R.id.tv_details, tipAppraise.content);

                                        }
                                    }
                                };

                        lvComment.setAdapter(commonAdapter);
                        svSun.onRefreshComplete();
                    }
                });


    }

    private PullToRefreshBase.OnRefreshListener2 refresh = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            String label = DateUtils.formatDateTime(
                    getApplicationContext(),
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            // 显示最后更新的时间
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            // 模拟加载任务
            getData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            // 显示最后更新的时间
            String label = DateUtils.formatDateTime(
                    getApplicationContext(),
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            Utils.show(mContext, "亲,已经到底了!");
            svSun.onRefreshComplete();
        }
    };
}

