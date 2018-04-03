package com.lechuang.dalaba.view.activity.soufanli;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lechuang.dalaba.R;
import com.lechuang.dalaba.utils.Utils;
import com.lechuang.dalaba.view.activity.home.SearchResultActivity;
import com.lechuang.dalaba.view.defineView.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：li on 2017/9/21 17:46
 * 邮箱：961567115@qq.com
 * 修改备注:
 */
public class SouFanLiFragment extends Fragment {
    @BindView(R.id.et_content)
    ClearEditText etContent;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    Unbinder unbinder;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_suofanli, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        unbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
     /*  ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText("some thing");*/
        Editable text1 = etContent.getText();
        if (!TextUtils.isEmpty(text1)) {
            text1.clear();
        }
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence text = cm.getText();
        //String s = sp.getString("clipBoardText", "");
        //剪切板没有内容
        if (text == null) {
            return;
        }
        //如果是老的   不复制
        if (sp.getString("clipBoardText", "").equals(text.toString())) {
            return;
        }
        //剪切板设置到editText
        etContent.setText(text);
        sp.edit().putString("clipBoardText", text.toString()).commit();

    }

    @OnClick(R.id.tv_search)
    public void onViewClicked() {
        //搜索内容
        String search = etContent.getText().toString().trim();
        if (search == null || search.isEmpty()) {
            Utils.show(getActivity(), "搜索内容不能为空");
            return;
        }
        //判断是否包含Emoji表情
        if (Utils.containsEmoji(search)) {
            Utils.show(getActivity(), getActivity().getResources().getString(R.string.no_emoji));
            return;
        }

        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra("type", 2);
        //rootName传递过去显示在搜索框上
//        intent.putExtra("rootName", search);
        //rootId传递过去入参
        intent.putExtra("rootId", search);
        startActivity(intent);
    }
}
