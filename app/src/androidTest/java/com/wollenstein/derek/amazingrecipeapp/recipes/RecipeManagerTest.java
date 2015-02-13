package com.wollenstein.derek.amazingrecipeapp.recipes;

import junit.framework.TestCase;

/**
 * Test suite for {@link com.wollenstein.derek.amazingrecipeapp.recipes.RecipeManager}
 */
public class RecipeManagerTest extends TestCase {

    private static final String RECIPE_1_NAME = "Recipe 1";
    private static final String RECIPE_2_NAME = "Recipe 2";
    private static final String RECIPE_1_CONTENT = "Content 1";
    private static final String RECIPE_2_CONTENT = "Content 2";

    public void testNullRecipeNames_throws() {
        try{
            new RecipeManager(null,new String[]{}, null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException expected) {}
    }

    public void testNullRecipes_throws() {
        try {
            new RecipeManager(new String[]{}, null, null);
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException expected) {}
    }

    public void testDifferentSizes_throws() {
        try {
            new RecipeManager(
                    new String[] {RECIPE_1_NAME},
                    new String[] {RECIPE_1_CONTENT, RECIPE_2_CONTENT}, null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    public void testGetName_returnsName() {
        assertEquals(RECIPE_1_NAME, getTestManager().getRecipeTitle(0));
    }

    public void testGetNameOutOfBounds_throws() {
        try {
            assertEquals(RECIPE_1_NAME, getTestManager().getRecipeTitle(3));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    public void testGetNameBelowBounds_throws() {
        try {
            assertEquals(RECIPE_1_NAME, getTestManager().getRecipeTitle(-1));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    public void testGetContent_returnsContent() {
        assertEquals(RECIPE_2_CONTENT, getTestManager().getRecipe(1));
    }

    public void testGetContentOutOfBounds_throws() {
        try {
            assertEquals(RECIPE_1_NAME, getTestManager().getRecipe(3));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    public void testGetContentBelowBounds_throws() {
        try {
            assertEquals(RECIPE_1_NAME, getTestManager().getRecipe(-1));
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    public void testGetRecipeCount_returnsCount() {
        assertEquals(2, getTestManager().getRecipeCount());
    }

    public void testUpdateContent_returnsUpdatedContent() {
        String updateContent = "Other Content";
        RecipeManager manager = getTestManager();
        assertEquals(RECIPE_1_CONTENT, manager.getRecipe(0));
        manager.updateRecipe(0, updateContent);
        assertEquals(updateContent, manager.getRecipe(0));
    }

    public void testUpdateOutOfBounds_throws() {
        try {
            getTestManager().updateRecipe(2, "Should not work");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    public void testUpdateBeforeBounds_throws() {
        try {
            getTestManager().updateRecipe(-1, "Should not work");
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    private RecipeManager getTestManager() {
        return new RecipeManager(
                new String[] {RECIPE_1_NAME, RECIPE_2_NAME},
                new String[] {RECIPE_1_CONTENT, RECIPE_2_CONTENT}, null);
    }
}
