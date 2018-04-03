package com.lechuang.dalaba.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.model.bean.SignBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by zhf on 2017/8/18.
 * 【签到】
 */
public class SigneActivity extends Activity {
    @BindView(R.id.iv_dismiss)
    ImageView ivDismiss;
    @BindView(R.id.tv_signin)
    TextView tvSignin;
    @BindView(R.id.gv_days)
    GridView gv_days;
    @BindView(R.id.btn_sign)
    Button btnSign;

    private Context mContext = SigneActivity.this;
    private String[] days = {"1天", "2天", "3天", "4天", "5天", "6天", "7天"};
    private ColorStateList redColors;

    private int count;  //签到天数
    private List<SignBean.ListBean> list;   //签到信息

    private Map<Integer, SignBean.ListBean> mapSigne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.activity_signe);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        redColors = ColorStateList.valueOf(0xfffe738d);

        //SpannableStringBuilder spanBuilder = new SpannableStringBuilder("连续签到7天,奖励翻倍哦~");
        //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
        //size  为0 即采用原始的正常的 size大小
        //spanBuilder.setSpan(new TextAppearanceSpan(null, 0, 0, redColors, null), 4, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        //((TextView) findViewById(R.id.tv_signin)).setText(spanBuilder);
        signInfo();

    }


    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/19  18:28
     * @describe 请求网络，查看签到信息
     */
    private void signInfo() {
        Netword.getInstance().getApi(CommenApi.class)
                .sign()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<SignBean>(mContext) {
                    @Override
                    public void successed(SignBean result) {
                        list = result.list;
                        int signStatus = result.signStatus;
                        if (signStatus == 1) {
                            btnSign.setText("已签到");
                            btnSign.setBackground(getResources().getDrawable(R.drawable.main_button_false));
                        } else {
                            btnSign.setText("今日签到");
                            btnSign.setBackground(getResources().getDrawable(R.drawable.main_button));
                        }

                        ((TextView) findViewById(R.id.tv_signin)).setText("连续签到,奖励翻倍哦~");

                        int size = list.size();
                        mapSigne = new HashMap<Integer, SignBean.ListBean>();
                        for (int i = 0; i < size; i++) {
                            mapSigne.put(list.get(i).continuousSigned, list.get(i));
                        }
                        count = result.continuousSigned;
                        if (count >= 6) {
                            gv_days.setAdapter(new SingeAdapter(6));
                        } else {
                            gv_days.setAdapter(new SingeAdapter(count - 1));
                        }
                    }
                });


    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/19  18:28
     * @describe 适配数据
     */
    private void sign() {
        Netword.getInstance().getApi(CommenApi.class)
                .signSuccessed()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
                        gv_days.setAdapter(new SingeAdapter(count));
                        Intent it = new Intent();
                        it.putExtra("sign", 1);
                        setResult(1, it);
                        btnSign.setText("已签到");
                        btnSign.setBackground(getResources().getDrawable(R.drawable.main_button_false));
                        Utils.show(mContext, result);
                    }
                });

    }

    @OnClick({R.id.iv_dismiss, R.id.btn_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_dismiss:
                finish();
                break;
            case R.id.btn_sign:
                sign();
                break;
        }
    }


    /**
     * 天数的适配器
     */
    class SingeAdapter extends BaseAdapter {
        private int index;
        private int images[] = {R.drawable.round_r, R.drawable.round};

        public SingeAdapter(int index) {
            this.index = index;
        }

        @Override
        public int getCount() {
            return days.length;
        }

        @Override
        public Object getItem(int position) {
            return days[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyTag mt = null;
            if (convertView == null) {
                mt = new MyTag();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_signin_days, null);
                mt.tv_days = (TextView) convertView.findViewById(R.id.tv_days);
                mt.tv_score = (TextView) convertView.findViewById(R.id.tv_score);
                mt.iv_round = (ImageView) convertView.findViewById(R.id.iv_round);
                convertView.setTag(mt);
            } else {
                mt = (MyTag) convertView.getTag();
            }

            //修改显示，用map集合储存（天数，签到信息）；
            SignBean.ListBean listBean = mapSigne.get(position + 1);
            mt.tv_score.setText("+" + (listBean.signStartIntegral + listBean.rewardIntegral));
            if (index == position) {
                mt.tv_days.setTextColor(getResources().getColor(R.color.main));
                mt.tv_score.setTextColor(getResources().getColor(R.color.main));
                mt.tv_days.setText((index + 1) + "天");
                mt.iv_round.setImageResource(images[0]);
            } else {
                mt.tv_days.setTextColor(getResources().getColor(R.color.black));
                mt.tv_score.setTextColor(getResources().getColor(R.color.black));
                mt.tv_days.setText(days[position]);
                mt.iv_round.setImageResource(images[1]);
            }

            notifyDataSetChanged();
            return convertView;
        }
    }

    /**
     * viewHolder类
     */
    class MyTag {
        private TextView tv_days, tv_score;//连续签到天数，积分
        private ImageView iv_round;
    }
}
