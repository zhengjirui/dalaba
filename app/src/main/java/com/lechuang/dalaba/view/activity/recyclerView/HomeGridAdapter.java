package com.lechuang.dalaba.view.activity.recyclerView;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.model.bean.HomeLastProgramBean;
import com.lechuang.dalaba.view.defineView.SpannelTextView;
import com.lechuang.dalaba.view.defineView.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: guoning
 * Date: 2017/10/31
 * Description:
 */

public class HomeGridAdapter extends RecyclerView.Adapter<HomeGridAdapter.MyViewHolder> {

    private List<HomeLastProgramBean.ListBean> data;
    private Context mContext;
    private int isAgencyStatus;

    public HomeGridAdapter(List<HomeLastProgramBean.ListBean> productList, Context context, int code) {
        data = productList;
        mContext = context;
        isAgencyStatus = code;
    }

    @Override
    public HomeGridAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_last_program_grid, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(HomeGridAdapter.MyViewHolder holder, int position) {
        try {
            HomeLastProgramBean.ListBean bean = data.get(position);
            //商品图
            Glide.with(mContext).load(bean.imgs).into(holder.ivImg);
            //动态调整滑动时的内存占用
            Glide.get(mContext).setMemoryCategory(MemoryCategory.LOW);
            //原价
            holder.tvOldprice.setText(bean.price + "");
            //中划线
            holder.tvOldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            //券后价
            holder.tvNowprice.setText("¥" + bean.preferentialPrice);
            //商品名称
            SpannableString ss = new SpannableString(bean.name);
            ImageSpan imageSpan1;
            if ("1".equals(bean.shopType)) {
                imageSpan1 = new ImageSpan(mContext, R.drawable.zhuan_taobao);
            } else {
                imageSpan1 = new ImageSpan(mContext, R.drawable.zhuan_tianmao);
            }
            ss.setSpan(imageSpan1, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //商品名称
            holder.spannelTextView.setShopType(Integer.parseInt(bean.shopType));
            holder.spannelTextView.setDrawText(bean.name);
            //赚
            if (isAgencyStatus == 1) {
                holder.tvGet.setVisibility(View.VISIBLE);
            } else {
                holder.tvGet.setVisibility(View.GONE);
            }
//        holder.tvGet.setText("赚 ¥ " + bean.zhuanMoney);
            holder.tvGet.setVisibility(View.GONE);
            //销量
            holder.tvXiaoliang.setText("已抢" + bean.nowNumber + "件");
            if (bean.couponMoney.equals("0") || bean.couponMoney == null) {
                holder.ll_lingquan.setVisibility(View.GONE);
            } else {
                holder.ll_lingquan.setVisibility(View.VISIBLE);
                //领券减金额
                holder.tvQuanMoney.setText("券 ¥" + bean.couponMoney);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        holder.llLayout.setTag(position);
        holder.llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickLitener != null) {
                    //注意这里使用getTag方法获取数据
                    mOnItemClickLitener.onItemClick(v, (int) v.getTag());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_layout)
        LinearLayout llLayout;
        @BindView(R.id.iv_img)
        SquareImageView ivImg;
        @BindView(R.id.spannelTextView)
        SpannelTextView spannelTextView;
        @BindView(R.id.tv_oldprice)
        TextView tvOldprice;
        @BindView(R.id.tv_get)
        TextView tvGet;
        @BindView(R.id.tv_nowprice)
        TextView tvNowprice;
        @BindView(R.id.tv_xiaoliang)
        TextView tvXiaoliang;
        @BindView(R.id.tv_quanMoney)
        TextView tvQuanMoney;
        @BindView(R.id.ll_lingquan)
        LinearLayout ll_lingquan;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
