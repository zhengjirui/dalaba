package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.utils.Utils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author zhf 2017/08/14
 *         【意见反馈】
 */
public class FeedBackActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_opinion)
    EditText et_opinion;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private Context mContext = FeedBackActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        initView();
    }


    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("意见反馈");
    }

    @OnClick({R.id.iv_back, R.id.btn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_ok:
                if (Utils.isEmpty(et_opinion)) {
                    Utils.show(this, "请输入意见反馈");
                } else {
                    //提交意见
                    commitOpinion(et_opinion.getText().toString());
                }
                break;
        }
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/9/25  18:12
     * @describe 上传意见
     */
    private void commitOpinion(String opinion) {
        final HashMap<String,String> map=new HashMap();
        map.put("opinion",opinion);
        Netword.getInstance().getApi(CommenApi.class)
                .opinion(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
                        Utils.show(mContext,result);
                        finish();
                    }
                });
    }
}
