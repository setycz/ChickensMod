package com.timwoodcreates.roost;
import java.util.*;
import com.setycz.chickens.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.inventory.*;
public final class RoostMod implements ModInitializer {
 public static final List<String> NAMES=List.of("roost","breeder","collector");
 public static final Map<String,ChickenItem> BIRDS=new LinkedHashMap<>();
 public static final List<ChickenItem> ORDER=new ArrayList<>();
 public static final List<MachineBlock> BLOCKS=new ArrayList<>();
 public static final List<BlockEntityType<Machine>> ENTITIES=new ArrayList<>();
 public static final List<MenuType<MachineMenu>> MENUS=new ArrayList<>();
 public static Item CATCHER;
 public static Identifier id(String name){return Identifier.fromNamespaceAndPath("roost",name);}
 private static Item.Properties props(String name){return new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id(name)));}
 @Override public void onInitialize(){
  ChickensMod.initialize();
  RoostConfig.load();
  CATCHER=Registry.register(BuiltInRegistries.ITEM,id("catcher"),new Item(props("catcher").durability(64)));
  registerBird("vanilla",null);
  for(Breed breed:Breeds.ALL.values())registerBird(breed.id,breed);
  for(int kind=0;kind<3;kind++){
   final int k=kind;var name=NAMES.get(kind);
   MachineBlock block=Registry.register(BuiltInRegistries.BLOCK,id(name),new MachineBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK,id(name))).strength(2.5f).sound(SoundType.WOOD).noOcclusion(),kind));BLOCKS.add(block);
   Registry.register(BuiltInRegistries.ITEM,id(name),new BlockItem(block,props(name).useBlockDescriptionPrefix()));
   ENTITIES.add(Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,id(name),FabricBlockEntityTypeBuilder.create(Machine::new,block).build()));
   MENUS.add(Registry.register(BuiltInRegistries.MENU,id(name),new MenuType<>((sync,inv)->new MachineMenu(sync,inv,k),net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS)));
  }
  UseEntityCallback.EVENT.register((player,level,hand,target,hit)->{
   var held=player.getItemInHand(hand);if(!held.is(CATCHER)||!(target instanceof Chicken chicken))return InteractionResult.PASS;
   if(chicken.isBaby()||chicken.isPassenger()||chicken.isVehicle()){if(!level.isClientSide())player.sendSystemMessage(Component.literal("Catch an adult chicken that is not carrying or riding another creature."));return InteractionResult.FAIL;}
   if(!(chicken instanceof ResourceChicken) && chicken.getClass()!=Chicken.class)return InteractionResult.PASS;
   if(level instanceof ServerLevel server && capture(player,chicken,server))held.hurtAndBreak(1,player,hand==InteractionHand.MAIN_HAND?net.minecraft.world.entity.EquipmentSlot.MAINHAND:net.minecraft.world.entity.EquipmentSlot.OFFHAND);
   return InteractionResult.SUCCESS;
  });
  Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,id("roost"),FabricCreativeModeTab.builder().title(Component.literal("Roost")).icon(()->new ItemStack(CATCHER)).displayItems((p,out)->{out.accept(CATCHER);BLOCKS.forEach(out::accept);BIRDS.values().forEach(out::accept);}).build());
 }
 private static void registerBird(String name,Breed breed){var item=Registry.register(BuiltInRegistries.ITEM,id(name+"_chicken"),new ChickenItem(props(name+"_chicken").stacksTo(16),breed));BIRDS.put(name,item);ORDER.add(item);}
 public static boolean capture(net.minecraft.world.entity.player.Player player,Chicken chicken,ServerLevel level){
  if(chicken.isRemoved()||chicken.isBaby()||chicken.isPassenger()||chicken.isVehicle())return false;
  var stack=ChickenItem.capture(chicken,level);
  if(!player.getInventory().add(stack) && !stack.isEmpty() && chicken.spawnAtLocation(level,stack)==null)return false;
  if(chicken.isLeashed())chicken.dropLeash();chicken.playSound(net.minecraft.sounds.SoundEvents.CHICKEN_EGG,1,1);chicken.discard();return true;
 }
}
