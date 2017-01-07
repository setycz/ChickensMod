package com.setycz.chickens.jei.throwing;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Ingredients;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 07.01.2017.
 */
public class ThrowingRecipeHandler implements IRecipeHandler<ThrowingRecipeWrapper> {

    @Override
    public Class<ThrowingRecipeWrapper> getRecipeClass() {
        return ThrowingRecipeWrapper.class;
    }

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return ThrowingRecipeCategory.UID;
    }

    @Override
    public String getRecipeCategoryUid(ThrowingRecipeWrapper recipe) {
        return ThrowingRecipeCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(ThrowingRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(ThrowingRecipeWrapper recipe) {
        IIngredients ingredients = new Ingredients();
        recipe.getIngredients(ingredients);
        return ingredients.getInputs(ItemStack.class).size() == 1 && ingredients.getOutputs(ItemStack.class).size() == 1;
    }
}
