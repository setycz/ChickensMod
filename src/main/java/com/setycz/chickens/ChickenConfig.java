package com.setycz.chickens;
import com.google.gson.*;
import java.nio.file.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;

/** Validated server configuration. Invalid settings stop startup with their location. */
public final class ChickenConfig {
 public static int spawnWeight=2;
 public static void load(){
  Path file=FabricLoader.getInstance().getConfigDir().resolve("chickens.json");
  try{
   JsonObject root;
   if(Files.exists(file)){try(var reader=Files.newBufferedReader(file)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
   else root=new JsonObject();
   if(root.has("spawnWeight"))spawnWeight=Math.clamp(root.get("spawnWeight").getAsInt(),0,100);
   root.addProperty("spawnWeight",spawnWeight);
   JsonObject breeds=root.has("breeds")?root.getAsJsonObject("breeds"):new JsonObject();
   for(Breed b:Breeds.ALL.values()){
    JsonObject cfg=breeds.has(b.id)?breeds.getAsJsonObject(b.id):new JsonObject();
    if(cfg.has("enabled"))b.enabled=cfg.get("enabled").getAsBoolean();
    if(cfg.has("layCoefficient")){double v=cfg.get("layCoefficient").getAsDouble();if(!Double.isFinite(v)||v<.01||v>100)throw new IllegalArgumentException(b.id+".layCoefficient must be 0.01..100");b.layCoefficient=v;}
    if(cfg.has("layItem"))b.layItem=stack(cfg.get("layItem").getAsString());
    if(cfg.has("dropItem"))b.dropItem=stack(cfg.get("dropItem").getAsString());
    cfg.addProperty("enabled",b.enabled);cfg.addProperty("layCoefficient",b.layCoefficient);cfg.addProperty("layItem",BuiltInRegistries.ITEM.getKey(b.layItem).toString());
    cfg.addProperty("dropItem",BuiltInRegistries.ITEM.getKey(b.dropItem==null?b.layItem:b.dropItem).toString());
    breeds.add(b.id,cfg);
   }
   root.add("breeds",breeds);Files.createDirectories(file.getParent());
   try(var writer=Files.newBufferedWriter(file)){new GsonBuilder().setPrettyPrinting().create().toJson(root,writer);}
  }catch(Exception e){throw new IllegalStateException("Cannot load "+file+": "+e.getMessage(),e);}
 }
 private static Item stack(String name){
  var key=Identifier.parse(name);if(!BuiltInRegistries.ITEM.containsKey(key))throw new IllegalArgumentException("Unknown item "+name);
  var item=BuiltInRegistries.ITEM.getValue(key);if(item==Items.AIR)throw new IllegalArgumentException("Air is not chicken output");return item;
 }
}
