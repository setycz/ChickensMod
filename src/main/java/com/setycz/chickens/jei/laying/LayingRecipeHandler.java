package com.setycz.chickens.jei.laying;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Ingredients;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 21.02.2016.
 */
public class LayingRecipeHandler implements IRecipeHandler<LayingRecipeWrapper> {
    @Override
    public Class<LayingRecipeWrapper> getRecipeClass() {
        return LayingRecipeWrapper.class;
    }

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return LayingRecipeCategory.UID;
    }

    @Override
    public String getRecipeCategoryUid(LayingRecipeWrapper recipe) {
        return LayingRecipeCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(LayingRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(LayingRecipeWrapper recipe) {
        IIngredients ingredients = new Ingredients();
        recipe.getIngredients(ingredients);
        return ingredients.getInputs(ItemStack.class).size() > 0 && ingredients.getOutputs(ItemStack.class).size() > 0;
    }
}
