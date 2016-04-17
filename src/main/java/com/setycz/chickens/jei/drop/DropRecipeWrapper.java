package com.setycz.chickens.jei.drop;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by setyc on 21.02.2016.
 */
public class DropRecipeWrapper extends BlankRecipeWrapper{
    private final List<ItemStack> chicken;
    private final List<ItemStack> egg;

    public DropRecipeWrapper(ItemStack chicken, ItemStack drop) {
        this.chicken = Collections.singletonList(chicken);
        this.egg = Collections.singletonList(drop);
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
