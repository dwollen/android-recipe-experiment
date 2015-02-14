package com.wollenstein.derek.amazingrecipeapp.recipes;

import android.support.annotation.Nullable;

/**
 * Created by dwollen on 2/13/15.
 */
public class RecipeIngredient {
    private final Long id;
    private final String name;
    private final int quantity;
    private final int quantityType;

    public RecipeIngredient(@Nullable Long id, String name, int quantity, int quantityType) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.quantityType = quantityType;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantityType() {
        return quantityType;
    }
}
