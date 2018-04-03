package com.lechuang.dalaba.view.activity.get;

import com.lechuang.dalaba.model.bean.GetBean;

/**
 * Author: guoning
 * Date: 2017/10/5
 * Description: 赚页面分享回调
 */

public interface OnKeyShareCallback {
    void show(GetBean.ListInfo listInfo, int position);
}
