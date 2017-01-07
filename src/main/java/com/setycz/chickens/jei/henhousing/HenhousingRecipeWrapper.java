package com.setycz.chickens.jei.henhousing;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 07.01.2017.
 */
public class HenhousingRecipeWrapper extends BlankRecipeWrapper {

    private final ItemStack hayBale;
    private final ItemStack dirtBlock;

    public HenhousingRecipeWrapper(ItemStack hayBale, ItemStack dirtBlock) {
        this.hayBale = hayBale;
        this.dirtBlock = dirtBlock;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, hayBale);
        ingredients.setOutput(ItemStack.class, dirtBlock);
    }
}
