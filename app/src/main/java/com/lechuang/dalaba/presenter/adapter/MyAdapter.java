package com.lechuang.dalaba.presenter.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.model.bean.TipoffShowBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianren on 2017/11/3.
 */

public class MyAdapter extends RecyclerView.Adapter {
    private List<Integer> mHeights;
    private List<TipoffShowBean.ListBean> list;
    private RecycleViewListener recycleViewListener;

    public MyAdapter(List<TipoffShowBean.ListBean> list, RecycleViewListener recycleViewListener) {
        this.mHeights = new ArrayList<>();
        this.list = list;
        this.recycleViewListener = recycleViewListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tipoff_item, parent, false);
        View itemView2 = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.baoliao_header, parent, false);

        if (viewType == 0)
            return new MyViewHolder(itemView2, 0);
        else
            return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        if (position == 0) {
            StaggeredGridLayoutManager.LayoutParams clp = (StaggeredGridLayoutManager.LayoutParams) myViewHolder.getView().getLayoutParams();
            // 最最关键一步，设置当前view占满列数，这样就可以占据两列实现头部了
            clp.setFullSpan(true);
            ((MyViewHolder) holder).getView().setLayoutParams(clp);
            return;
        }
// 随机高度, 模拟瀑布效果.
        myViewHolder.setData(list.get(position).img, list.get(position).title, list.get(position).photo, list.get(position).nickName,
                list.get(position).pageViews + "", list.get(position).praiseCount + "");
        myViewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleViewListener.itemClik(list.get(position));
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void addData(List<TipoffShowBean.ListBean> list) {
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, userName, comment, dianzan;
        private ImageView img, photo;
        private View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            init(itemView);
        }

        public MyViewHolder(View itemView, int type) {
            super(itemView);
            this.view = itemView;
        }

        public void init(View view) {
            title = (TextView) view.findViewById(R.id.title_tipoff);
            userName = (TextView) view.findViewById(R.id.user_name);
            comment = (TextView) view.findViewById(R.id.tv_comment);
            dianzan = (TextView) view.findViewById(R.id.tv_dianzan);
            img = (ImageView) view.findViewById(R.id.iv_tipoff);
            photo = (ImageView) view.findViewById(R.id.iv_tipoff_photo);


        }

        public void setData(String img, String title, String photo, String userName, String comment, String dianzan) {
            this.title.setText(title);
            this.userName.setText(userName);
            this.comment.setText(comment);
            this.dianzan.setText(dianzan);
            Glide.with(MyApplication.getInstance()).load(img).into(this.img);
            Glide.with(MyApplication.getInstance()).load(photo).into(this.photo);
        }

        public View getView() {
            return this.view;
        }
    }

    public interface RecycleViewListener {
        void itemClik(TipoffShowBean.ListBean listBean);
    }
}

