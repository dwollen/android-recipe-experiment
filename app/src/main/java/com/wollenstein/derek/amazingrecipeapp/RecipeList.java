package com.wollenstein.derek.amazingrecipeapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecipeList extends Activity implements RecipeFragment.OnFragmentInteractionListener {

    private String[] mCoolRecipeNames;
    private String[] mCoolRecipes;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mActionBar;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        if (saveInstanceState != null) {
            restoreState(saveInstanceState);
        }

        setContentView(R.layout.activity_recipe_list);
        mCoolRecipeNames = getResources().getStringArray(R.array.cool_recipe_names);
        mCoolRecipes = getResources().getStringArray(R.array.cool_recipes);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBar = (Toolbar) findViewById(R.id.toolbar);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.app_name, R.string.app_name);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(
                this, R.layout.drawer_list_item, R.id.drawer_item_text, mCoolRecipeNames));
        mDrawerList.setOnItemClickListener(new DrawerItemOnClickListener());

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        setTitle(getResources().getString(R.string.app_name));
//
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mActionBar.setTitle(title);
    }

    private void restoreState(Bundle saveInstanceState) {
        CharSequence title = saveInstanceState.getCharSequence("title");
        if (title != null) {
            setTitle(title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("title", getTitle());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_recipe_list_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentInteraction(String coolRecipe) {
        Log.d("recipe", "Somebody gave me this sweet recipe: " + coolRecipe);
    }

    private class DrawerItemOnClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = RecipeFragment.newRecipeFragment(position, mCoolRecipes[position]);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mCoolRecipeNames[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
