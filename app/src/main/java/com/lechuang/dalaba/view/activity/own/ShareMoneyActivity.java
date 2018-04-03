package com.lechuang.dalaba.view.activity.own;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.LeCommon;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.ShareModel;
import com.lechuang.dalaba.model.bean.ShareMoneyBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.OwnApi;
import com.lechuang.dalaba.utils.QRCodeUtils;
import com.lechuang.dalaba.view.dialog.ShareMoneyDialog;
import com.lechuang.dalaba.view.dialog.ShowShareDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：yrj on 2017/10/6 15:31
 * 邮箱：961567115@qq.com
 * 修改备注: 分享赚钱
 */
public class ShareMoneyActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView iv_share;
    private LocalSession session;
    private String url;

    private Context mContext = ShareMoneyActivity.this;
    private ImageView back;
    private int width;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_money);
        shareHandler = new ShareHandler(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        initVeiw();
        initDate();
    }

    private void initDate() {
       /* Netword.getInstance().getApi(OwnApi.class)
                .shareMoneyInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<ShareMoneyBean>(mContext) {
                    @Override
                    public void successed(ShareMoneyBean result) {
                        //result.image;
                        //result.shareUrl;
                    }
                });*/
    }

    private String shareUrl;
    private String image;

    private void initVeiw() {
        iv_share = (ImageView) findViewById(R.id.iv_share);
        back = (ImageView) findViewById(R.id.web_back);
        //访问网络结束后，设置返回键显示。
        findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
        findViewById(R.id.web_back).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_right).setOnClickListener(this);
        back.setOnClickListener(this);
        Netword.getInstance().getApi(OwnApi.class)
                .shareMoneyInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<ShareMoneyBean>(mContext) {
                    @Override
                    public void successed(ShareMoneyBean result) {
                        image = result.image;
                        shareUrl = result.shareUrl;
                        createBitmap();
                    }
                });

       /* session = LocalSession.get(this);
        //!=1 不是代理       =1 是代理
        if (session.getIsAgencyStatus() == 1) {
            url = QUrl.agentShare + "?i=" + session.getId();
        } else {
            url = QUrl.shareMoney;
        }*/
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_back:
                finish();
                break;
            case R.id.iv_right:
                shareByOneShare();
                break;
        }


    }

    private String mLocalUrl;
    private View content;

    private void createBitmap() {
        content = getLayoutInflater().inflate(R.layout.bitmap_share_money, null);
        final ImageView imageView = (ImageView) content.findViewById(R.id.share_image);

        Glide.with(this).load(image).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
                float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                Bitmap qrImage = QRCodeUtils.createQRImage(shareUrl, width / 6, width / 6);
                Message message = Message.obtain();
                message.obj = qrImage;
                shareHandler.sendMessage(message);

            }
        });
    }


    private ShareHandler shareHandler;

    private class ShareHandler extends Handler {
        WeakReference<Context> weakReference;

        public ShareHandler(Context context) {
            weakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context context = weakReference.get();
            if (context == null) return;
            ImageView codeView = (ImageView) content.findViewById(R.id.code);
            codeView.setImageBitmap((Bitmap) msg.obj);
            Bitmap bitmap = saveViewBitmap(content);
            try {
                if (TextUtils.isEmpty(mLocalUrl)) {
                    mLocalUrl = getExternalCacheDir() + "/" + System.currentTimeMillis() + ".png";
                }
                File dir = new File(mLocalUrl);
                FileOutputStream fileOutputStream = new FileOutputStream(dir);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                Bitmap bm = BitmapFactory.decodeFile(mLocalUrl);
                iv_share.setImageBitmap(bm);
                //分享
                //shareByOneShare();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 把View转化为bitmap
     *
     * @param view
     * @return
     */
    private Bitmap saveViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight());
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
        }
        Bitmap bmp = duplicateBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        return bmp;
    }

    /**
     * 改变像素点
     *
     * @param bmpSrc
     * @return
     */
    public Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }
        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();
        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Bitmap.Config.ARGB_8888);
        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            canvas.drawRect(rect, paint);
            canvas.drawBitmap(bmpSrc, 0, 0, paint);
        }
        return bmpDest;
    }

    private ShareMoneyDialog dialog;
    private SharedPreferences sp;

    //分享
    private void shareByOneShare() {
        if (sp.getInt(LeCommon.KEY_AGENCY_STATUS, 0) == 1) {
            //代理
            File[] imgUrlFiles = new File[1];
            imgUrlFiles[0] = new File(mLocalUrl);
            new ShowShareDialog(this,"lianjie").show();
//            new ShareModel().gotoShareImage(mContext, mLocalUrl);
            return;
        }
        dialog = new ShareMoneyDialog(this, R.layout.dialog_share_money);
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_daili).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ApplyAgentActivity.class));
            }
        });
        dialog.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File[] imgUrlFiles = new File[1];
                imgUrlFiles[0] = new File(mLocalUrl);
                new ShowShareDialog(mContext,imgUrlFiles).show();
//                new ShareModel().gotoShareImage(mContext, mLocalUrl);
            }
        });
        dialog.show();
    }
}
