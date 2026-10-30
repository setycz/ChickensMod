package com.timwoodcreates.roost.jei;

import java.util.*;
import com.setycz.chickens.*;
import com.timwoodcreates.roost.*;
import mezz.jei.api.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.*;
import mezz.jei.api.registration.*;
import mezz.jei.api.gui.builder.*;
import mezz.jei.api.gui.drawable.*;
import mezz.jei.api.gui.ingredient.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Discovered by JEI only; no main/client initializer references JEI classes. */
@JeiPlugin
public final class RoostJeiPlugin implements IModPlugin {
 public record Recipe(ItemStack left,ItemStack right,ItemStack child,int weight,int total) {}
 public static final RecipeType<Recipe> TYPE=RecipeType.create("roost","breeding",Recipe.class);
 public Identifier getPluginUid(){return RoostMod.id("jei_breeding");}
 @Override public void registerCategories(IRecipeCategoryRegistration r){r.addRecipeCategories(new Category(r.getJeiHelpers().getGuiHelper().createDrawableItemLike(RoostMod.BLOCKS.get(1))));}
 @Override public void registerRecipes(IRecipeRegistration r){r.addRecipes(TYPE,recipes());}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration r){r.addRecipeCatalyst(new ItemStack(RoostMod.BLOCKS.get(1)),TYPE);}
 public static List<Recipe> recipes(){
  List<Recipe> recipes=new ArrayList<>();Set<String> pairs=new HashSet<>();
  for(Breed child:Breeds.ALL.values()){
   if(!child.enabled())continue;
   addPair(recipes,pairs,child,child);
   if(child.parent1!=null&&child.parent2!=null)addPair(recipes,pairs,child.parent1,child.parent2);
  }
  var vanilla=RoostMod.BIRDS.get("vanilla");
  recipes.add(new Recipe(new ItemStack(vanilla),new ItemStack(vanilla),new ItemStack(vanilla),1,1));
  return List.copyOf(recipes);
 }
 private static void addPair(List<Recipe> recipes,Set<String> pairs,Breed a,Breed b){
  String key=a.id.compareTo(b.id)<=0?a.id+":"+b.id:b.id+":"+a.id;
  if(!a.enabled()||!b.enabled()||!pairs.add(key))return;
  for(var outcome:Breeding.outcomes(a,b))recipes.add(new Recipe(new ItemStack(RoostMod.BIRDS.get(a.id)),new ItemStack(RoostMod.BIRDS.get(b.id)),new ItemStack(RoostMod.BIRDS.get(outcome.breed().id)),outcome.weight(),outcome.totalWeight()));
 }
 static final class Category implements IRecipeCategory<Recipe>{
  private final IDrawable icon;Category(IDrawable icon){this.icon=icon;}
  public RecipeType<Recipe> getRecipeType(){return TYPE;}
  public Component getTitle(){return Component.literal("Chicken Breeding");}
  public IDrawable getIcon(){return icon;}
  public int getWidth(){return 154;}public int getHeight(){return 60;}
  public void setRecipe(IRecipeLayoutBuilder b,Recipe r,IFocusGroup focus){
   b.addInputSlot(4,8).addItemStack(r.left());b.addInputSlot(30,8).addItemStack(r.right());
   List<ItemStack> seeds=BuiltInRegistries.ITEM.get(ItemTags.CHICKEN_FOOD).map(tag->tag.stream().map(h->new ItemStack(h.value(),2)).toList()).orElse(List.of(new ItemStack(Items.WHEAT_SEEDS,2)));
   b.addInputSlot(60,8).addItemStacks(seeds);b.addOutputSlot(124,8).addItemStack(r.child());
  }
  public void draw(Recipe r,IRecipeSlotsView slots,GuiGraphicsExtractor g,double x,double y){
   var font=Minecraft.getInstance().font;
   g.text(font,"+",23,12,0xFF404040,false);g.text(font,"+",50,12,0xFF404040,false);g.text(font,"->",91,12,0xFF404040,false);
   g.text(font,String.format(Locale.ROOT,"Chance: %.1f%%",100.0*r.weight()/r.total()),4,33,0xFF404040,false);
   g.text(font,"Breeder + 2 seeds",4,46,0xFF404040,false);
  }
 }
}
