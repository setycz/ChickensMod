package com.timwoodcreates.roost;

import com.setycz.chickens.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.storage.*;
import net.minecraft.network.chat.Component;

public final class ChickenItem extends Item {
    public final Breed breed;
    public ChickenItem(Properties properties,Breed breed) { super(properties);this.breed=breed; }
    public static ChickenItem bird(ItemStack stack) { return stack.getItem() instanceof ChickenItem c?c:null; }
    public static CompoundTag data(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag(); }
    public static int stat(ItemStack stack,String key) { return Math.clamp(data(stack).getIntOr(key,1),1,10); }
    /** Entity runtime state is not part of a portable chicken's identity. */
    private static final java.util.Set<String> TRANSIENT=java.util.Set.of(
        "UUID","leash","Leash","Pos","Motion","Rotation","FallDistance","OnGround","Air",
        "PortalCooldown","LayTime","EggLayTime","Brain","InLove","LoveCause","ForcedAge",
        "HurtTime","HurtByTimestamp","DeathTime","Fire","TicksFrozen","FallFlying","LeftHanded",
        "Analyzed","invulnerable_time","last_hurt_by_player","last_hurt_by_player_memory_time",
        "last_hurt_by_mob","ticks_since_last_hurt_by_mob","current_impulse_context",
        "current_impulse_context_reset_grace_time");
    public static boolean normalize(ItemStack stack) {
        var bird=bird(stack);if(bird==null)return false;
        var tag=data(stack);
        if(tag.getIntOr("RoostDataVersion",0)==1)return false;
        for(String key:TRANSIENT)tag.remove(key);
        // Keep baby age, but discard an adult's temporary breeding cooldown.
        if(tag.getIntOr("Age",0)>=0)tag.remove("Age");
        if(bird.breed!=null) {
            for(String key:java.util.List.of("Growth","Gain","Strength"))tag.putInt(key,Math.clamp(tag.getIntOr(key,1),1,10));
            // Resource breed textures override vanilla chicken appearance variants.
            tag.remove("variant");
        }
        tag.putInt("RoostDataVersion",1);
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        return true;
    }
    @Override public void inventoryTick(ItemStack stack,ServerLevel level,net.minecraft.world.entity.Entity entity,net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack,level,entity,slot);
        if(normalize(stack) && entity instanceof net.minecraft.world.entity.player.Player player)player.getInventory().setChanged();
    }
    public static ItemStack capture(Chicken chicken,ServerLevel level) {
        var item=chicken instanceof ResourceChicken c?RoostMod.BIRDS.get(c.breed().id):RoostMod.BIRDS.get("vanilla");
        ItemStack stack=new ItemStack(item);
        var out=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());
        chicken.saveWithoutId(out);var tag=out.buildResult();
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        if(chicken.hasCustomName())stack.set(DataComponents.CUSTOM_NAME,chicken.getCustomName());
        normalize(stack);
        return stack;
    }
    public Chicken entity(ItemStack stack,ServerLevel level) {
        Chicken c=breed==null?EntityTypes.CHICKEN.create(level,EntitySpawnReason.TRIGGERED):breed.entityType.create(level,EntitySpawnReason.TRIGGERED);
        if(c==null)return null;
        c.load(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),data(stack)));
        if(c instanceof ResourceChicken r)r.stats(stat(stack,"Growth"),stat(stack,"Gain"),stat(stack,"Strength"));
        return c;
    }
    public int layTime(ItemStack stack,net.minecraft.util.RandomSource random) {
        double min=6000.0*(breed==null?1:breed.tier()*breed.layCoefficient);
        return (int)Math.clamp(min*(1+random.nextDouble()),1,200000000);
    }
    public ItemStack production(ItemStack stack,net.minecraft.util.RandomSource random) {
        return breed==null?new ItemStack(random.nextInt(3)==0?Items.FEATHER:Items.EGG):breed.lay(stat(stack,"Gain"));
    }
    public static ItemStack child(ItemStack left,ItemStack right,ServerLevel level) {
        var a=bird(left);var b=bird(right);if(a==null||b==null)return ItemStack.EMPTY;
        if(a.breed==null || b.breed==null)return a.breed==b.breed?new ItemStack(RoostMod.BIRDS.get("vanilla")):ItemStack.EMPTY;
        var p=(ResourceChicken)a.entity(left,level);var q=(ResourceChicken)b.entity(right,level);
        var child=p==null||q==null?null:p.getBreedOffspring(level,q);
        return child==null?ItemStack.EMPTY:capture(child,level);
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level=context.getLevel();var pos=context.getClickedPos();
        if(level.getBlockEntity(pos) instanceof Machine machine && machine.kind()!=2) {
            if(!level.mayInteract(context.getPlayer(),pos))return InteractionResult.FAIL;
            if(!level.isClientSide()) {
                ItemStack copy=context.getItemInHand().copy();int accepted=machine.insertBird(copy);
                if(accepted==0)return InteractionResult.FAIL;
                // A contained chicken is always transferred, including in Creative, to avoid copying named entities.
                context.getItemInHand().shrink(accepted);
            }
            return InteractionResult.SUCCESS;
        }
        var target=pos.relative(context.getClickedFace());
        if(context.getPlayer()==null || !context.getPlayer().mayUseItemAt(target,context.getClickedFace(),context.getItemInHand()) || !level.mayInteract(context.getPlayer(),pos))return InteractionResult.FAIL;
        if(level instanceof ServerLevel server) {
            var chicken=entity(context.getItemInHand(),server);if(chicken==null)return InteractionResult.FAIL;
            chicken.setPos(target.getX()+.5,target.getY(),target.getZ()+.5);
            if(!server.noCollision(chicken) || !server.addFreshEntity(chicken))return InteractionResult.FAIL;
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
