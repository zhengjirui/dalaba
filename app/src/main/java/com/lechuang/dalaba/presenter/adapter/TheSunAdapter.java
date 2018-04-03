package com.lechuang.dalaba.presenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.model.LocalSession;
import com.lechuang.dalaba.model.bean.SunShowBean;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.presenter.net.Netword;
import com.lechuang.dalaba.presenter.net.ResultBack;
import com.lechuang.dalaba.presenter.net.netApi.TipoffShowApi;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.SunBigPicActivity;
import com.lechuang.dalaba.view.defineView.MGridView;
import com.lechuang.dalaba.view.defineView.RatingBar;
import com.lechuang.dalaba.view.defineView.XCRoundImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by yrj on 2017/8/31.
 * 晒单Adapter
 */


public class TheSunAdapter extends BaseAdapter {

    protected List<SunShowBean.ListBean> theSunList;
    protected ArrayList<String> imgList;
    protected Context context;
    private Map<String, ArrayList<String>> map;

    private LocalSession mSession;

    public TheSunAdapter(List<SunShowBean.ListBean> mDatas, Context context, Map<String, ArrayList<String>> map) {
        this.theSunList = mDatas;
        this.context = context;
        this.map = map;
        mSession = LocalSession.get(context);
    }


    @Override
    public int getCount() {
        return theSunList == null ? 0 : theSunList.size();
    }

    @Override
    public Object getItem(int position) {
        return theSunList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.thesun_mlv_item, null);
            //发布图片gridView
            holder.mgv_img = (MGridView) convertView.findViewById(R.id.mgv_img);
            //头像
            holder.iv_headImg = (XCRoundImageView) convertView.findViewById(R.id.iv_headImg);
            //星级
            holder.ratingbarId = (RatingBar) convertView.findViewById(R.id.ratingbarId);
            //发布人
            holder.tv_issuer = (TextView) convertView.findViewById(R.id.tv_issuer);
            //点赞数量
            holder.tv_likeNum = (TextView) convertView.findViewById(R.id.tv_likeNum);
            //评论数量
            holder.tv_commentNum = (TextView) convertView.findViewById(R.id.tv_commentNum);
            //发布内容
            holder.tv_details = (TextView) convertView.findViewById(R.id.tv_details);
            //发布时间
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
           //点赞按钮
            holder.cb_praise = (ImageView) convertView.findViewById(R.id.cb_praise);
            /* //评论按钮
            holder.line_sun_comment = (LinearLayout) convertView.findViewById(R.id.line_sun_comment);
            //分享
            holder.line_sun_share = (LinearLayout) convertView.findViewById(R.id.ll_share);*/

            //记录点击的gridview处于父ListView的position
            holder.mgv_img.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ratingbarId.setClickable(false);
        //取List对应下标的数据
        final SunShowBean.ListBean sun = theSunList.get(position);
        //图片JSONArray
        List<SunShowBean.ListBean.Img1Bean> array= sun.img1;
        //获取点击的gridview处于父ListView的position
        //final int p = (int) holder.mgv_img.getTag();
        final int p1 = position;
        //存储图片数据的集合
        imgList = new ArrayList<>();
        //解析出图片地址添加到集合
        for (int i = 0; i < array.size(); i++) {
            SunShowBean.ListBean.Img1Bean img1Bean = array.get(i);
            imgList.add(img1Bean.imgUrl);
        }
        //map集合存储每一个图片集合  key = "theSunImgList" + position
        map.put("theSunImgList" + position, imgList);
        //头像
        // ImageLoader.getInstance().displayImage(sun.photo, holder.iv_headImg);
        Glide.with(MyApplication.getInstance()).load(sun.photo).into(holder.iv_headImg);
        //发布人
        holder.tv_issuer.setText(sun.nickName);
        //发布时间
        holder.tv_time.setText(sun.createTimeStr);
        //星级
        holder.ratingbarId.setStar(sun.starLevel);
        //评论内容
        holder.tv_details.setText(sun.content);
        //评论数量
        holder.tv_commentNum.setText(sun.appraiseCount + "");
        //点赞数量
        holder.tv_likeNum.setText(sun.praiseCount + "");
        //晒单界面暂不点赞
        //嵌套点赞
        if (sun.status == 1) {
            holder.cb_praise.setSelected(false);
        } else {
            holder.cb_praise.setSelected(true);
        }
        //holder.cb_praise.setChecked(true);.
        holder.cb_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb_praise.setClickable(false);
                if(sun.status==0){
                    sun.status=1;
                    sun.praiseCount= sun.praiseCount -1;
                    holder.tv_likeNum.setText(sun.praiseCount + "");
                 holder.cb_praise.setSelected(false);

                }else {
                    sun.status=0;
                    sun.praiseCount= sun.praiseCount + 1;
                    holder.tv_likeNum.setText(sun.praiseCount+ "");
                    holder.cb_praise.setSelected(true);
                }
                tipHelp(sun.id,2);
                holder.cb_praise.setClickable(true);
            }
        });
       /* holder.cb_praise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSession.isLogin()) {
                    if (Utils.isNetworkAvailable(context)) {
                        if (isChecked) {
                            holder.tv_likeNum.setText((sun.praiseCount + 1) + "");
                        } else {
                            holder.tv_likeNum.setText((Integer.valueOf(holder.tv_likeNum.getText().toString()) - 1) + "");
                        }
                        //index传0 表示给晒单或者爆料点赞  1表示给里面的评论点赞
                        tipHelp(theSunList.get(position).id, 2);
                    } else {
                        Toast.makeText(context, context.getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    holder.cb_praise.setChecked(false);
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });*/


        //评论
       /* holder.line_sun_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession.isLogin()) {
                    Intent intent = new Intent(context, TipOffAddCommentActivity.class);
                    intent.putExtra("tipId", theSunList.get(p).id);
                    intent.putExtra("type", 2);
                    context.startActivity(intent);
                } else {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });*/

        //gridView设置适配器
        holder.mgv_img.setAdapter(new CommonAdapter<String>(context, imgList, R.layout.thesun_gvitem) {
            @Override
            public void setData(com.lechuang.dalaba.view.defineView.ViewHolder viewHolder, Object item) {
                viewHolder.displayImage(R.id.iv_gv_img, (String) item);
            }
        });
        //item点击事件
        holder.mgv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(context, SunBigPicActivity.class);
                        intent.putExtra("current", position);
                        intent.putStringArrayListExtra("list", map.get("theSunImgList" + p1));
                        context.startActivity(intent);
            }
        });


       /* //分享
        holder.line_sun_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession.isLogin()) {
                    NativeShareDialog shareDialog = new NativeShareDialog(context);
                    shareDialog.setTitle(theSunList.get(p).nickName);
                    shareDialog.setBmUrl(map.get("theSunImgList" + p));
                    shareDialog.setDescription(theSunList.get(p).content);
                    shareDialog.setUrl(QUrl.theSunShare + "?t=1" + "&i=" + theSunList.get(p).id);
                    shareDialog.setPicUrl("");
                    shareDialog.noSaveBitmap();
                    shareDialog.show();
                } else {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });*/

        return convertView;
    }

    //点赞
    private void tipHelp(String tipId, int index) {
        Netword.getInstance().getApi(TipoffShowApi.class)
                .tipPraise(tipId, index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultBack<String>(context) {
                    @Override
                    public void successed(String result) {
                        Utils.show(context,result);
                    }
                });
    }


    private class ViewHolder {
        //头像
        private XCRoundImageView iv_headImg;
        //星级
        private RatingBar ratingbarId;
        //发布人,昵称
        private TextView tv_issuer;
        //发布时间
        private TextView tv_time;
        //发布内容
        private TextView tv_details;
        //发布图片GridView
        private MGridView mgv_img;
        //点赞数量
        private TextView tv_likeNum;
        //评论数量
        private TextView tv_commentNum;
        //点赞按钮
        private ImageView cb_praise;
        //评论
        private LinearLayout line_sun_comment;
        //分享
        private LinearLayout line_sun_share;
    }


}