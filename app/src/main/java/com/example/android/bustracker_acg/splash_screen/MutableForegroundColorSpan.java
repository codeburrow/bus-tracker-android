package com.example.android.bustracker_acg.splash_screen;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

/**
 * Created by giorgos on 3/11/2016.
 */

public class MutableForegroundColorSpan extends CharacterStyle
        implements UpdateAppearance {

    public static final String TAG = "MutableForegroundColorSpan";

    private int mColor;

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(mColor);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }
}
