package com.setycz.chickens.jei.henhousing;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Ingredients;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 07.01.2017.
 */
public class HenhousingRecipeHandler implements IRecipeHandler<HenhousingRecipeWrapper> {
    @Override
    public Class<HenhousingRecipeWrapper> getRecipeClass() {
        return HenhousingRecipeWrapper.class;
    }

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return HenhousingRecipeCategory.UID;
    }

    @Override
    public String getRecipeCategoryUid(HenhousingRecipeWrapper recipe) {
        return HenhousingRecipeCategory.UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(HenhousingRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(HenhousingRecipeWrapper recipe) {
        IIngredients ingredients = new Ingredients();
        recipe.getIngredients(ingredients);
        return ingredients.getInputs(ItemStack.class).size() > 0 && ingredients.getOutputs(ItemStack.class).size() == 1;
    }
}
