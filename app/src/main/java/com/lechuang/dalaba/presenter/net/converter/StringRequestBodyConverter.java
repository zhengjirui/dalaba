package com.lechuang.dalaba.presenter.net.converter;


import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * 作者：尹松涛
 * 邮箱：yinsongtaoshmily@outlook.com
 * 日期：2017/9/26
 * 时间：10:50
 */

public class StringRequestBodyConverter implements Converter<String, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public RequestBody convert(String s) throws IOException {
        return RequestBody.create(MEDIA_TYPE, s);
    }
}
