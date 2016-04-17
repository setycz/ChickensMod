package com.setycz.chickens.jei.drop;

import com.setycz.chickens.ChickensMod;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Created by setyc on 21.02.2016.
 */
public class DropRecipeCategory implements IRecipeCategory {

    public static final String UID = "chickens.Drops";
    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private String title;

    public DropRecipeCategory(IGuiHelper guiHelper) {
        title = Translator.translateToLocal("gui.drops");

        ResourceLocation location = new ResourceLocation(ChickensMod.MODID, "textures/gui/drops.png");
        background = guiHelper.createDrawable(location, 0, 0, 82, 54);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 82, 0, 13, 10);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        arrow.draw(minecraft, 40, 21);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int inputSlot = 0;
        guiItemStacks.init(inputSlot, true, 13, 15);
        guiItemStacks.setFromRecipe(inputSlot, recipeWrapper.getInputs());

        int outputSlot = 1;
        guiItemStacks.init(outputSlot, false, 57, 15);
        guiItemStacks.setFromRecipe(outputSlot, recipeWrapper.getOutputs());
    }
}
