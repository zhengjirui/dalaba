package com.lechuang.dalaba.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;


import com.lechuang.dalaba.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhf on 2017/9/5.
 * 【版本更新】
 */

public class UpdateVersion {
    private Context context;
    private ProgressDialog pBar;
    private String describe;//版本号
    private String url;//下载文件地址

    public UpdateVersion(Context context) {
        this.context = context;
    }

    public void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("版本更新");
        builder.setMessage(describe);
        //builder.setCancelable(false);//设置弹出框不可退出

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    downFile(url);//在下面的代码段
                } else {
                    Toast.makeText(context, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private void downFile(final String downUrl) {
        pBar = new ProgressDialog(context);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setCancelable(false);//设置弹出框不可退出
        pBar.setProgress(0);
        pBar.show();
        new Thread() {
            public void run() {
                try {
                    String sdpath;
                    // 判断SD卡是否存在，并且是否具有读写权限
                    if (Environment.getExternalStorageState()
                            .equals(Environment.MEDIA_MOUNTED)) {
                        sdpath = Environment.getExternalStorageDirectory() + "/";
                    } else {
                        sdpath = Environment.getDataDirectory() + "/";
                    }
                    URL url = new URL(downUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5 * 1000);// 设置超时时间
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Charset", "GBK,utf-8;q=0.7,*;q=0.3");
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();
                    pBar.setMax(length);  //设置进度条的总长度
                    FileOutputStream fos = null;
                    if (is != null) {
                        File file = new File(sdpath, context.getResources().getString(R.string.app_name) + ".apk");
                        fos = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fos.write(buf, 0, ch);
                            process += ch;
                            pBar.setProgress(process); //这里就是关键的实时更新进度了！
                        }
                    }
                    fos.flush();
                    if (fos != null) {
                        fos.close();
                    }
                    down();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void down() {
        pBar.cancel();
        update();
    }

    private SharedPreferences sp;
    //安装文件，一般固定写法
    private void update() {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().commit();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.app_name) + ".apk")),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
