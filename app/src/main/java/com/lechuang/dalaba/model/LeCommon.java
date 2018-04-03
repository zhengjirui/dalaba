package com.lechuang.dalaba.model;

import com.alibaba.mobileim.gingko.plugin.action.WEIXIN;

/**
 * Author: guoning
 * Date: 2017/10/8
 * Description: 保存key
 */

public class LeCommon {
    public static final String KEY_AGENCY_STATUS = "isAgencyStatus";//是否是代理 1 代理  0 不是
    public static final String KEY_HAS_LOGIN = "isLogin";//是否登陆

    public static final String KEY_PAY_PRICE = "payPriceStr";
    public static final java.lang.String KEY_ROOT_ID = "rootId";
    public static final String ACTION_LOGIN_SUCCESS = "login_success";//登陆成功
    public static final String ACTION_LOGIN_OUT = "login_out"; // 退出登陆
    public static final String ACTION_APPLY_AGENT_SUCCESS = "agent_success";//申请代理成功
    public static boolean isTop = true; // SwipeLayout 是否可点击
    public static final String WEIXIN = "isPass"; // SwipeLayout 是否可点击
}
