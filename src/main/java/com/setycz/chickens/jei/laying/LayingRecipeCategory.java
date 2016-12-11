package com.setycz.chickens.jei.laying;

import com.setycz.chickens.ChickensMod;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Created by setyc on 21.02.2016.
 */
public class LayingRecipeCategory implements IRecipeCategory {

    public static final String UID = "chickens.Laying";
    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final String title;
    private final IDrawableStatic icon;

    public LayingRecipeCategory(IGuiHelper guiHelper) {
        title = Translator.translateToLocal("gui.laying");

        ResourceLocation location = new ResourceLocation(ChickensMod.MODID, "textures/gui/laying.png");
        background = guiHelper.createDrawable(location, 0, 0, 82, 54);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 82, 0, 13, 10);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        ResourceLocation iconLocation = new ResourceLocation(ChickensMod.MODID, "textures/gui/laying_icon.png");
        icon = guiHelper.createDrawable(iconLocation, 0, 0, 16, 16);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        arrow.draw(minecraft, 40, 21);
    }

    @Override
    @Deprecated
    public void drawAnimations(Minecraft minecraft) {
    }

    @Override
    @Deprecated
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int inputSlot = 0;
        guiItemStacks.init(inputSlot, true, 13, 15);
        guiItemStacks.set(ingredients);

        int outputSlot = 1;
        guiItemStacks.init(outputSlot, false, 57, 15);
        guiItemStacks.set(ingredients);
    }
}
