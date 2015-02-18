package com.wollenstein.derek.amazingrecipeapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wollenstein.derek.amazingrecipeapp.uihelper.KeyboardHidingDrawerToggle;

public class RecipeList extends Activity implements RecipeFragment.OnFragmentInteractionListener {

    public static final String TITLE_KEY = "title";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mActionBar;
    private View mDrawerHeader;
    private View mDrawerFooter;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_recipe_list);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerHeader = getLayoutInflater().inflate(R.layout.drawer_list_header, null);
        mDrawerFooter = getLayoutInflater().inflate(R.layout.drawer_list_footer, null);
        mActionBarDrawerToggle = new KeyboardHidingDrawerToggle(
                this, mDrawerLayout, R.string.app_name, R.string.app_name, getApplicationContext());

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.addHeaderView(mDrawerHeader, null, false);
        mDrawerList.addFooterView(mDrawerFooter, null, true);
        mDrawerList.setAdapter(new ArrayAdapter<String>(
                this, R.layout.drawer_list_item, R.id.drawer_item_text, new String[0]));
        mDrawerList.setOnItemClickListener(new DrawerItemOnClickListener());

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        final View addButton = findViewById(R.id.add_button);
        // TODO: Is this actually reasonable? Should I read sizes from the view?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addButton.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int diameter = view.getWidth();
//                int diameter = getResources().getDimensionPixelSize(R.dimen.round_button_diameter);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        outline.setOval(0, 0, diameter, diameter);
                    } else {
                        Log.e("recipe", "Executing a ViewOutlineProvider in a pre-L build");
                    }
                }
            });
            addButton.setClipToOutline(true);
        } else {
            // If we are in a pre-L version, then we need to manually setup elevation and animation.
            ViewCompat.setElevation(
                    addButton, getResources().getDimension(R.dimen.fab_elevation_low));
            float zTranslation =
                    getResources().getDimension(R.dimen.fab_elevation_high)
                    - getResources().getDimension(R.dimen.fab_elevation_low);
            final float xyTranslation = (float) Math.sqrt(zTranslation);
            final long fabAnimateDuration =
                    getResources().getInteger(R.integer.fab_animate_duration);
            addButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            addButton.animate()
                                    .translationX(xyTranslation)
                                    .translationY(xyTranslation)
                                    .setDuration(fabAnimateDuration);
                            break;
                        case MotionEvent.ACTION_UP:
                            addButton.animate()
                                    .translationX(0)
                                    .translationY(0)
                                    .setDuration(fabAnimateDuration);
                            break;
                    }
                    return false;
                }
            });
        }
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("recipe", "Somebody really thought clicking this would do something");
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, RecipeFragment.newRecipeFragment(0, "Unused Recipe"))
                        .commit();
            }
        });

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(TITLE_KEY, getTitle());
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
    }

    private class DrawerItemOnClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= mDrawerList.getCount() - mDrawerList.getFooterViewsCount()) {
                launchGithubLink(position);
            }
        }
    }

    private void launchGithubLink(int position) {
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
        Intent launchGithubIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getResources().getString(R.string.github_url)));
        startActivity(launchGithubIntent);
    }
}
