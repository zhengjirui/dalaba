package com.lechuang.dalaba.view.defineView;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.MyApplication;
import com.lechuang.dalaba.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProductMap extends FrameLayout implements OnPageChangeListener {
    protected Context mContext;
    /**
     * 装点点的ImageView数组
     */
    ViewGroup group;
    private ViewPager advPager = null;
    TextView time;
    protected List<String> mArrayList;
    private ImageView[] imageViews = null;

    public ProductMap(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.product_adv_map, this);
        init(context);

    }

    public ProductMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.product_adv_map, this);
        init(context);
    }

    public ViewPager getPagerView() {
        return advPager;

    }

    private void init(Context context) {
        mContext = context;
        advPager = (ViewPager) findViewById(R.id.adv_pager);
        ViewGroup.LayoutParams params = advPager.getLayoutParams();
        params.height = Utils.getScreenWidth(mContext);
        advPager.setLayoutParams(params);
        time = (TextView) findViewById(R.id.time);
        group = (ViewGroup) findViewById(R.id.viewGroup);
        advPager.addOnPageChangeListener(this);
    }

    public void setText(String text) {
        time.setText(text);
    }

    public void setTextHide() {
        time.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrollStateChanged(int position) {

    }

    private Runnable mTicker;
    private Handler mHandler;
    /**
     * 运行启动自动翻页
     **/
    private boolean Automaticsliding = false;
    int nowpage;
    /**
     * 是否正在执行自动翻页
     **/
    private boolean running = false;

    public void setAutomaticsliding(boolean b) {
        if (b) {

            if (!Automaticsliding) {
                Automaticsliding = true;
                if (!isRunning()) {
                    OnRun();
                    mHandler = new Handler();
                    mTicker = new Runnable() {
                        public void run() {
                            setNextItem();
                            if (Automaticsliding) {
                                mHandler.postDelayed(mTicker, 4000);
                            } else {
                                OnStop();
                            }
                        }
                    };
                    mHandler.postDelayed(mTicker, 4000);
                } else {
                    /* 如果还在运行，则不重新开启 */
//					Log.e("q", "无改变");
                }
            } else {
				/* 如果已经自动翻页，则不再开启自动翻页 */
            }
        } else {
            Automaticsliding = false;
        }

    }

    private void OnRun() {
//		Log.e("q", "开始运行");
        running = true;
    }

    private void OnStop() {
//		Log.e("q", "停止运行");
        running = false;

    }

    public boolean isRunning() {
        return running;
    }

    /**
     * 向下翻一页，如果为空或者长度为1，则不支持自动翻页，关闭自动翻页
     */
    public void setNextItem() {
        if (mArrayList == null) {
            Automaticsliding = false;
            return;
        }
        if (mArrayList.size() <= 1) {
            Automaticsliding = false;
            return;
        }
        if (nowpage != advPager.getCurrentItem()) {
            nowpage = advPager.getCurrentItem();
            return;
        }
        nowpage++;
        if (nowpage > mArrayList.size() - 1) {
            nowpage = 0;
        }
        advPager.setCurrentItem(nowpage, true);
    }

    public void setCurrentItem(int position) {
        advPager.setCurrentItem(position);
    }

    public int getCurrentItem() {
        return advPager.getCurrentItem();

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mChangeListener != null)
            mChangeListener.onPageSelected(position);
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[position]
                    .setBackgroundResource(R.drawable.adv_focus_radius1);
            if (position != i) {
                imageViews[i].setBackgroundResource(R.drawable.adv_focus_radius);
            }
        }
    }

    public int getCount() {
        return imageViews.length;

    }

    public void setAdapter(List<String> arrayList) {
        imageViews = new ImageView[arrayList.size()];
        group.removeAllViews();
        if (arrayList.size() > 1) {
            for (int i = 0; i < arrayList.size(); i++) {
                imageViews[i] = new ImageView(mContext);
                imageViews[i].setScaleType(ScaleType.FIT_XY);

                if (i == 0) {
                    // 默认选中第一张图片
                    imageViews[i]
                            .setBackgroundResource(R.drawable.adv_focus_radius1);

                } else {
                    imageViews[i].setBackgroundResource(R.drawable.adv_focus_radius);
                }
                LinearLayout.LayoutParams wmParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                wmParams.setMargins(8, 0, 8, 5);
                wmParams.width = 15;
                wmParams.height = 15;
                group.addView(imageViews[i], wmParams);
            }
        }
        AdvAdapter adapter = new AdvAdapter(mContext, arrayList);
        mArrayList = arrayList;
        advPager.setAdapter(adapter);
    }

    private class AdvAdapter extends PagerAdapter {
        private List<String> mArrayList = null;
        private Context mContext;

        public AdvAdapter(Context context, List<String> list) {
            mContext = context;
            if (list != null) {
                mArrayList = list;
            } else {
                mArrayList = new ArrayList<String>();
            }

        }

        @Override
        public int getCount() {
            return mArrayList.size();

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.view_pager_banner, null);
            ImageView imageView = (ImageView) v.findViewById(R.id.iv_banner);

            //ImageView imageView = new ImageView(mContext);
            //imageView.setScaleType(ScaleType.CENTER_INSIDE);
            //修改图片加载的框架
            //ImageLoader.getInstance().displayImage(mArrayList.get(position), imageView);
            Glide.with(MyApplication.getInstance()).load(mArrayList.get(position)).into(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnScrollListener != null) {
                        mOnScrollListener.onItemClick(position,
                                mArrayList);
                    }
                }
            });
            //((ViewPager) container).addView(imageView, 0);
            ((ViewPager) container).addView(v, 0);

            //return imageView;
            return v;
        }

        /*@Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //((ViewPager) container).removeView((ImageView) object);
        }*/
        /**
         * 销毁position位置的界面
         */
        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ((ViewPager) view).removeView(view);
        }
    }

    OnItemClickListener mOnScrollListener;
    OnPageChangeListener mChangeListener;

    public interface OnItemClickListener {
        public void onItemClick(int position, Object object);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnScrollListener = l;
    }

    public interface OnPageChangeListener {
        public void onPageSelected(int position);
    }

    public void setOnPageChangeListener(OnPageChangeListener l) {
        mChangeListener = l;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Automaticsliding = false;
    }
}
