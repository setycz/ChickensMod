package com.timwoodcreates.roost;
import java.nio.file.*;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
public final class RoostConfig {
 public static double roostSpeed=1,breederSpeed=1;
 public static void load(){
  var path=FabricLoader.getInstance().getConfigDir().resolve("roost.json");
  try{
   JsonObject data=Files.exists(path)?JsonParser.parseString(Files.readString(path)).getAsJsonObject():new JsonObject();
   if(data.has("roostSpeed"))roostSpeed=data.get("roostSpeed").getAsDouble();
   if(data.has("breederSpeed"))breederSpeed=data.get("breederSpeed").getAsDouble();
   if(!Double.isFinite(roostSpeed)||!Double.isFinite(breederSpeed)||roostSpeed<.01||breederSpeed<.01||roostSpeed>100||breederSpeed>100)throw new IllegalArgumentException("Speeds must be 0.01..100");
   data.addProperty("roostSpeed",roostSpeed);data.addProperty("breederSpeed",breederSpeed);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(data));
  }catch(Exception e){throw new IllegalStateException("Cannot load "+path,e);}
 }
}
