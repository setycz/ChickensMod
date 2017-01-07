package com.setycz.chickens.jei.throwing;

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
public class ThrowingRecipeCategory implements IRecipeCategory {
    public static final String UID = "chickens.Throws";
    private final String title;
    private final IDrawableStatic background;
    private final IDrawableStatic icon;
    private final IDrawableAnimated arrow;

    public ThrowingRecipeCategory(IGuiHelper guiHelper) {
        title = Translator.translateToLocal("gui.throws");

        ResourceLocation location = new ResourceLocation(ChickensMod.MODID, "textures/gui/throws.png");
        background = guiHelper.createDrawable(location, 0, 0, 82, 54);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 82, 0, 13, 10);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        ResourceLocation iconLocation = new ResourceLocation(ChickensMod.MODID, "textures/gui/throws_icon.png");
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
        arrow.draw(minecraft, 32, 21);
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
        guiItemStacks.init(inputSlot, true, 10, 15);
        guiItemStacks.set(ingredients);

        int outputSlot = 1;
        guiItemStacks.init(outputSlot, false, 55, 15);
        guiItemStacks.set(ingredients);
    }
}
