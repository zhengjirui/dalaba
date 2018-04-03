package com.lechuang.dalaba.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.view.defineView.DialogAlertView;


/**
 * Created by zhf on 2017/8/8.
 * 【对话框工具】
 */

public class DialogUtil {
    //【1是拍照，2是相册，3是取消】
    public interface onClickItem {
        void clickItem(int item);
    }

    //签到
    public interface onClickSign {
        int clickSign();
    }

    //【我要晒单-选择拍照或相册对话框】
    public static void showDialog(Context context, final onClickItem item) {
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        commonPart(context, dialog, Gravity.BOTTOM, R.layout.dialog_select_image);
        //拍照
        dialog.findViewById(R.id.liner_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                item.clickItem(1);
            }
        });
        //相册
        dialog.findViewById(R.id.liner_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                item.clickItem(2);
            }
        });
        //取消
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                item.clickItem(3);
            }
        });
        dialog.show();
    }

    //我要晒单，选择图片大于9张弹出对话框
    public static void showMySunDialog(Context context) {
        final DialogAlertView dialog = new DialogAlertView(context, R.style.CustomDialog);
        dialog.setView(R.layout.dialog_hint);
        dialog.show();
        ((TextView) dialog.findViewById(R.id.txt_notice)).setText("你最多只能选择9张图片");
        dialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 公共部分
     *
     * @param context
     * @param dialog      自己声明的Dialog
     * @param gravity     Dialog显示位置，基本上就两种情况：Gravity.CENTER or Gravity.BOTTOM
     * @param layoutResID Dialog的布局文件
     *
     */
    public static void commonPart(Context context, Dialog dialog, int gravity, int layoutResID) {
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(layoutResID);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        window.setGravity(gravity);
        window.setAttributes(params);

        // 防止dialog上方有条蓝色的线出现
        if (dialog.findViewById(context.getResources().getIdentifier("android:id/titleDivider", null, null)) != null) {
            dialog.findViewById(context.getResources().getIdentifier("android:id/titleDivider", null, null)).setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
