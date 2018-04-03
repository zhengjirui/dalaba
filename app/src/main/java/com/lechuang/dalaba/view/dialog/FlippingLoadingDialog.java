package com.lechuang.dalaba.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.lechuang.dalaba.R;


public class FlippingLoadingDialog extends Dialog {
    private TextView textView;
    private String text;

    public FlippingLoadingDialog(Context context, String text) {
        super(context, R.style.FullScreenDialog);
        setContentView(R.layout.flipping_loading_dialog);
        this.text = text;

        init();

        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    private void init() {
        textView = (TextView) findViewById(R.id.tv_loading);
        textView.setText(text);
    }

    public void setText(String text) {
        this.text = text;

        textView.setText(text);
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
