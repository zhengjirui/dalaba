package com.lechuang.dalaba.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.base.BaseActivity;
import com.lechuang.dalaba.base.Constants;
import com.lechuang.dalaba.presenter.CommonAdapter;
import com.lechuang.dalaba.utils.SearchHistoryBean;
import com.lechuang.dalaba.utils.SearchHistoryUtils;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.defineView.ViewHolder;

import java.util.ArrayList;

/**
 * @author yrj
 * @date 2017/9/29
 * @E-mail 1422947831@qq.com
 * @desc 搜索界面
 */
public class SearchActivity extends BaseActivity {


    private EditText etProduct;  //搜索框
    private TextView tvSearch;   //搜索
    private ListView lvHistory;  //搜索历史
    private TextView tvHistory;  //删除历史
    //保存搜索历史的sp
    private SharedPreferences sp;
    private Context mContext = SearchActivity.this;
    //搜索历史
    private SearchHistoryUtils historyUtils;
    private CommonAdapter<SearchHistoryBean> mAdapter;
    private ArrayList<SearchHistoryBean> list;
    private Intent intent;
    private int whereSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search);
        //MODE_APPEND会一直叠加数据 MODE_PRIVATE存一个
        sp = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        //最大数量10条
        historyUtils = new SearchHistoryUtils(mContext, 10, sp);
        //initState();
        initEvent();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        etProduct = (EditText) findViewById(R.id.et_product);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        lvHistory = (ListView) findViewById(R.id.lv_history);
        tvHistory = (TextView) findViewById(R.id.tv_history);
    }

    @Override
    protected void initData() {

    }


    private void initEvent() {
        intent = new Intent(mContext, SearchResultActivity.class);
        whereSearch = getIntent().getIntExtra("whereSearch",1);
        // if(list!=null)
        findViewById(R.id.tv_search).setOnClickListener(this);
        findViewById(R.id.tv_history).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        list = historyUtils.sortHistory();
        if (list != null && list.size() > 0) {
            tvHistory.setVisibility(View.VISIBLE);
        }

        mAdapter = new CommonAdapter<SearchHistoryBean>(mContext, list, R.layout.item_search_history) {
            @Override
            public void setData(ViewHolder viewHolder, Object item) {
                viewHolder.setText(R.id.tv_history, ((SearchHistoryBean) item).history);
            }
        };
        lvHistory.setAdapter(mAdapter);
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etProduct.setText(list.get(position).history);
                search(list.get(position).history);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:   //搜索
                //搜索内容
                String search = etProduct.getText().toString().trim();
                if (search == null || search.isEmpty()) {
                    Utils.show(mContext, "搜索内容不能为空");
                    return;
                }
                //判断是否包含Emoji表情
                if (Utils.containsEmoji(search)) {
                    Utils.show(mContext, mContext.getResources().getString(R.string.no_emoji));
                    return;
                }
                historyUtils.save(search);
                tvHistory.setVisibility(View.VISIBLE);
                search(search);
                finish();
                break;
            case R.id.tv_history:  //清除历史
                //清除sp里面所有数据
                historyUtils.clear();
                //清除list数据
                list.clear();
                //刷新listview
                mAdapter.notifyDataSetChanged();
                tvHistory.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 开始搜索
     * @param search
     */
    private void search(String search) {
        if(whereSearch==0){
            intent=new Intent();
            intent.putExtra("content",etProduct.getText().toString().trim());
            this.setResult(1,intent);
        }else {
            //传递一个值,搜索结果页面用来判断是从分类还是搜索跳过去的 1:分类 2:搜索界面
            intent.putExtra("type", 2);
            //rootName传递过去显示在搜索框上
            intent.putExtra("rootName", search);
            //rootId传递过去入参
            intent.putExtra("rootId", search);
            startActivity(intent);

        }
    }
    /**
     * 沉浸式状态栏
     */
   /* private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }*/
}
