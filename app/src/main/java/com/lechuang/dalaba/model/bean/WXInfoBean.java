package com.lechuang.dalaba.model.bean;

import java.util.List;

/**
 * @author: LGH
 * @since: 2018/3/30
 * @describe:
 */

public class WXInfoBean {


    /**
     * openid : olmt4wfxS24VeeVX16_zUhZezY
     * nickname : 李文星
     * sex : 1
     * language : zh_CN
     * city : Shenzhen
     * province : Guangdong
     * country : CN
     * headimgurl : http://wx.qlogo.cn/mmopen/ajNVdqHZLLDickRibe5D4x2ADgSfianmA4kK9hY4esrvGhmAFCe5wjox6b6pL4ibiblKnxibzVtGdqfa2UVHACfmmUsQ/0
     * privilege : []
     * unionid : o5aWQwAa7niCIXhAIRBOwglIJ7UQ
     */

    public String openid;
    public String nickname;
    public int sex;
    public String language;
    public String city;
    public String province;
    public String country;
    public String headimgurl;
    public String unionid;
    public List<?> privilege;

    public int errcode;
    public String errmsg;
}
