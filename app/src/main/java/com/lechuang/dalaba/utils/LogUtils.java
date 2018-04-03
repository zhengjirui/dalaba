package com.lechuang.dalaba.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 作者：li on 2017/9/23 14:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class LogUtils {
    public  static void toast(Context context,String string){
        Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
    }
}
