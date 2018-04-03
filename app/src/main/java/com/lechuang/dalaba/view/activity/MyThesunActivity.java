package com.lechuang.dalaba.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.model.bean.UpFileBean;
import com.lechuang.dalaba.presenter.RecyclerItemClickListener;
import com.lechuang.dalaba.presenter.adapter.MyThesunAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.TheSunApi;
import com.lechuang.dalaba.utils.DialogUtil;
import com.lechuang.dalaba.utils.ImageSelectorUtils;
import com.lechuang.dalaba.utils.LogUtils;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.DialogAlertView;
import com.lechuang.dalaba.view.defineView.RatingBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lechuang.dalaba.base.Constants.RESULT_CODE;

/**
 * @author zhf 2017/08/08
 *         我要晒单
 */
public class MyThesunActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 0x00000011;
    private Context mContext = MyThesunActivity.this;
    private TextView tv_complete;
    private RecyclerView rvImage;
    private MyThesunAdapter mAdapter;
    private EditText tv_orderId;//订单编号
    private EditText et_comment;//评论
    private ArrayList<String> images;
    private RatingBar ratingbarId;
    private int starCount;//星星个数
    private String name;
    private List<MultipartBody.Part> parts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_thesun);
        initView();
        initEvents();
    }

    public void initView() {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        findViewById(R.id.tv_cancel).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_title)).setText("我要晒单");
        tv_complete = (TextView) findViewById(R.id.tv_complete);
        tv_complete.setVisibility(View.VISIBLE);
        tv_complete.setText("发送");
        tv_orderId = (EditText) findViewById(R.id.tv_orderId);//订单编号
        et_comment = (EditText) findViewById(R.id.et_comment);//评论
        rvImage = (RecyclerView) findViewById(R.id.rv_image);
        ratingbarId = (RatingBar) findViewById(R.id.ratingbarId);

    }

    public void initEvents() {

        tv_complete.setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        images = getIntent().getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
        rvImage.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new MyThesunAdapter(mContext);
        if (null != images) {
            mAdapter.refresh(images);
        }
        rvImage.setAdapter(mAdapter);
        rvImage.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (mAdapter.getItemViewType(position) == MyThesunAdapter.ADD_IMAGE) {
                    DialogUtil.showDialog(mContext, new DialogUtil.onClickItem() {
                        @Override
                        public void clickItem(int item) {
                            switch (item) {
                                case 1://拍照
                                    getPicture();
                                    break;
                                case 2://相册
                                    //多选(最多9张)
                                    int count = MyThesunAdapter.MAX - position;
                                    ImageSelectorUtils.openModifyPhoto((Activity) mContext, REQUEST_CODE, false, count, images);
                                    break;
                                case 3://取消

                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        }));
        //默认星星5个
        starCount = 5;
        //选择星星的个数
        ratingbarId.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                starCount = (int) ratingCount;

            }
        });
    }

    //拍照
    private void getPicture() {
        name = UUID.randomUUID() + ".jpg";//UUID随机ID数
        //判断内存卡是否存在
        String SDStart = Environment.getExternalStorageState();
        if (SDStart.equals(Environment.MEDIA_MOUNTED)) {
            File file1 = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));//文件夹是否存在
            if (!file1.exists()) {
                file1.mkdir();
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + name);
            Uri imageUri = FileProvider.getUriForFile(mContext, Constants.package_fileProvider, file);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, RESULT_CODE);
        } else {
            Utils.show(mContext, "内存卡不存在");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE) {
            // TODO:解决调出相机不拍照就返回情况下,是一张黑色图片的情况
            if(resultCode!=Activity.RESULT_CANCELED)
            {
                if (null == images) {
                    images = new ArrayList<>();
                }
                images.add(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + name);
                mAdapter.refresh(images);
            }
        }
        if (requestCode == REQUEST_CODE && data != null) {
            images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
            mAdapter.refresh(images);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete://发送
                String orderNum = Utils.getTextFromEditText(tv_orderId).trim();
                if (orderNum.equals("")) {
                    LogUtils.toast(this, "请输入订单编号");
                    return;
                }
                if (!Utils.isNumeric(orderNum.trim())) {
                    LogUtils.toast(this, "订单号不包含数字之外的内容");
                    return;
                }
                if (orderNum.contains(" ")) {
                    LogUtils.toast(this, "订单号不能包含空格");
                    return;
                }
                if (images == null || images.size() < 0) {
                    /*Toast toast = Toast.makeText(getApplicationContext(),
                            "请添加图片", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.close1);
                    toastView.addView(imageCodeProject, 0);
                    toast.show();*/
                    Utils.show(this, "请添加图片");
                    return;
                }
                sendOrder();
                break;
            case R.id.tv_cancel://取消
                final DialogAlertView dialog = new DialogAlertView(mContext, R.style.CustomDialog);
                dialog.setView(R.layout.dialog_unlogin);
                dialog.show();
                ((TextView) dialog.findViewById(R.id.txt_notice)).setText("退出此次编辑吗?");
                dialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.txt_exit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                break;

        }
    }

    /**
     * @author li
     * 邮箱：961567115@qq.com
     * @time 2017/10/7  21:30
     * @describe 发送评论
     */
    private void sendOrder() {
        String trim = Utils.getTextFromEditText(tv_orderId).trim();
        Netword.getInstance().getApi(TheSunApi.class)
                .isRightOrder(trim)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(this) {
                    @Override
                    public void successed(String result) {
                        upFile();
                    }
                });
    }

    /**
     * 上传图片
     */
    public void upFile() {
        parts = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            //这里采用的Compressor图片压缩
            File file = new Compressor.Builder(this)
                    .setMaxWidth(720)
                    .setMaxHeight(1080)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToFile(new File(images.get(i)));
            RequestBody requestFile = RequestBody
                    .create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody
                    .Part.createFormData("file", file.getName(), requestFile);
            parts.add(part);
        }

        Netword.getInstance().getApi(TheSunApi.class)
                .fileUpload(parts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<UpFileBean>(this) {
                    @Override
                    public void successed(UpFileBean result) {
                        sendData(result.imageId);
                    }
                });
    }

    /**
     * 上传数据
     */
    public void sendData(String imgUrl) {
        String comment = et_comment.getText().toString().trim();
        String order = tv_orderId.getText().toString().trim();
        Netword.getInstance().getApi(TheSunApi.class)
                .sunComment(comment, imgUrl, starCount, order)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(mContext) {
                    @Override
                    public void successed(String result) {
                        Utils.show(mContext, result);
                        finish();
                    }
                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != images) {
            images.clear();
        }
    }
}
