package com.wollenstein.derek.amazingrecipeapp.recipes;

import android.provider.BaseColumns;

/**
 * Describes the requirements for a recipe in our recipes database
 */
public final class RecipeContract {
    private RecipeContract() {
        // Uninstantiable
    }

    // Step Type Constants
    // These are used to make it easier to do things like set a timer, etc.
    public static final int STEP_TYPE_PREPARE = 0;
    public static final int STEP_TYPE_WAIT = 1;
    public static final int STEP_TYPE_HEAT = 2;

    // Unit type constraints
    public static final int UNIT_TYPE_COUNT = 0;
    public static final int UNIT_TYPE_GRAM = 1;
    public static final int UNIT_TYPE_ML = 2;

    // Described overall per recipe properties
    public static abstract class Recipe implements BaseColumns {
        public static final String TABLE_NAME = "recipe";
        public static final String COLUMN_NAME_TITLE = "title";
    }

    public static abstract class RecipeIngredient implements BaseColumns {
        public static final String TABLE_NAME = "recipe_ingredient";
        public static final String COLUMN_NAME_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NAME_INGREDIENT_NAME = "ingredient_name";
        public static final String COLUMN_NAME_INGREDIENT_QUANTITY = "ingredient_quantity";
        public static final String COLUMN_NAME_INGREDIENT_UNIT_TYPE = "ingredient_unit_type";
    }

    // Describes a particular step of a recipe
    public static abstract class RecipeStep implements BaseColumns {
        public static final String TABLE_NAME = "recipe_step";
        public static final String COLUMN_NAME_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NAME_STEP_TYPE = "step_type";
        public static final String COLUMN_NAME_STEP_DATA = "step_data";
    }
}
