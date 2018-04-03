package com.lechuang.dalaba.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Simple to Introduction
 *
 * @Description: [展示用户搜索历史的工具]
 * @Author: [yrj]
 * @CreateDate: [2017/8/26 ]
 * @Version: [v1.0]
 */
public class SearchHistoryUtils {

    private Context context;
    //最大展示条目
    private int HISTORY_MAX;
    private SharedPreferences sp;
    //sp的key生成时间,根据时间排序
    private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public SearchHistoryUtils(Context context, int historyMax, SharedPreferences sp) {
        this.context = context.getApplicationContext();
        this.HISTORY_MAX = historyMax;
        this.sp = sp;
    }

    //获取key,即生成时间字符串
    public String getKey() {
        return mFormat.format(new Date());
    }

    //把所有的搜索历史读并排序返回
    public ArrayList<SearchHistoryBean> sortHistory() {
        ArrayList<SearchHistoryBean> mResults = new ArrayList<>();
        //sp的getAll()返回所有的数据,返回Map<String, ?>
        Map<String, String> hisAll = (Map<String, String>) getAll();
        //将key排序升序
        Object[] keys = hisAll.keySet().toArray();
        Arrays.sort(keys);
        int keyLeng = keys.length;
        //这里计算 如果历史记录条数是大于 可以显示的最大条数，则用最大条数做循环条件，防止历史记录条数-最大条数为负值，数组越界
        int hisLeng = keyLeng > HISTORY_MAX ? HISTORY_MAX : keyLeng;
        for (int i = 1; i <= hisLeng; i++) {
            mResults.add(new SearchHistoryBean((String) keys[keyLeng - i], hisAll.get(keys[keyLeng - i])));
        }
        return mResults;
    }

    //获取所有sp存储的数据
    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    //保存的方法,传入value
    public void save(String value) {
        Map<String, String> historys = (Map<String, String>) getAll();
        for (Map.Entry<String, String> entry : historys.entrySet()) {
            if (value.equals(entry.getValue())) {
                remove(entry.getKey());
            }
        }
        sp.edit().putString(getKey(), value).commit();
    }

    //remove 掉key值对应的sp数据
    public void remove(String key) {
        sp.edit().remove(key).commit();
    }
    //清除所有的sp数据数据
    public void clear() {
        sp.edit().clear().commit();
    }


}
