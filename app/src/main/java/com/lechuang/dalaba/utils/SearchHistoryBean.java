package com.lechuang.dalaba.utils;

/**
 * Simple to Introduction
 *
 * @Description: [$desc]
 * @Author: [yrj]
 * @CreateDate: [2017/8/26 ]
 * @Version: [v1.0]
 */
public class SearchHistoryBean {
    public String history;
    public String Key;
    public SearchHistoryBean(String Key,String history){
        this.Key = Key;
        this.history = history;
    }
}
