package com.setycz.chickens.jei.breeding;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by setyc on 21.02.2016.
 */
public class BreedingRecipeWrapper extends BlankRecipeWrapper {
    private final List<ItemStack> parents;
    private final List<ItemStack> child;

    public BreedingRecipeWrapper(ItemStack parent1, ItemStack parent2, ItemStack child) {
        parents = new ArrayList<ItemStack>();
        parents.add(parent1);
        parents.add(parent2);
        this.child = Collections.singletonList(child);
    }

    @Override
    public List getInputs() {
        return parents;
    }

    @Override
    public List getOutputs() {
        return child;
    }
}
