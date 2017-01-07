package com.setycz.chickens.jei.henhousing;

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
 * Created by setyc on 07.01.2017.
 */
public class HenhousingRecipeCategory implements IRecipeCategory {
    public static final String UID = "chickens.Henhousing";
    private final String title;
    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final IDrawableStatic icon;

    public HenhousingRecipeCategory(IGuiHelper guiHelper) {
        title = Translator.translateToLocal("gui.henhousing");

        ResourceLocation location = new ResourceLocation(ChickensMod.MODID, "textures/gui/henhouse.png");
        background = guiHelper.createDrawable(location, 18, 12, 72, 62);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 195, 0, 12, 57);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);

        ResourceLocation iconLocation = new ResourceLocation(ChickensMod.MODID, "textures/gui/henhousing_icon.png");
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
        arrow.draw(minecraft, 75 - 18, 14 - 12);
    }

    @Override
    @Deprecated
    public void drawAnimations(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int inputSlot = 0;
        guiItemStacks.init(inputSlot, true, 24 - 18, 17 - 12);
        guiItemStacks.set(ingredients);

        int outputSlot = 1;
        guiItemStacks.init(outputSlot, false, 24 - 18, 54 - 12);
        guiItemStacks.set(ingredients);
    }
}
