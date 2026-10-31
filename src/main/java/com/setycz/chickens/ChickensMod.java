package com.setycz.chickens;

import java.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.core.dispenser.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.Level;

public final class ChickensMod implements ModInitializer {
    private static boolean initialized;
    public static final Map<Item,Breed> COLORED_EGGS=new LinkedHashMap<>();
    public static final Map<String,Block> HENHOUSES=new LinkedHashMap<>();
    public static BlockEntityType<HenhouseBlockEntity> HENHOUSE_ENTITY;
    public static EntityType<ColoredEgg> COLORED_EGG_ENTITY;
    public static net.minecraft.world.inventory.MenuType<HenhouseMenu> HENHOUSE_MENU;
    public static Item ANALYZER,WATER_EGG,LAVA_EGG;
    public static Identifier id(String path){return Identifier.fromNamespaceAndPath("chickens",path);}
    private static Item.Properties props(String path){return new Item.Properties().setId(ResourceKey.create(Registries.ITEM,id(path)));}
    private static <T extends Item> T item(String path,T item){return Registry.register(BuiltInRegistries.ITEM,id(path),item);}
    @Override public void onInitialize(){initialize();}
    /** Companions can establish registry readiness regardless of entrypoint order. */
    public static synchronized void initialize(){
        if(initialized)return;
        initialized=true;
        HENHOUSE_MENU=Registry.register(BuiltInRegistries.MENU,id("henhouse"),new net.minecraft.world.inventory.MenuType<>(HenhouseMenu::new,net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
        WATER_EGG=item("water_egg",new LiquidEggItem(Fluids.WATER,props("water_egg").stacksTo(16)));
        LAVA_EGG=item("lava_egg",new LiquidEggItem(Fluids.LAVA,props("lava_egg").stacksTo(16)));
        for(Item egg:List.of(WATER_EGG,LAVA_EGG)) {
            var fluid=egg==WATER_EGG?Fluids.WATER:Fluids.LAVA;
            net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.combinedItemApiProvider(egg).register(context ->
                new LiquidEggStorage(context,net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(fluid)));
        }
        ANALYZER=item("analyzer",new Item(props("analyzer").stacksTo(1)));
        Breeds.defaults().forEach(b->Breeds.ALL.put(b.id,b));
        ChickenConfig.load();
        for(Breed b:Breeds.ALL.values()){
            var key=ResourceKey.create(Registries.ENTITY_TYPE,id(b.id+"_chicken"));
            b.entityType=Registry.register(BuiltInRegistries.ENTITY_TYPE,key,EntityType.Builder.of(ResourceChicken::new,MobCategory.CREATURE).sized(.4f,.7f).eyeHeight(.644f).clientTrackingRange(8).build(key));
            FabricDefaultAttributeRegistry.register(b.entityType,Chicken.createAttributes());
            String egg=b.id+"_chicken_spawn_egg";
            b.spawnEgg=item(egg,new SpawnEggItem(props(egg).spawnEgg(b.entityType)));
            if(b.dyeColor!=null){
                String name=b.id+"_egg";Item colored=item(name,new ColoredEggItem(props(name).stacksTo(16)));COLORED_EGGS.put(colored,b);
            }
            SpawnPlacements.register(b.entityType,SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(type,level,reason,pos,random)->{
                if(!b.enabled())return false;
                if(b.spawn==Breeds.SpawnType.NETHER)return !level.getBlockState(pos.below()).isAir() && level.getBlockState(pos).isAir();
                return net.minecraft.world.entity.animal.Animal.checkAnimalSpawnRules(type,level,reason,pos,random);
            });
            if(ChickenConfig.spawnWeight>0 && b.enabled() && b.tier()==1 && b.spawn!=Breeds.SpawnType.NONE){
                var selector=b.spawn==Breeds.SpawnType.NETHER?BiomeSelectors.foundInTheNether():b.spawn==Breeds.SpawnType.SNOW?BiomeSelectors.foundInOverworld().and(ctx->ctx.getBiome().coldEnoughToSnow(net.minecraft.core.BlockPos.ZERO,63)):BiomeSelectors.foundInOverworld().and(ctx->!ctx.getBiome().coldEnoughToSnow(net.minecraft.core.BlockPos.ZERO,63));
                BiomeModifications.addSpawn(selector,MobCategory.CREATURE,b.entityType,ChickenConfig.spawnWeight,3,5);
            }
        }
        var eggKey=ResourceKey.create(Registries.ENTITY_TYPE,id("colored_egg"));
        COLORED_EGG_ENTITY=Registry.register(BuiltInRegistries.ENTITY_TYPE,eggKey,EntityType.Builder.<ColoredEgg>of(ColoredEgg::new,MobCategory.MISC).sized(.25f,.25f).clientTrackingRange(4).updateInterval(10).build(eggKey));
        for(String wood:List.of("oak","acacia","birch","dark_oak","jungle","spruce")){
            String name=wood.equals("oak")?"henhouse":"henhouse_"+wood;
            Block b=Registry.register(BuiltInRegistries.BLOCK,id(name),new HenhouseBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK,id(name))).strength(2.5f).sound(SoundType.WOOD)));
            HENHOUSES.put(wood,b);item(name,new BlockItem(b,props(name).useBlockDescriptionPrefix()));
        }
        HENHOUSE_ENTITY=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,id("henhouse"),FabricBlockEntityTypeBuilder.create(HenhouseBlockEntity::new,HENHOUSES.values().toArray(Block[]::new)).build());
        for(var entry:COLORED_EGGS.entrySet()) DispenserBlock.registerProjectileBehavior(entry.getKey());
        for(Item egg:List.of(WATER_EGG,LAVA_EGG))DispenserBlock.registerBehavior(egg,new DefaultDispenseItemBehavior(){
            @Override protected ItemStack execute(BlockSource source,ItemStack stack){
                var target=source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                if(((LiquidEggItem)egg).emptyContents(null,source.level(),target,null)){stack.shrink(1);return stack;}
                return super.execute(source,stack);
            }
        });
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,id("chickens"),FabricCreativeModeTab.builder().title(Component.literal("Chickens")).icon(()->new ItemStack(Breeds.get("diamond").spawnEgg)).displayItems((parameters,out)->{
            for(Breed b:Breeds.ALL.values())if(b.enabled())out.accept(b.spawnEgg);
            for(var e:COLORED_EGGS.entrySet())if(e.getValue().enabled())out.accept(e.getKey());
            out.accept(ANALYZER);out.accept(WATER_EGG);out.accept(LAVA_EGG);HENHOUSES.values().forEach(out::accept);
        }).build());
        UseEntityCallback.EVENT.register((player,level,hand,target,hit)->{
            if(target.getClass()!=Chicken.class || !player.getItemInHand(hand).is(Items.BOOK) || !Breeds.get("smart").enabled())return InteractionResult.PASS;
            if(level instanceof ServerLevel server){
                teach((Chicken)target);
            }
            return InteractionResult.SUCCESS;
        });
    }
    public static ResourceChicken teach(Chicken original) {
        return original.convertTo(Breeds.get("smart").entityType,ConversionParams.single(original,true,true),EntitySpawnReason.CONVERSION,smart->{
            smart.setAge(original.getAge());smart.setHealth(original.getHealth());smart.setChickenJockey(original.isChickenJockey());
        });
    }

}
