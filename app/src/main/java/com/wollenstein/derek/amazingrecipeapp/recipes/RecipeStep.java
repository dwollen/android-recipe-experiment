package com.wollenstein.derek.amazingrecipeapp.recipes;

import android.support.annotation.Nullable;

/**
 * Created by dwollen on 2/13/15.
 */
public class RecipeStep {
    private final Long id;
    private final int stepType;
    private final String stepData;

    public RecipeStep(String stepData) {
        this(null, RecipeContract.STEP_TYPE_PREPARE, stepData);
    }

    public RecipeStep(@Nullable Long id, int stepType, String stepData) {
        this.id = id;
        this.stepType = stepType;
        this.stepData = stepData;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public int getStepType() {
        return stepType;
    }

    public String getStepData() {
        return stepData;
    }
}
