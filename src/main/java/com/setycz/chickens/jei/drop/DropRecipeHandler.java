package com.setycz.chickens.jei.drop;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Ingredients;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 21.02.2016.
 */
public class DropRecipeHandler implements IRecipeHandler<DropRecipeWrapper> {
    @Override
    public Class<DropRecipeWrapper> getRecipeClass() {
        return DropRecipeWrapper.class;
    }

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return DropRecipeCategory.UID;
    }

    @Override
    public String getRecipeCategoryUid(DropRecipeWrapper recipe) {
        return DropRecipeCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(DropRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(DropRecipeWrapper recipe) {
        IIngredients ingredients = new Ingredients();
        recipe.getIngredients(ingredients);
        return ingredients.getInputs(ItemStack.class).size() > 0 && ingredients.getOutputs(ItemStack.class).size() > 0;
    }
}
