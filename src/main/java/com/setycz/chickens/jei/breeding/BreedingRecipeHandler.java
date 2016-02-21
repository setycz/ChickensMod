package com.setycz.chickens.jei.breeding;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

/**
 * Created by setyc on 21.02.2016.
 */
public class BreedingRecipeHandler implements IRecipeHandler<BreedingRecipeWrapper> {
    @Nonnull
    @Override
    public Class<BreedingRecipeWrapper> getRecipeClass() {
        return BreedingRecipeWrapper.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return BreedingRecipeCategory.UID;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull BreedingRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull BreedingRecipeWrapper recipe) {
        return recipe.getInputs().size() > 1 && recipe.getOutputs().size() > 0;
    }
}
