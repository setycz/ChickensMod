package com.setycz.chickens.jei.laying;

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
public class LayingRecipeCategory implements IRecipeCategory {

    public static final String UID = "chickens.Laying";
    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private String title;

    public LayingRecipeCategory(IGuiHelper guiHelper) {
        title = Translator.translateToLocal("gui.laying");

        ResourceLocation location = new ResourceLocation(ChickensMod.MODID, "textures/gui/laying.png");
        background = guiHelper.createDrawable(location, 55, 16, 82, 54);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 176, 14, 24, 17);
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
        arrow.draw(minecraft, 24, 18);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int inputSlot = 0;
        guiItemStacks.init(inputSlot, true, 0, 0);
        guiItemStacks.setFromRecipe(inputSlot, recipeWrapper.getInputs());

        int outputSlot = 1;
        guiItemStacks.init(outputSlot, false, 60, 18);
        guiItemStacks.setFromRecipe(outputSlot, recipeWrapper.getOutputs());
    }
}
