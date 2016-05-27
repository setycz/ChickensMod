package com.setycz.chickens.jei.drop;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

/**
 * Created by setyc on 21.02.2016.
 */
public class DropRecipeHandler implements IRecipeHandler<DropRecipeWrapper> {
    @Nonnull
    @Override
    public Class<DropRecipeWrapper> getRecipeClass() {
        return DropRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return DropRecipeCategory.UID;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull DropRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull DropRecipeWrapper recipe) {
        return recipe.getInputs().size() > 0 && recipe.getOutputs().size() > 0;
    }
}
