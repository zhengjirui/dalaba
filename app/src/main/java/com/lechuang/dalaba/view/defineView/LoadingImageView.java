package com.lechuang.dalaba.view.defineView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class LoadingImageView extends android.support.v7.widget.AppCompatImageView {
    private RotateAnimation rotateAnimation;
    private boolean isHasAnimation;

    public LoadingImageView(Context context) {
        super(context);
    }

    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void setRotateAnimation() {
        if (isHasAnimation == false && getWidth() > 0 && getVisibility() == View.VISIBLE) {
            isHasAnimation = true;
            rotateAnimation = new RotateAnimation(0.0f, +360.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(2000L);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setRepeatCount(-1);
            rotateAnimation.setRepeatMode(Animation.RESTART);

            setAnimation(rotateAnimation);
        }
    }

    private void clearRotateAnimation() {
        if (isHasAnimation) {
            isHasAnimation = false;
            setAnimation(null);
            rotateAnimation = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setRotateAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        clearRotateAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0) {
            setRotateAnimation();
        }
    }

    public void startAnimation() {
        if (isHasAnimation) {
            super.startAnimation(rotateAnimation);
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == View.INVISIBLE || visibility == View.GONE) {
            clearRotateAnimation();
        } else {
            setRotateAnimation();
        }
    }
}
