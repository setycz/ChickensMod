package com.setycz.chickens.jei.laying;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Created by setyc on 21.02.2016.
 */
public class LayingRecipeWrapper extends BlankRecipeWrapper{
    private final List<ItemStack> input;
    private final List<ItemStack> output;

    public LayingRecipeWrapper(ItemStack input, ItemStack output) {
        this.input = Collections.singletonList(input);
        this.output = Collections.singletonList(output);
    }

    @Override
    public List getInputs() {
        return input;
    }

    @Override
    public List getOutputs() {
        return output;
    }
}
