package com.wollenstein.derek.amazingrecipeapp.uihelper;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wollenstein.derek.amazingrecipeapp.RecipeList;

/**
 * Wrapper for {@link android.support.v7.app.ActionBarDrawerToggle} which additionally hides
 * the keyboard when the drawer is opened.
 */
public class KeyboardHidingDrawerToggle extends ActionBarDrawerToggle {
    private final Context mContext;

    public KeyboardHidingDrawerToggle(
            Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes,
            int closeDrawerContentDescRes, Context context) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.mContext = context;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
    }
}
