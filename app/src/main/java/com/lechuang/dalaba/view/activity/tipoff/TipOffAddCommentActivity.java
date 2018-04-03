package com.lechuang.dalaba.view.activity.tipoff;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.netApi.TipoffShowApi;
import com.lechuang.dalaba.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：li on 2017/10/7 15:38
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class TipOffAddCommentActivity extends AppCompatActivity {
    @BindView(R.id.iv_addCommentClose)
    ImageView ivAddCommentClose;
    @BindView(R.id.tv_addCommentSubmit)
    TextView tvAddCommentSubmit;
    @BindView(R.id.et_comment)
    EditText etComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_off_add_comment);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_addCommentClose, R.id.tv_addCommentSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_addCommentClose:
                finish();
                break;
            case R.id.tv_addCommentSubmit:
                if (Utils.isEmpty(etComment)) {
                    Utils.show(this,"请输入内容");
                    return;
                }
                //不能包含emoji表情
                if(Utils.containsEmoji(etComment.getText().toString())){
                    Utils.show(this,this.getResources().getString(R.string.no_emoji));
                    return;
                }
                //添加评论
                submit();
                break;
        }
    }

    private void submit() {
        int type = getIntent().getIntExtra("type",1);
        String id = getIntent().getStringExtra("tipId");
        String content =etComment.getText().toString().trim();
        Netword.getInstance().getApi(TipoffShowApi.class)
                .sendContent(id,type,content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        Utils.show(TipOffAddCommentActivity.this,result);
                        finish();
                    }
                });
    }
}
