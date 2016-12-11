package com.setycz.chickens.jei.breeding;

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
public class BreedingRecipeCategory implements IRecipeCategory {
    public static final String UID = "chickens.Breeding";
    private final String title;
    private final IDrawableStatic background;
    private final IDrawableAnimated arrow;
    private final IDrawableStatic icon;

    public BreedingRecipeCategory(IGuiHelper guiHelper) {
        title = Translator.translateToLocal("gui.breeding");

        ResourceLocation location = new ResourceLocation(ChickensMod.MODID, "textures/gui/breeding.png");
        background = guiHelper.createDrawable(location, 0, 0, 82, 54);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 82, 0, 7, 7);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

        ResourceLocation iconLocation = new ResourceLocation(ChickensMod.MODID, "textures/gui/breeding_icon.png");
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
        arrow.draw(minecraft, 37, 5);
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

        int parent1Slot = 0;
        guiItemStacks.init(parent1Slot, true, 10, 15);
        guiItemStacks.set(ingredients);

        int parent2Slot = 1;
        guiItemStacks.init(parent2Slot, true, 53, 15);
        guiItemStacks.set(ingredients);

        int childrenSlot = 2;
        guiItemStacks.init(childrenSlot, false, 33, 30);
        guiItemStacks.set(ingredients);
    }
}
