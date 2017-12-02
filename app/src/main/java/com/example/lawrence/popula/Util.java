package com.example.lawrence.popula;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class Util {

public static void hideKeyboard(Activity activity) {
    if(activity.getCurrentFocus() != null) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0); }
}

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

public static float pixelsToDp(float dpValue, Context context) {
    float density = context.getResources().getDisplayMetrics().density;
    return dpValue * density;
}

    public static float dpToPixels(float pxValue, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return pxValue / density;
    }


}
