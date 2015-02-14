package com.wollenstein.derek.amazingrecipeapp.recipes;

import android.content.ContentValues;
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
    public static final String RECIPE = "recipe";

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

    public List<Recipe> readRecipes(SQLiteDatabase database) {
        Cursor recipeCursor = database.query(
                RecipeContract.Recipe.TABLE_NAME,
                new String[] {RecipeContract.Recipe._ID, RecipeContract.Recipe.COLUMN_NAME_TITLE},
                null, null, null, null, null);
        List<Recipe> recipes = new ArrayList<>();
        if (!recipeCursor.moveToFirst()) {
            return recipes;
        }
        while (!recipeCursor.isAfterLast()) {
            long recipeId = recipeCursor.getLong(0);
            String name = recipeCursor.getString(1);
            List<RecipeStep> steps = readRecipeSteps(database, recipeId);
            List<RecipeIngredient> ingredients = readRecipeIngredients(database, recipeId);
            recipes.add(new Recipe(recipeId, name, steps, ingredients));
        }
        return recipes;
    }

    /**
     * Wrapper for updates to a specific recipe.
     * TODO: Better handling for database update failures
     * TODO: Do not update fields which are unchanged.
     * TODO: Need a way to handle deleting extra steps / ingredients.
     */
    private static class RecipeUpdate {
        private final Recipe recipeUpdate;
        private final SQLiteDatabase database;

        public RecipeUpdate(Recipe recipeUpdate, SQLiteDatabase database) {
            if (recipeUpdate == null) {
                Log.e(RECIPE, "Cannot perform an update on a null recipe");
                throw new NullPointerException("recipeUpdate is null");
            }
            if (database == null) {
                Log.e(RECIPE, "Cannot perform an update on a null database");
                throw new NullPointerException("database is null");
            }
            this.recipeUpdate = recipeUpdate;
            this.database = database;
        }

        public Recipe upsert() {
            if (recipeUpdate.getId() == null) {
                return create();
            } else {
                return update(recipeUpdate.getId());
            }
        }

        private Recipe update(long recipeId) {
            ContentValues updated = new ContentValues();
            updated.put(RecipeContract.Recipe.COLUMN_NAME_TITLE, recipeUpdate.getName());
            database.update(
                    RecipeContract.Recipe.TABLE_NAME,
                    updated,
                    RecipeContract.Recipe._ID + " = ?",
                    new String[] { Long.toString(recipeId) });
            List<RecipeStep> finalSteps = new ArrayList<>();
            List<RecipeIngredient> finalIngredients = new ArrayList<>();
            for (RecipeStep step : recipeUpdate.getSteps()) {
                finalSteps.add(step.getId() == null
                        ? createStep(recipeId, step)
                        : updateStep(recipeId, step.getId(), step));
            }
            for (RecipeIngredient ingredient : recipeUpdate.getIngredients()) {
                finalIngredients.add(ingredient.getId() == null
                        ? createIngredient(recipeId, ingredient)
                        : updateIngredient(recipeId, ingredient.getId(), ingredient));
            }
            return new Recipe(recipeId, recipeUpdate.getName(), finalSteps, finalIngredients);
        }

        private Recipe create() {
            ContentValues inserted = new ContentValues();
            inserted.put(RecipeContract.Recipe.COLUMN_NAME_TITLE, recipeUpdate.getName());
            long recipeId = database.insert(RecipeContract.Recipe.TABLE_NAME, null, inserted);
            if (recipeId < 0) {
                Log.e("recipe", "Unable to save new recipe.");
                return recipeUpdate;
                // TODO: Maybe load some UI for this error?
            }
            List<RecipeStep> finalSteps = new ArrayList<>();
            List<RecipeIngredient> finalIngredients = new ArrayList<>();
            for (RecipeStep step : recipeUpdate.getSteps()) {
                finalSteps.add(createStep(recipeId, step));
            }
            for (RecipeIngredient ingredient : recipeUpdate.getIngredients()) {
                finalIngredients.add(createIngredient(recipeId, ingredient));
            }
            return new Recipe(recipeId, recipeUpdate.getName(), finalSteps, finalIngredients);
        }

        private RecipeStep createStep(long recipeId, RecipeStep sourceStep) {
            ContentValues inserted = new ContentValues();
            inserted.put(RecipeContract.RecipeStep.COLUMN_NAME_RECIPE_ID, recipeId);
            inserted.put(RecipeContract.RecipeStep.COLUMN_NAME_STEP_TYPE, sourceStep.getStepType());
            inserted.put(RecipeContract.RecipeStep.COLUMN_NAME_STEP_DATA, sourceStep.getStepData());
            long stepId = database.insert(RecipeContract.RecipeStep.TABLE_NAME, null, inserted);
            if (stepId < 0) {
                Log.e(RECIPE, "Unable to save new recipe step.");
                return sourceStep;
                // TODO: Maybe we should put in some UI for this type of error?
            }
            return new RecipeStep(stepId, sourceStep.getStepType(), sourceStep.getStepData());
        }

        private RecipeStep updateStep(long recipeId, long stepId, RecipeStep sourceStep) {
            ContentValues updated = new ContentValues();
            updated.put(RecipeContract.RecipeStep.COLUMN_NAME_STEP_TYPE, sourceStep.getStepType());
            updated.put(RecipeContract.RecipeStep.COLUMN_NAME_STEP_DATA, sourceStep.getStepData());
            int updates = database.update(
                    RecipeContract.RecipeStep.TABLE_NAME,
                    updated,
                    RecipeContract.RecipeStep._ID + " = ? AND "
                            + RecipeContract.RecipeStep.COLUMN_NAME_RECIPE_ID + " = ?",
                    new String[] { Long.toString(stepId), Long.toString(recipeId) });
            if (updates > 1) {
                Log.e(RECIPE, updates + " steps with the same id:" + recipeId + ", " + stepId);
            }
            return updates == 0 ? sourceStep
                    : new RecipeStep(stepId, sourceStep.getStepType(), sourceStep.getStepData());
        }

        private RecipeIngredient createIngredient(
                long recipeId, RecipeIngredient sourceIngredient) {
            ContentValues inserted = new ContentValues();
            inserted.put(RecipeContract.RecipeIngredient.COLUMN_NAME_RECIPE_ID, recipeId);
            inserted.put(
                    RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_NAME,
                    sourceIngredient.getName());
            inserted.put(
                    RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_QUANTITY,
                    sourceIngredient.getQuantity());
            inserted.put(
                    RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_UNIT_TYPE,
                    sourceIngredient.getQuantityType());
            long ingredientId = database.insert(
                    RecipeContract.RecipeIngredient.TABLE_NAME, null, inserted);
            if (ingredientId < 0) {
                Log.e(RECIPE, "Unable to save new recipe ingredient.");
                return sourceIngredient;
                // TODO: Maybe we should put in some UI for this type of error?
            }
            return new RecipeIngredient(
                    ingredientId,
                    sourceIngredient.getName(),
                    sourceIngredient.getQuantity(),
                    sourceIngredient.getQuantityType());
        }

        private RecipeIngredient updateIngredient(
                long recipeId, long ingredientId, RecipeIngredient sourceIngredient) {
            ContentValues updated = new ContentValues();
            updated.put(
                    RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_NAME,
                    sourceIngredient.getName());
            updated.put(
                    RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_QUANTITY,
                    sourceIngredient.getQuantity());
            updated.put(
                    RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_UNIT_TYPE,
                    sourceIngredient.getQuantityType());
            int updates = database.update(
                    RecipeContract.RecipeIngredient.TABLE_NAME,
                    updated,
                    RecipeContract.RecipeIngredient._ID + " = ? AND "
                            + RecipeContract.RecipeIngredient.COLUMN_NAME_RECIPE_ID + " = ?",
                    new String[] { Long.toString(ingredientId), Long.toString(recipeId) });
            if (updates > 1) {
                Log.e(RECIPE,
                        updates + " ingredients with the same id:" + recipeId + ", "
                                + ingredientId);
            }
            return updates == 0 ? sourceIngredient : new RecipeIngredient(
                    ingredientId,
                    sourceIngredient.getName(),
                    sourceIngredient.getQuantity(),
                    sourceIngredient.getQuantityType());
        }
    }

    private List<RecipeStep> readRecipeSteps(SQLiteDatabase database, long recipeId) {
        Cursor recipeStepCursor = database.query(
                RecipeContract.RecipeStep.TABLE_NAME,
                new String[] {
                        RecipeContract.RecipeStep._ID,
                        RecipeContract.RecipeStep.COLUMN_NAME_STEP_TYPE,
                        RecipeContract.RecipeStep.COLUMN_NAME_STEP_DATA},
                RecipeContract.RecipeStep.COLUMN_NAME_RECIPE_ID + " = ?",
                new String[] { Long.toString(recipeId) }, null, null, null);
        List<RecipeStep> recipeSteps = new ArrayList<>();
        if (!recipeStepCursor.moveToFirst()) {
            return recipeSteps;
        }
        while (!recipeStepCursor.isAfterLast()) {
            long stepId = recipeStepCursor.getLong(0);
            int stepType = recipeStepCursor.getInt(1);
            String stepData = recipeStepCursor.getString(2);
            recipeSteps.add(new RecipeStep(stepId, stepType, stepData));
        }
        return recipeSteps;
    }

    private List<RecipeIngredient> readRecipeIngredients(SQLiteDatabase database, long recipeId) {
        Cursor recipeIngredientCursor = database.query(
                RecipeContract.RecipeIngredient.TABLE_NAME,
                new String[] {
                        RecipeContract.RecipeIngredient._ID,
                        RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_NAME,
                        RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_QUANTITY,
                        RecipeContract.RecipeIngredient.COLUMN_NAME_INGREDIENT_UNIT_TYPE},
                RecipeContract.RecipeStep.COLUMN_NAME_RECIPE_ID + " = ?",
                new String[] { Long.toString(recipeId)}, null, null, null);
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        if (!recipeIngredientCursor.moveToFirst()) {
            return recipeIngredients;
        }
        while (!recipeIngredientCursor.isAfterLast()) {
            long ingredientId = recipeIngredientCursor.getLong(0);
            String ingredientName = recipeIngredientCursor.getString(1);
            int ingredientQuantity = recipeIngredientCursor.getInt(2);
            int ingredientUnitType = recipeIngredientCursor.getInt(3);
            recipeIngredients.add(new RecipeIngredient(
                    ingredientId, ingredientName, ingredientQuantity, ingredientUnitType));
        }
        return recipeIngredients;
    }
}
