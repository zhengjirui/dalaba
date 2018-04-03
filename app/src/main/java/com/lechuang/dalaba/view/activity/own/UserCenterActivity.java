package com.lechuang.dalaba.view.activity.own;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ali.auth.third.core.model.Session;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.model.bean.UpFileBean;
import com.lechuang.dalaba.model.bean.UpdataInfoBean;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.netApi.CommenApi;
import com.lechuang.dalaba.presenter.net.netApi.TheSunApi;
import com.lechuang.dalaba.utils.DialogUtil;
import com.lechuang.dalaba.utils.PhotoUtil;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * @author zhf 2017/08/14
 *         【个人信息】
 */
public class UserCenterActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.line_head)
    LinearLayout lineHead;
    @BindView(R.id.iv_head)
    XCRoundImageView ivHead;
    @BindView(R.id.line_user)
    LinearLayout lineUser;
    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.line_set_user)
    LinearLayout lineSetUser;
    @BindView(R.id.tv_userPhone)
    TextView tvUserPhone;
    @BindView(R.id.line_phone_number)
    LinearLayout linePhoneNumber;
    @BindView(R.id.tv_userTaobao)
    TextView tvUserTaobao;
    @BindView(R.id.ll_tb)
    LinearLayout llTb;
    @BindView(R.id.tv_userPay)
    TextView tvUserPay;
    @BindView(R.id.line_alipay)
    LinearLayout lineAlipay;
    private Context mContext = UserCenterActivity.this;

    private LocalSession mSession = LocalSession.get(mContext);
    private File file;
    //保存用户登录信息的sp
    private SharedPreferences se;

    private List<MultipartBody.Part> parts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
        initEvents();

    }

    public void initView() {
        //保存用户登录信息的sp
        se = PreferenceManager.getDefaultSharedPreferences(this);
        //头像
        if (!se.getString("photo", "").equals("")) {
            Glide.with(MyApplication.getInstance()).load(se.getString("photo", "")).into(ivHead);
        }
        mSession.setImge(se.getString("photo", ""));
        ((TextView) findViewById(R.id.tv_title)).setText("个人信息");
    }

    public void initEvents() {
        tvUserName.setText(se.getString("nickName",se.getString("phone", "----")));
        tvUserPhone.setText(se.getString("phone", "----"));
        tvUserTaobao.setText(se.getString("taobaoNumber", "未授权"));
        tvUserPay.setText(se.getString("alipayNumber", "绑定支付宝账号"));
    }

    @OnClick({R.id.iv_back, R.id.line_head, R.id.line_user, R.id.line_set_user, R.id.line_phone_number, R.id.ll_tb, R.id.line_alipay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.line_head:
                setCamear();
                File file1 = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));//文件夹是否存在
                if (!file1.exists()) {
                    file1.mkdir();
                }
                file = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + Constants.HEAD);

                DialogUtil.showDialog(this, new DialogUtil.onClickItem() {
                    @Override
                    public void clickItem(int item) {
                        switch (item) {
                            case 1://拍照
                                getPicture();
                                break;
                            case 2://相册
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(intent, 1);
                                break;
                            case 3://取消

                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case R.id.line_set_user://设置用户名
                startActivity(new Intent(mContext, ChangeUserNameActivity.class));
                break;
            case R.id.line_phone_number://身份校验  1: 更换手机号码   2 : 更绑支付宝
                startActivity(new Intent(mContext, CheckIdentityActivity.class).putExtra("type", 1));
                break;
            case R.id.line_alipay:  //更换支付宝帐号
               /* if (mSession.getAlipayNumber().equals("")) {//没绑定支付宝
                    startActivity(new Intent(mContext,BoundAlipayActivity.class));
                } else {
                    startActivity(new Intent(mContext, ChangeBoundAlipayActivity.class).putExtra("pay", se.getString("alipayNumber", "")));
                }*/
                startActivity(new Intent(mContext, CheckIdentityActivity.class));
                break;
            case R.id.ll_tb:  //绑定淘宝
                // TODO: 2017/9/25 三方淘宝的接口
                if (Utils.isNetworkAvailable(mContext)) {
                        final AlibcLogin alibcLogin = AlibcLogin.getInstance();
                        alibcLogin.showLogin(this,new AlibcLoginCallback() {
                            @Override
                            public void onSuccess() {
                                Session taobao = alibcLogin.getSession();
                                //mSession.setLogin(true);
                                //mSession.setImge(taobao.avatarUrl);
                                //mSession.setName(taobao.nick);
                                //mSession.setAccountNumber(taobao.nick);
                                updateInfoTaobao(taobao.nick);
                                //finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Utils.show(mContext,s);

                            }
                        });
                } else {
                    Utils.show(mContext,getString(R.string.net_error1));
                }

                break;
        }
    }

    /**
     * 支付宝更改
     * @param nick
     */
    private void updateInfoTaobao(final String nick) {
        Map<String,String> map=new HashMap<>();
        map.put("taobaoNumber",nick);
        Netword.getInstance().getApi(CommenApi.class)
                .updataInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<UpdataInfoBean>(mContext) {
                    @Override
                    public void successed(UpdataInfoBean result) {
                        se.edit().putString("taobaoNumber",nick).commit();
                        tvUserTaobao.setText(nick);
                    }
                });

    }

    /**
     * 更改头像
     * @param s
     */
    private void updateInfoPhoto(final String s) {
        Map<String,String> map=new HashMap<>();
        map.put("photo",s);
        Netword.getInstance().getApi(CommenApi.class)
                .updataInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<UpdataInfoBean>(mContext) {
                    @Override
                    public void successed(UpdataInfoBean result) {
                        String s = result.photo;
                        mSession.setImge(s);
                        //更新用户头像
                        se.edit().putString("photo", s).commit();
                    }
                });

    }

    //Android sdk6.0
    private static final int REQUEST_PERMISSION_CAMERA = 222;
    private static final int REQUEST_PERMISSION_STORAGE = 333;

    private void setCamear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // getPicture();
                } else {
                    // Permission Denied
                    Utils.show(mContext, "很遗憾你把相机权限禁用了!");
                    finish();
                }
                break;
            case REQUEST_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //getPicture();

                } else {
                    // Permission Denied
                    Utils.show(mContext, "很遗憾你把读取文件权限禁用了!");
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

/**
 *  @author li
 *  邮箱：961567115@qq.com
 *  @time 2017/9/25  17:49
 *  @describe 从照相机获取图片
 */
    private void getPicture() {
        //final String time = String.valueOf(System.currentTimeMillis())+".jpg";
        //判断内存卡是否存在
        String SDStart = Environment.getExternalStorageState();
        if (SDStart.equals(Environment.MEDIA_MOUNTED)) {
            File file1 = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name));//文件夹是否存在
            if (!file1.exists()) {
                file1.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + Constants.HEAD);

            //android 7.0
            //  Uri imageUri = FileProvider.getUriForFile(mContext, "com.lechuang.letaotao.fileProvider", file);
            //适配7.0和7.0一下的地址
            Uri imageUri = PhotoUtil.getUriForFile(mContext, file);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, 2);
        } else {
            Utils.show(mContext, "内存卡不存在");
        }
    }

    /**
     * 压缩图片
     *
     * @param data
     */
    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            String path = Uri.fromFile(file).getPath();
            bitmap = PhotoUtil.toSmall(bitmap, path);
            ivHead.setImageBitmap(bitmap);
        }
       upHeadPhoto();
    }

    /**
     * 上传头像
     */
    private void upHeadPhoto() {
        parts = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/" + Constants.HEAD);
        RequestBody requestFile = RequestBody
                .create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody
                .Part.createFormData("file", file.getName(), requestFile);
        parts.add(part);
        Netword.getInstance().getApi(TheSunApi.class)
                .fileUpload(parts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<UpFileBean>(mContext) {
                    @Override
                    public void successed(UpFileBean result) {
                        String imageId = result.imageId;
                        updateInfoPhoto(imageId);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode != -1) {
                    return;
                }
                Utils.resizeImage(data.getData(), this, 3);
                break;
            case 2:
                if (resultCode != -1) {
                    return;
                }
                if (PhotoUtil.isSdcardExisting()) {
                    Uri uriForFile = PhotoUtil.getUriForFile(mContext, file);
                    Utils.resizeImage(uriForFile, this, 3);
                } else {
                    Utils.show(mContext, "未找到存储卡，无法存储照片");
                }
                break;
            case 3:
                if (data != null) {
                    showResizeImage(data);
                }


                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
