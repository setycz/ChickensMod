package com.setycz.chickens.jei.laying;

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
public class LayingRecipeWrapper extends BlankRecipeWrapper{
    private final List<ItemStack> chicken;
    private final List<ItemStack> egg;
    private final int minTime;
    private final int maxTime;

    public LayingRecipeWrapper(ItemStack chicken, ItemStack egg, int minTime, int maxTime) {
        this.minTime = minTime/20/60;
        this.maxTime = maxTime/20/60;
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

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
        String message = Translator.translateToLocalFormatted("gui.laying.time", minTime, maxTime);
        minecraft.fontRendererObj.drawString(message, 24, 7, Color.gray.getRGB());
    }
}
