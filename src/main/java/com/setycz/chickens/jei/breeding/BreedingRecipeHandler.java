package com.setycz.chickens.jei.breeding;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Ingredients;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 21.02.2016.
 */
public class BreedingRecipeHandler implements IRecipeHandler<BreedingRecipeWrapper> {
    @Override
    public Class<BreedingRecipeWrapper> getRecipeClass() {
        return BreedingRecipeWrapper.class;
    }

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return BreedingRecipeCategory.UID;
    }

    @Override
    public String getRecipeCategoryUid(BreedingRecipeWrapper recipe) {
        return BreedingRecipeCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(BreedingRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(BreedingRecipeWrapper recipe) {
        IIngredients ingredients = new Ingredients();
        recipe.getIngredients(ingredients);
        return ingredients.getInputs(ItemStack.class).size() > 1 && ingredients.getOutputs(ItemStack.class).size() > 0;
    }
}
