package com.wollenstein.derek.amazingrecipeapp.recipes;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a recipe in our system.
 */
public final class Recipe {
    private final Long id;
    private final String name;
    private final List<RecipeIngredient> ingredients;
    private final List<RecipeStep> steps;

    public Recipe(String name, List<RecipeStep> stepList) {
        this(null, name, stepList, new ArrayList<RecipeIngredient>());
    }

    public Recipe(
            @Nullable Long id, String name, List<RecipeStep> stepList,
            List<RecipeIngredient> ingredientList) {
        this.id = id;
        this.name = name;
        this.steps = new ArrayList<>(stepList);
        this.ingredients = new ArrayList<>(ingredientList);
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public static Recipe fromNameAndSteps(String name, String steps) {
        String[] stepEntries = steps.split("\n");
        List<RecipeStep> stepList = new ArrayList<>();
        for (String entry : stepEntries) {
            stepList.add(new RecipeStep(entry));
        }
        return new Recipe(name, stepList);
    }
}
