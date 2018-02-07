package com.fekracomputers.islamiclibrary.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.utility.Util;

public class KeyboardAwareEditText extends AppCompatEditText {
    @Nullable
    final InputMethodManager mInputMethodManager = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
    private KeyboardListener mKeyboardListener;

    public  void setNumberEditorValid(boolean valid, CharSequence errorString) {
        if (valid) {
            this.setError(null);
            this.setTextColor(Util.getThemeColor(getContext(), R.attr.blueThemedText));
        } else {
            this.setError(errorString, null);
            this.setTextColor(Util.getThemeColor(getContext(), R.attr.skimPageError));
        }
    }

    public interface KeyboardListener {
        void onKeyboardDismissed(KeyboardAwareEditText keyboardAwareEditText);
    }

    public KeyboardAwareEditText(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        correctDiretion();
    }

    public void setKeyboardListener(KeyboardListener keyboardListener) {
        mKeyboardListener = keyboardListener;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mKeyboardListener == null || !mInputMethodManager.isActive(this) || keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyPreIme(keyCode, event);
        }
        mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        mKeyboardListener.onKeyboardDismissed(this);
        return true;
    }
    
    public void correctDiretion()
    {
        boolean numIsRtl=true;
        if (ViewCompat.getLayoutDirection(this) != View.LAYOUT_DIRECTION_RTL) {
            numIsRtl = false;
        }
        ViewCompat.setLayoutDirection(this, View.LAYOUT_DIRECTION_LTR);
        this.setGravity(numIsRtl ? Gravity.LEFT : Gravity.RIGHT);

    }
}
