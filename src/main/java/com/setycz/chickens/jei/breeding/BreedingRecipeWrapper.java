package com.setycz.chickens.jei.breeding;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by setyc on 21.02.2016.
 */
public class BreedingRecipeWrapper extends BlankRecipeWrapper {
    private final List<ItemStack> parents;
    private final List<ItemStack> child;
    private final int chance;

    public BreedingRecipeWrapper(ItemStack parent1, ItemStack parent2, ItemStack child, float chance) {
        parents = new ArrayList<ItemStack>();
        parents.add(parent1);
        parents.add(parent2);
        this.child = Collections.singletonList(child);
        this.chance = Math.round(chance);
    }

    @Override
    public List getInputs() {
        return parents;
    }

    @Override
    public List getOutputs() {
        return child;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String message = Translator.translateToLocalFormatted("gui.breeding.time", chance);
        minecraft.fontRendererObj.drawString(message, 32, 25, Color.gray.getRGB());
    }
}
