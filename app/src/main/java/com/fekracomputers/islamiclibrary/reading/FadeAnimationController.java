package com.fekracomputers.islamiclibrary.reading;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;

public class FadeAnimationController {
    private Animator mAnimation;
    private final int mInvisibleValue;
    private final View mView;
    private int mVisibility;
    private final float mVisibleAlpha;

    public interface OnVisibilityChangedListener {
        void onVisibilityChangeBegin();

        void onVisibilityChangeEnd();
    }

    public static Animator createAnimator(View view, float toAlpha, int durationMillis, int startDelayMillis, AnimatorListener listener) {
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), toAlpha);
        animator.setDuration((long) durationMillis);
        animator.setStartDelay((long) startDelayMillis);
        animator.addListener(listener);
        return animator;
    }

    public FadeAnimationController(View view, int invisibleValue, float visibleAlpha) {
        this.mView = view;
        this.mVisibility = view.getVisibility();
        this.mInvisibleValue = invisibleValue;
        this.mVisibleAlpha = visibleAlpha;
    }

    public FadeAnimationController(View view, int invisibleValue) {
        this(view, invisibleValue, 1.0f);
    }

    public FadeAnimationController(View view) {
        this(view, View.GONE);
    }

    public boolean getVisible() {
        return this.mVisibility == View.VISIBLE;
    }

    public void setVisibilityNoAnim(int visibility) {
        if (visibility != this.mView.getVisibility() || this.mAnimation != null) {
            if (this.mAnimation != null) {
                this.mAnimation.cancel();
                this.mAnimation = null;
            }
            this.mVisibility = visibility;
            setViewVisibility(visibility);
            this.mView.setAlpha(visibility == View.VISIBLE ? this.mVisibleAlpha : 0.0f);
        }
    }

    public void setVisibility(int visibility) {
        setVisibility(visibility, 300, null);
    }

    public void setVisibility(int visibility, int durationMillis, OnVisibilityChangedListener listener) {
        setVisibility(visibility, durationMillis, 0, listener);
    }

    public void setVisibility(final int visibility, int durationMillis, int startDelayMillis, final OnVisibilityChangedListener listener) {
        if (visibility != this.mVisibility) {
            this.mVisibility = visibility;
            final boolean toVisible = visibility == View.VISIBLE;
            if (toVisible) {
                setViewVisibility(View.INVISIBLE);
            }
            float toAlpha = toVisible ? this.mVisibleAlpha : 0.0f;
            AnimatorListener animatorListener = new AnimatorListener() {
                public void onAnimationEnd(Animator animator) {
                    FadeAnimationController.this.mAnimation = null;
                    if (!toVisible) {
                        FadeAnimationController.this.setViewVisibility(visibility);
                    }
                    if (listener != null) {
                        listener.onVisibilityChangeEnd();
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }

                public void onAnimationStart(Animator animator) {
                    if (animator.isRunning()) {
                        if (toVisible) {
                            FadeAnimationController.this.setViewVisibility(View.VISIBLE);
                        }
                        if (listener != null) {
                            listener.onVisibilityChangeBegin();
                        }
                    }
                }
            };
            if (this.mAnimation != null) {
                this.mAnimation.cancel();
            }
            this.mAnimation = createAnimator(this.mView, toAlpha, durationMillis, startDelayMillis, animatorListener);
            this.mAnimation.start();
        } else if (Log.isLoggable("FadeAnimController", Log.DEBUG)) {
            Log.d("FadeAnimController", "setVisibility() was a no-op because it is already " + visibility);
        }
    }

    public void setVisibleNoAnim(boolean visible) {
        setVisibilityNoAnim(visible ? View.VISIBLE : this.mInvisibleValue);
    }

    public void setVisible(boolean visible) {
        setVisible(visible, 300, null);
    }

    public void setVisible(boolean visible, OnVisibilityChangedListener listener) {
        setVisible(visible, 300, listener);
    }

    public void setVisible(boolean visible, int durationMillis, OnVisibilityChangedListener listener) {
        setVisible(visible, durationMillis, View.VISIBLE, listener);
    }

    public void setVisible(boolean visible, int durationMillis, int startDelayMillis, OnVisibilityChangedListener listener) {
        setVisibility(visible ? View.VISIBLE : this.mInvisibleValue, durationMillis, startDelayMillis, listener);
    }

    protected void setViewVisibility(int viewVisibility) {
        this.mView.setVisibility(viewVisibility);
    }
}
