package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.presenter.net.QUrl;
import com.lechuang.dalaba.utils.Utils;

/**
 * 作者：li on 2017/10/6 15:49
 * 邮箱：961567115@qq.com
 * 修改备注:代理分享邀请好友
 */
public class AgentShareActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext = AgentShareActivity.this;
    private SharedPreferences sp;
    private String url;
    private ImageView mWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_money);
        initVeiw();
    }

    private void initVeiw() {
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        //分享
        findViewById(R.id.iv_right).setOnClickListener(this);
        findViewById(R.id.web_back).setOnClickListener(this);
        mWeb = (ImageView) findViewById(R.id.iv_share);
        url = QUrl.activityShare + "?i=" + sp.getString("id","");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_right:
               /* NativeShareDialog shareDialog = new NativeShareDialog(mContext);
                shareDialog.setTitle("分享代理");
                //shareDialog.setBmUrl(img);
                shareDialog.setDescription("");
                shareDialog.setUrl(url);
                shareDialog.setPicUrl("");
                shareDialog.noSaveBitmap();
                shareDialog.show();*/
                Utils.systemShare(mContext, getResources().getString(R.string.app_name) + "分享代理:" + "\n" + url);
                break;
            case R.id.web_back:
                finish();
                break;
            default:
                break;
        }
    }
}
