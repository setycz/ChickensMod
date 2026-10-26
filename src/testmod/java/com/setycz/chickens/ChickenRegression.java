package com.setycz.chickens;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.*;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.phys.*;

public final class ChickenRegression implements ModInitializer {
 private static int checks;
 private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);checks++;System.out.println("CHICKENS PASS: "+message);}
 @Override public void onInitialize(){ServerLifecycleEvents.SERVER_STARTED.register(server->{
  try{run(server.overworld());System.out.println("CHICKENS REGRESSION OK: "+checks+" checks");}
  catch(Throwable e){e.printStackTrace();System.out.println("CHICKENS REGRESSION FAILED");}
  finally{server.halt(false);}
 });}
 private static ResourceChicken chicken(ServerLevel level,String id){return Breeds.get(id).entityType.create(level,EntitySpawnReason.COMMAND);}
 private static int output(HenhouseBlockEntity h){int n=0;for(int i=2;i<=10;i++)n+=h.getItem(i).getCount();return n;}
 private static void run(ServerLevel level){
  check(Breeds.ALL.size()==42,"all 42 original breeds registered");
  for(Breed b:Breeds.ALL.values()){
   check(b.entityType!=null && b.spawnEgg!=null && b.tier()>=1,b.id+" entity, egg and ancestry");
   check(b.layItem!=Items.AIR,b.id+" produces a real item");
  }
  check(Breeds.get("blue").layItem==Items.LAPIS_LAZULI && Breeds.get("white").layItem==Items.BONE_MEAL && Breeds.get("black").layItem==Items.INK_SAC && Breeds.get("brown").layItem==Items.COCOA_BEANS,"legacy dye resources keep lapis, bone meal, ink and cocoa outputs");
  check(ChickensMod.COLORED_EGGS.size()==16,"all 16 dye eggs registered");
  check(ChickensMod.HENHOUSES.size()==6,"all six henhouses registered");
  check(Breeding.stat(1,10,1,10,1)==10,"stronger parent weights inherited stat");
  check(Breeding.stat(1,1,1,1,1)==2 && Breeding.stat(10,10,10,10,2)==10,"stats improve and cap at ten");
  var a=chicken(level,"iron");var b=chicken(level,"iron");a.stats(1,1,1);b.stats(1,9,1);
  var child=(ResourceChicken)a.getBreedOffspring(level,b);
  check(child.gain()>=6 && child.gain()<=7,"Gain uses both parents rather than parent two twice");
  a.stats(10,10,10);b.stats(10,10,10);child=(ResourceChicken)a.getBreedOffspring(level,b);
  check(child.growth()==10 && child.gain()==10 && child.strength()==10,"10/10/10 stays capped");
  var red=chicken(level,"red");var white=chicken(level,"white");red.stats(10,10,10);white.stats(10,10,10);
  int pink=0;
  for(int i=0;i<300;i++){
   var c=(ResourceChicken)red.getBreedOffspring(level,white);
   check(c.breed().id.equals("red") || c.breed().id.equals("white") || c.breed().id.equals("pink"),"crossbreed produces only valid outcomes");
   if(c.breed().id.equals("pink")){pink++;check(c.growth()==1 && c.gain()==1 && c.strength()==1,"new breed starts 1/1/1");}
  }
  check(pink>0 && pink<150,"crossbreeding can unlock pink without guaranteeing mutation");
  a.stats(9,8,7);a.setAge(-1234);var out=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());a.addAdditionalSaveData(out);
  var loaded=chicken(level,"iron");loaded.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),out.buildResult()));
  check(loaded.growth()==9 && loaded.gain()==8 && loaded.strength()==7 && loaded.getAge()==-1234,"stats and age survive save/reload");
  var malformed=out.buildResult();malformed.putInt("Growth",-200);malformed.putInt("Gain",500);malformed.putInt("Strength",0);
  loaded.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),malformed));
  check(loaded.growth()==1 && loaded.gain()==10 && loaded.strength()==1,"malformed saved stats clamped");
  check(chicken(level,"quartz").fireImmune() && !chicken(level,"iron").fireImmune(),"Nether base fire immunity preserved");
  check(Breeds.get("iron").lay(4).getCount()==1 && Breeds.get("iron").lay(5).getCount()==2 && Breeds.get("iron").lay(10).getCount()==3,"Gain output thresholds");
  var state=ChickensMod.HENHOUSES.get("oak").defaultBlockState();var h=new HenhouseBlockEntity(BlockPos.ZERO,state);
  var rest=h.collect(new ItemStack(Items.IRON_INGOT,3));check(rest.getCount()==3 && output(h)==0,"unfueled henhouse leaves every item for world drop");
  h.setItem(0,new ItemStack(Items.HAY_BLOCK));rest=h.collect(new ItemStack(Items.IRON_INGOT,3));check(rest.isEmpty() && output(h)==3 && h.energy()==97,"Gain three collected once and fuel charged per item");
  var save=h.saveWithoutMetadata(level.registryAccess());var reloaded=new HenhouseBlockEntity(BlockPos.ZERO,state);reloaded.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),save));
  check(output(reloaded)==3 && reloaded.energy()==97,"henhouse inventory and remaining hay energy persist");
  h=new HenhouseBlockEntity(BlockPos.ZERO,state);h.setItem(0,new ItemStack(Items.HAY_BLOCK,2));for(int i=2;i<=10;i++)h.setItem(i,new ItemStack(Items.IRON_INGOT,64));h.setItem(2,new ItemStack(Items.IRON_INGOT,63));
  rest=h.collect(new ItemStack(Items.IRON_INGOT,3));check(rest.getCount()==2 && output(h)==576 && h.energy()==99,"partial collection returns exact remainder without duplication");
  rest=h.collect(new ItemStack(Items.IRON_INGOT,3));check(rest.getCount()==3 && h.energy()==99,"full henhouse consumes no fuel and loses no items");
  h=new HenhouseBlockEntity(BlockPos.ZERO,state);h.setItem(0,new ItemStack(Items.HAY_BLOCK,2));h.setItem(1,new ItemStack(Items.DIRT,63));
  rest=h.collect(new ItemStack(Items.IRON_INGOT,100));check(rest.isEmpty() && h.getItem(1).getCount()==64 && h.getItem(0).getCount()==1,"one hay collects 100 items and becomes one dirt");
  rest=h.collect(new ItemStack(Items.IRON_INGOT));check(rest.getCount()==1 && h.getItem(0).getCount()==1,"full compost slot blocks further hay consumption");
  var p=new BlockPos(0,100,0);level.getChunk(p);level.setBlock(p,state,3);h=(HenhouseBlockEntity)level.getBlockEntity(p);h.setItem(0,new ItemStack(Items.HAY_BLOCK));
  var iron=chicken(level,"iron");iron.stats(10,10,10);iron.setPos(.5,101,.5);iron.setNoAi(true);level.addFreshEntity(iron);
  var saved=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());iron.addAdditionalSaveData(saved);var tag=saved.buildResult();tag.putInt("LayTime",1);iron.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),tag));
  iron.aiStep();check(output(h)==3 && h.energy()==97,"live chicken deposits all Gain output into nearby henhouse");
  int eggs=level.getEntitiesOfClass(ItemEntity.class,new AABB(p).inflate(5),e->e.getItem().is(Items.EGG)).size();
  check(eggs==0,"resource chicken suppresses extra vanilla egg production");
  var loot=new java.util.ArrayList<ItemStack>();iron.dropFromLootTable(level,level.damageSources().generic(),true,net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE,ChickensMod.id("entities/resource_chicken")),loot::add);
  check(loot.stream().anyMatch(s->s.is(Items.FEATHER)) && loot.stream().anyMatch(s->s.is(Items.CHICKEN)),"resource death loot table parses and generates both pools");
  // Native Fabric fluid transactions must consume one egg only on commit.
  for(var egg:java.util.List.of(ChickensMod.WATER_EGG,ChickensMod.LAVA_EGG)){
   var fluid=net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(egg==ChickensMod.WATER_EGG?net.minecraft.world.level.material.Fluids.WATER:net.minecraft.world.level.material.Fluids.LAVA);
   var inv=new net.minecraft.world.SimpleContainer(new ItemStack(egg,2));
   var slot=net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage.of(inv,null).getSlot(0);
   var context=net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.ofSingleSlot(slot);
   var storage=context.find(net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.ITEM);
   long bucket=net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;
   try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()){
    check(storage.extract(fluid,bucket-1,tx)==0,"partial tank requests cannot consume a liquid egg");
   }
   try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()){
    check(storage.extract(fluid,bucket,tx)==bucket,"liquid egg exposes one bucket");
   }
   check(inv.getItem(0).getCount()==2,"rolled back fluid transfer leaves eggs intact");
   try(var tx=net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()){
    check(storage.extract(fluid,bucket,tx)==bucket,"committed transfer yields one bucket");tx.commit();
   }
   check(inv.getItem(0).getCount()==1,"committed transfer consumes one egg with no free bucket");
  }
  check(h.canPlaceItemThroughFace(0,new ItemStack(Items.HAY_BLOCK),Direction.UP) && !h.canPlaceItemThroughFace(1,new ItemStack(Items.DIRT),Direction.UP),"hopper input accepts only hay in fuel slot");
  check(h.canTakeItemThroughFace(2,new ItemStack(Items.IRON_INGOT),Direction.DOWN) && !h.canTakeItemThroughFace(0,new ItemStack(Items.HAY_BLOCK),Direction.DOWN),"hopper output cannot extract stored fuel");
  var invPlayer=new net.minecraft.world.entity.player.Player(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"ChickenTest")) {
   public net.minecraft.world.level.GameType gameMode(){return net.minecraft.world.level.GameType.SURVIVAL;} public boolean isSpectator(){return false;}public boolean isCreative(){return false;}
  };
  var regular=EntityTypes.CHICKEN.create(level,EntitySpawnReason.COMMAND);regular.setPos(3,101,3);regular.setNoAi(true);regular.setAge(-1234);regular.setHealth(2);regular.setCustomName(net.minecraft.network.chat.Component.literal("Bookworm"));level.addFreshEntity(regular);regular.setLeashedTo(invPlayer,false);
  var smart=ChickensMod.teach(regular);
  check(smart!=null && regular.isRemoved() && smart.breed()==Breeds.get("smart") && smart.getAge()==-1234 && smart.getHealth()==2 && smart.getName().getString().equals("Bookworm") && smart.isNoAi(),"book conversion keeps age, health, name and AI setting");
  check(smart.isLeashed() && smart.getLeashHolder()==invPlayer,"book conversion preserves the lead connection");smart.discard();
  var menu=new HenhouseMenu(1,invPlayer.getInventory(),h);
  check(menu.getSlot(0).mayPlace(new ItemStack(Items.HAY_BLOCK)) && !menu.getSlot(0).mayPlace(new ItemStack(Items.DIAMOND)),"menu fuel rejects non-hay");
  check(!menu.getSlot(1).mayPlace(new ItemStack(Items.DIRT)) && !menu.getSlot(2).mayPlace(new ItemStack(Items.IRON_INGOT)) && !menu.getSlot(11).isActive(),"output and unused GUI slots reject insertion");
  h.setItem(0,new ItemStack(Items.HAY_BLOCK,2));h.setItem(2,new ItemStack(Items.IRON_INGOT,3));
  for(var entity:level.getEntitiesOfClass(ItemEntity.class,new AABB(p).inflate(8)))entity.discard();
  level.removeBlock(p,false);
  var drops=level.getEntitiesOfClass(ItemEntity.class,new AABB(p).inflate(8));
  check(drops.stream().filter(e->e.getItem().is(Items.HAY_BLOCK)).mapToInt(e->e.getItem().getCount()).sum()==2 && drops.stream().filter(e->e.getItem().is(Items.IRON_INGOT)).mapToInt(e->e.getItem().getCount()).sum()==3,"breaking stocked henhouse drops contents exactly once");
  var dp=p.east(10);var dispenser=net.minecraft.world.level.block.Blocks.DISPENSER.defaultBlockState().setValue(net.minecraft.world.level.block.DispenserBlock.FACING,Direction.EAST);
  level.setBlock(dp,dispenser,3);var db=(net.minecraft.world.level.block.entity.DispenserBlockEntity)level.getBlockEntity(dp);
  var source=new net.minecraft.core.dispenser.BlockSource(level,dp,dispenser,db);
  var water=new ItemStack(ChickensMod.WATER_EGG,2);var behavior=net.minecraft.world.level.block.DispenserBlock.DISPENSER_REGISTRY.get(ChickensMod.WATER_EGG);
  behavior.dispense(source,water);
  check(water.getCount()==1 && level.getFluidState(dp.east()).is(net.minecraft.world.level.material.Fluids.WATER),"dispenser places water and consumes exactly one egg");
  var colored=ChickensMod.COLORED_EGGS.keySet().iterator().next();var coloredStack=new ItemStack(colored,2);
  net.minecraft.world.level.block.DispenserBlock.DISPENSER_REGISTRY.get(colored).dispense(source,coloredStack);
  check(coloredStack.getCount()==1 && !level.getEntitiesOfClass(ColoredEgg.class,new AABB(dp).inflate(4)).isEmpty(),"dispenser throws custom colored egg and consumes one");
  iron.discard();level.removeBlock(dp,false);level.removeBlock(dp.east(),false);
 }
}
