package com.lechuang.dalaba.view.activity.recyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.GetBean;
import com.lechuang.dalaba.view.activity.get.OnKeyShareCallback;
import com.lechuang.dalaba.view.defineView.SpannelTextView;

/**
 * Author: guoning
 * Date: 2017/10/6
 * Description:  赚列表页面的Adapter
 */

public class GetInfoAdapter extends BaseQuickAdapter<GetBean.ListInfo, GetInfoHolder> {


    private OnKeyShareCallback callback;
    private Context context;

    public GetInfoAdapter(Context context) {
        super(R.layout.item_get);
        this.context = context;
    }

    @Override
    protected void convert(final GetInfoHolder helper, final GetBean.ListInfo listInfo) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Glide.with(context).load(listInfo.imgs).into((ImageView) helper.getView(R.id.img));
        Glide.get(context).setMemoryCategory(MemoryCategory.LOW);

        helper.setText(R.id.price, listInfo.price);
        ((TextView) helper.getView(R.id.price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        helper.setText(R.id.preferentialPrice, "¥ " + listInfo.preferentialPrice);
        helper.setText(R.id.nowNumber, "已售出 " + listInfo.nowNumber + " 件");
        helper.setText(R.id.couponMoney, listInfo.couponMoney + "元");
        //是不是代理都显示赚
        helper.setText(R.id.zhuanMoney, listInfo.zhuanMoney);
        SpannelTextView productName = helper.getView(R.id.spannelTextView);
        productName.setShopType(listInfo.shopType == null ? 1 : Integer.parseInt(listInfo.shopType));
        productName.setDrawText(listInfo.productName);
        final ImageView share = helper.getView(R.id.share);
        final LinearLayout ll_share = helper.getView(R.id.ll_share);

        final int adapterPosition = helper.getAdapterPosition();
        ll_share.setTag(adapterPosition);
//        final CommFragment.InfoHolder tempHolder = holder;
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) ll_share.getTag();
                if (pos != adapterPosition) return;
                if (callback != null) callback.show(listInfo, adapterPosition);
            }
        });

    }


    public void setOnKeyShareCallback(OnKeyShareCallback callback) {
        this.callback = callback;
    }

}
