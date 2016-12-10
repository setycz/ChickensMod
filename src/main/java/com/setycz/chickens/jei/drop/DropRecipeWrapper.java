package com.setycz.chickens.jei.drop;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 21.02.2016.
 */
public class DropRecipeWrapper extends BlankRecipeWrapper {
    private final ItemStack chicken;
    private final ItemStack egg;

    public DropRecipeWrapper(ItemStack chicken, ItemStack drop) {
        this.chicken = chicken;
        this.egg = drop;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, chicken);
        ingredients.setOutput(ItemStack.class, egg);
    }
}
