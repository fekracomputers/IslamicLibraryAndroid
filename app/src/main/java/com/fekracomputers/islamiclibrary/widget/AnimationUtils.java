package com.fekracomputers.islamiclibrary.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 1/5/2017.
 */

public class AnimationUtils {
    private static final int BOOKMARK_ANIMATING_OVERSHOOT_TENSION = 5;

    public static void addBookmarkWithAnimation(final View bookmarkImage, Animator.AnimatorListener animatorListener) {
        bookmarkImage.setPivotY(0);
        ValueAnimator moveAnim = ObjectAnimator.ofFloat(bookmarkImage, View.SCALE_Y, 0, 1);
        moveAnim.setDuration(1000);
        moveAnim.setInterpolator(new OvershootInterpolator(BOOKMARK_ANIMATING_OVERSHOOT_TENSION));
        moveAnim.addListener(animatorListener);
        moveAnim.start();
    }

    public static void removeBookmarkWithAnimation(final View bookmarkImage, Animator.AnimatorListener animatorListener) {
        bookmarkImage.setPivotY(0);
        ValueAnimator moveAnim = ObjectAnimator.ofFloat(bookmarkImage, View.SCALE_Y, 1, 2, 0);
        moveAnim.setDuration(1000);
        moveAnim.setInterpolator(new DecelerateInterpolator());
        moveAnim.addListener(animatorListener);
        moveAnim.start();
    }

}
