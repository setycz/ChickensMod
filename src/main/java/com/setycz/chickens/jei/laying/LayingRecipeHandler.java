package com.setycz.chickens.jei.laying;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

/**
 * Created by setyc on 21.02.2016.
 */
public class LayingRecipeHandler implements IRecipeHandler<LayingRecipeWrapper> {
    @Nonnull
    @Override
    public Class<LayingRecipeWrapper> getRecipeClass() {
        return LayingRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return LayingRecipeCategory.UID;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull LayingRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull LayingRecipeWrapper recipe) {
        return recipe.getInputs().size() > 0 && recipe.getOutputs().size() > 0;
    }
}
