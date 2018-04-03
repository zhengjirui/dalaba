package cn.sharesdk.onekeyshare;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lechuang.share.R;

import java.io.File;

import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * @author: LGH
 * @since: 2018/3/16
 * @describe:
 */

public class ShowShareDialog extends Dialog implements View.OnClickListener  {

    private Context mContent;
    private File[] mImgUrlFiles;

    public ShowShareDialog(Context context, File[] imgUrlFiles) {
        super(context, R.style.BottomDialogStyle);
        setContentView(R.layout.show_share_dialog);
        this.mContent = context;
        this.mImgUrlFiles = imgUrlFiles;
        init();

        //setCancelable(true);
        //设置该属性之后点击dialog之外的地方不消失
        //setCanceledOnTouchOutside(false);
    }

    private void init() {

        findViewById(R.id.share_qq_kongjian).setOnClickListener(this);
        findViewById(R.id.share_qq).setOnClickListener(this);
        findViewById(R.id.share_weixin).setOnClickListener(this);
        findViewById(R.id.share_friends).setOnClickListener(this);
        findViewById(R.id.share_sinawbo).setOnClickListener(this);

        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.share_qq_kongjian) {
            ShareUtils.shareToQQZ(mContent,mImgUrlFiles);
        } else if (id == R.id.share_qq) {
            ShareUtils.shareToQQ(mContent,mImgUrlFiles);
        } else if (id == R.id.share_weixin) {
            ShareUtils.shareToWChart(mContent,mImgUrlFiles);
        } else if (id == R.id.share_friends) {
            ShareUtils.shareToWChartFs(mContent,mImgUrlFiles);
        } else if(id == R.id.share_sinawbo){
            ShareUtils.shareToSinaWeiBo(mContent,mImgUrlFiles);
        }

    }

}
