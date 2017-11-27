package com.example.lawrence.popula;


import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Util {

public static void hideKeyboard(Activity activity) {
    if(activity.getCurrentFocus() != null) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0); }
}

}
