package com.wollenstein.derek.amazingrecipeapp.recipes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for managing Recipe DB actions
 */
public class RecipeDBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DATABASE_NAME = "CoolRecipe.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String CREATE_RECIPE_INGREDIENT =
        "CREATE TABLE IF NOT EXISTS " + RecipeContract.RecipeIngredient.TABLE_NAME + " (" +
            RecipeContract.RecipeIngredient._ID + " INTEGER PRIMARY KEY," +
            RecipeContract.RecipeIngredient.COLUMN_NAME_RECIPE_ID + INT_TYPE + COMMA_SEP +
            RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_NAME + TEXT_TYPE + COMMA_SEP +
            RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_QUANTITY + TEXT_TYPE + COMMA_SEP +
            RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_UNIT_TYPE + INT_TYPE +
        " )";
    private static final String CREATE_RECIPE_STEP =
        "CREATE TABLE IF NOT EXISTS " + RecipeContract.RecipeStep.TABLE_NAME + " (" +
            RecipeContract.RecipeStep._ID + " INTEGER PRIMARY KEY," +
            RecipeContract.RecipeStep.COLUMN_NAME_RECIPE_ID + INT_TYPE + COMMA_SEP +
            RecipeContract.RecipeStep.COLUMN_NAME_STEP_TYPE + INT_TYPE + COMMA_SEP +
            RecipeContract.RecipeStep.COLUMN_NAME_STEP_DATA + TEXT_TYPE +
        " )";
    private static final String CREATE_RECIPE =
        "CREATE TABLE IF NOT EXISTS " + RecipeContract.Recipe.TABLE_NAME + " (" +
            RecipeContract.Recipe._ID + " INTEGER PRIMARY KEY," +
            RecipeContract.Recipe.COLUMN_NAME_TITLE + TEXT_TYPE +
        " )";

    public RecipeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPE_STEP);
        db.execSQL(CREATE_RECIPE_INGREDIENT);
        db.execSQL(CREATE_RECIPE);
    }

    // This is the first version of this db, so any upgrades constitute a grave error.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("recipe", "Error, I have no idea how to ugprade from "
                + oldVersion + " to " + newVersion);
        Log.e("recipe", "Trying to just create the db and seeing what happens");
        onCreate(db);
    }

    // Database Accessor methods

    public List<String> readRecipes(SQLiteDatabase database) {
        Cursor recipeCursor = database.query(
                RecipeContract.Recipe.TABLE_NAME,
                new String[] {RecipeContract.Recipe.COLUMN_NAME_TITLE},
                null, null, null, null, null);
        List<String> recipeNames = new ArrayList<>();
        if (!recipeCursor.moveToFirst()) {
            return recipeNames;
        }
        while (!recipeCursor.isAfterLast()) {
            recipeNames.add(recipeCursor.getString(0));
        }
        return recipeNames;
    }
}
