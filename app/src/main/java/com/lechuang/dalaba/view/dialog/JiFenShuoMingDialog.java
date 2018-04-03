package com.lechuang.dalaba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.lechuang.dalaba.R;

/**
 * @author yrj
 * @date 2017/10/9
 * @E-mail 1422947831@qq.com
 * @desc 版本更新的dialog
 */

public class JiFenShuoMingDialog extends Dialog {
    private Context mContext;

    public JiFenShuoMingDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        setContentView(R.layout.dialog_jifen_shuoming);
        this.mContext = context;

        //设置点击dialog之外的地方不消失，
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        findViewById(R.id.v_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
