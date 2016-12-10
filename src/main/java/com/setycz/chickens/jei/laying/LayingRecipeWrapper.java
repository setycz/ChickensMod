package com.setycz.chickens.jei.laying;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by setyc on 21.02.2016.
 */
public class LayingRecipeWrapper extends BlankRecipeWrapper {
    private final ItemStack chicken;
    private final ItemStack egg;
    private final int minTime;
    private final int maxTime;

    public LayingRecipeWrapper(ItemStack chicken, ItemStack egg, int minTime, int maxTime) {
        this.minTime = minTime / 20 / 60;
        this.maxTime = maxTime / 20 / 60;
        this.chicken = chicken;
        this.egg = egg;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, chicken);
        ingredients.setOutput(ItemStack.class, egg);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String message = Translator.translateToLocalFormatted("gui.laying.time", minTime, maxTime);
        minecraft.fontRendererObj.drawString(message, 24, 7, Color.gray.getRGB());
    }
}
