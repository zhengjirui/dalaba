package com.lechuang.dalaba.model.bean;

/**
 * @author yrj
 * @date 2017/10/10
 * @E-mail 1422947831@qq.com
 * @desc 是否有最新的消息的实体类
 */

public class OwnNewsBean {
    /**
     * data : {"appUsers":{"alipayNumber":"12345","createTime":1505816434000,"createTimeStr":"2017-09-19 18:20:34","customerServiceId":"sfghvcfghh","id":"1k70l","isAgencyStatus":1,"nickName":"给你们地","openImPassword":"18c3becc7ee7b6f308f61041c72db18c","password":"1663321542db1723c39b742210078a68765e46f38f4bcce7e750facf30cc735c0b2bafd1795a4f5d","phone":"15538106078","photo":"http://img.taoyouji666.com/7A505BC8C904AA084CFA1508F7BA063C?imageView2/2/w/150/q/90","safeToken":"8A8493F2CD31894A4B2DE855FE97F2CC","signedStatus":0,"status":1,"taobaoNumber":"春暖静待花开","verifiCode":787792},"status":0}
     * error : Success
     * errorCode : 200
     */

    public AppUsersBean appUsers;

    // 0 不显示小红点  1显示
    public int status;

    public static class AppUsersBean {
        /**
         * alipayNumber : 12345
         * createTime : 1505816434000
         * createTimeStr : 2017-09-19 18:20:34
         * customerServiceId : sfghvcfghh
         * id : 1k70l
         * isAgencyStatus : 1
         * nickName : 给你们地
         * openImPassword : 18c3becc7ee7b6f308f61041c72db18c
         * password : 1663321542db1723c39b742210078a68765e46f38f4bcce7e750facf30cc735c0b2bafd1795a4f5d
         * phone : 15538106078
         * photo : http://img.taoyouji666.com/7A505BC8C904AA084CFA1508F7BA063C?imageView2/2/w/150/q/90
         * safeToken : 8A8493F2CD31894A4B2DE855FE97F2CC
         * signedStatus : 0
         * status : 1
         * taobaoNumber : 春暖静待花开
         * verifiCode : 787792
         */

        public String alipayNumber;
        public long createTime;
        public String createTimeStr;
        //客服 账号
        public String customerServiceId;
        public String id;
        public int isAgencyStatus;
        public String nickName;
        //openImPassword 密码
        public String openImPassword;
        public String password;
        //openIM账号
        public String phone;
        public String photo;
        public String safeToken;
        public int signedStatus;
        public int status;
        public String taobaoNumber;
        public int verifiCode;
    }

}
