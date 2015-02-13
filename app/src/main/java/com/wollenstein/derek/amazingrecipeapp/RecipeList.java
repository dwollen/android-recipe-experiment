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

import com.wollenstein.derek.amazingrecipeapp.recipes.RecipeManager;

public class RecipeList extends Activity implements RecipeFragment.OnFragmentInteractionListener {

    public static final String RECIPES_KEY = "recipes";
    public static final String TITLE_KEY = "title";
    private RecipeManager mRecipeManager;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mActionBar;
    private View mDrawerHeader;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_recipe_list);
        String[] recipeNames = getResources().getStringArray(R.array.cool_recipe_names);
        mRecipeManager = new RecipeManager(
                recipeNames, getResources().getStringArray(R.array.cool_recipes));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerHeader = getLayoutInflater().inflate(R.layout.drawer_list_header, null);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.app_name, R.string.app_name);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.addHeaderView(mDrawerHeader, "You are the very best", false);
        mDrawerList.setAdapter(new ArrayAdapter<String>(
                this, R.layout.drawer_list_item, R.id.drawer_item_text, recipeNames));
        mDrawerList.setOnItemClickListener(new DrawerItemOnClickListener());

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        if (saveInstanceState != null) {
            restoreState(saveInstanceState);
        } else {
            setTitle(getResources().getString(R.string.app_name));
        }

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
        String[] recipes = saveInstanceState.getStringArray("recipes");
        if (recipes != null) {
            for (int i = 0; i < recipes.length; i++) {
                mRecipeManager.updateRecipe(i, recipes[i]);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TITLE_KEY, getTitle());
        String[] recipes = new String[mRecipeManager.getRecipeCount()];
        for (int i = 0; i < mRecipeManager.getRecipeCount(); i++) {
            recipes[i] = mRecipeManager.getRecipe(i);
        }
        outState.putStringArray(RECIPES_KEY, recipes);
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
    public void onFragmentInteraction(int recipeNumber, String coolRecipe) {
        Log.d("recipe", "Somebody gave me this sweet recipe: " + coolRecipe);
        mRecipeManager.updateRecipe(recipeNumber, coolRecipe);
    }

    private class DrawerItemOnClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Subtract from position to account for the header views entries.
            selectItem(position - mDrawerList.getHeaderViewsCount());
        }
    }

    private void selectItem(int position) {
        Fragment fragment = RecipeFragment.newRecipeFragment(
                position, mRecipeManager.getRecipe(position));

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        // Look one up in recipe names to account for the header row.
        setTitle(mRecipeManager.getRecipeTitle(position));
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
