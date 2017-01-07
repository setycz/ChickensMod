package com.setycz.chickens.jei.throwing;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 07.01.2017.
 */
public class ThrowingRecipeWrapper extends BlankRecipeWrapper {

    private final ItemStack colorEgg;
    private final ItemStack chicken;

    public ThrowingRecipeWrapper(ItemStack colorEgg, ItemStack chicken) {
        this.colorEgg = colorEgg;
        this.chicken = chicken;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, colorEgg);
        ingredients.setOutput(ItemStack.class, chicken);
    }
}
