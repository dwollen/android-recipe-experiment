package com.wollenstein.derek.amazingrecipeapp.recipes;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

/**
 * Facade for interacting with recipes.
 * This should probably become database backed eventually.
 * For now, we still have a fixed set of recipes, but they can be changed.
 */
public class RecipeManager {
    public static final String RECIPE = "recipe";
    private final RecipeDBHelper mDbHelper;
    public String[] mRecipeNames;
    public String[] mRecipes;

    public RecipeManager(String[] recipeNames, String[] recipes, Context context) {
        if (recipeNames == null) {
            Log.e("recipe", "Recieved empty recipe names");
            throw new NullPointerException("Empty Recipe Names");
        }
        if (recipes == null) {
            Log.e("recipe", "Recieved empty recipe names");
            throw new NullPointerException("Empty Recipes");
        }
        if (recipeNames.length != recipes.length) {
            Log.e("recipe", "Recipes and recipe names have different lengths");
            throw new IllegalArgumentException("Recipes and names have different lengths");
        }
        mRecipeNames = recipeNames;
        mRecipes = recipes;
        mDbHelper = new RecipeDBHelper(context);
        new ReloadRecipesTask(mDbHelper).execute();
    }

    public String getRecipeTitle(int n) {
        if (n >= mRecipeNames.length || n < 0) {
            Log.e("recipe", "Requested recipe " + n + " but length is " + mRecipeNames.length);
            throw new IllegalArgumentException("Requested recipe title " + n);
        }
        return mRecipeNames[n];
    }

    public String getRecipe(int n) {
        if (n >= mRecipes.length || n < 0) {
            Log.e(RECIPE, "Requested recipe " + n + " but length is " + mRecipes.length);
            throw new IllegalArgumentException("Requested recipe " + n);
        }
        return mRecipes[n];
    }

    public void updateRecipe(int n, String newRecipe) {
        Log.d(RECIPE, "Trying to set recipe " + n + " to " + newRecipe);
        if (n >= mRecipes.length || n < 0) {
            Log.e(RECIPE, "Trying to update recipe " + n + " but length is " + mRecipes.length);
            throw new IllegalArgumentException("Requested recipe " + n);
        }
        mRecipes[n] = newRecipe;
    }

    public int getRecipeCount() {
        return mRecipes.length;
    }

    private static final class ReloadRecipesTask extends AsyncTask<Void, Void, List<String>> {

        private RecipeDBHelper mDbHelper;

        public ReloadRecipesTask(RecipeDBHelper helper) {
            mDbHelper = helper;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try (SQLiteDatabase database = mDbHelper.getWritableDatabase()) {
                return mDbHelper.readRecipes(database);
            }
        }

        @Override
        protected void onPostExecute(List<String> recipes) {
            super.onPostExecute(recipes);
            for (String recipe : recipes) {
                Log.i("recipe", "Got a recipe: " + recipes);
            }
        }
    }
}
