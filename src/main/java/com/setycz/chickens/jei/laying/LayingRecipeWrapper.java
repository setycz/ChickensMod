package com.setycz.chickens.jei.laying;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Created by setyc on 21.02.2016.
 */
public class LayingRecipeWrapper extends BlankRecipeWrapper{
    private final List<ItemStack> chicken;
    private final List<ItemStack> egg;

    public LayingRecipeWrapper(ItemStack chicken, ItemStack egg) {
        this.chicken = Collections.singletonList(chicken);
        this.egg = Collections.singletonList(egg);
    }

    @Override
    public List getInputs() {
        return chicken;
    }

    @Override
    public List getOutputs() {
        return egg;
    }
}
