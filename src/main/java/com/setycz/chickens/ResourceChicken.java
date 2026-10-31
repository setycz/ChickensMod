package com.setycz.chickens;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.*;

public final class ResourceChicken extends Chicken {
    private static final EntityDataAccessor<Integer> GROWTH=SynchedEntityData.defineId(ResourceChicken.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GAIN=SynchedEntityData.defineId(ResourceChicken.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STRENGTH=SynchedEntityData.defineId(ResourceChicken.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ANALYZED=SynchedEntityData.defineId(ResourceChicken.class,EntityDataSerializers.BOOLEAN);
    private int layTime = -1;
    public ResourceChicken(EntityType<? extends Chicken> type,Level level) { super(type,level); }
    public Breed breed() { return Breeds.byType(getType()); }
    public int growth() { return entityData.get(GROWTH); }
    public int gain() { return entityData.get(GAIN); }
    public int strength() { return entityData.get(STRENGTH); }
    public void stats(int g,int a,int s) { entityData.set(GROWTH,Math.clamp(g,1,10)); entityData.set(GAIN,Math.clamp(a,1,10)); entityData.set(STRENGTH,Math.clamp(s,1,10)); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder b) { super.defineSynchedData(b); b.define(GROWTH,1);b.define(GAIN,1);b.define(STRENGTH,1);b.define(ANALYZED,false); }
    @Override protected Component getTypeName() { return Component.literal(breed().name); }
    @Override public boolean fireImmune() { return breed().spawn==Breeds.SpawnType.NETHER || super.fireImmune(); }
    @Override public boolean canMate(Animal other) { return other!=this && other instanceof ResourceChicken && isInLove() && other.isInLove(); }
    @Override public Chicken getBreedOffspring(ServerLevel level,AgeableMob mate) {
        if(!(mate instanceof ResourceChicken p)) return null;
        Breed child=Breeding.choose(breed(),p.breed(),random);
        ResourceChicken c=child.entityType.create(level,EntitySpawnReason.BREEDING);
        if(c==null) return null;
        if(child==breed() && child==p.breed()) {
            c.stats(Breeding.stat(strength(),p.strength(),growth(),p.growth(),1+random.nextInt(2)),
                    Breeding.stat(strength(),p.strength(),gain(),p.gain(),1+random.nextInt(2)),
                    Breeding.stat(strength(),p.strength(),strength(),p.strength(),1+random.nextInt(2)));
        } else if(child==breed()) c.stats(growth(),gain(),strength());
        else if(child==p.breed()) c.stats(p.growth(),p.gain(),p.strength());
        return c;
    }
    @Override public void setAge(int age) {
        if(age==-24000 || age==6000) age=age*(11-growth())/10;
        super.setAge(age);
    }
    private void resetLayTime() {
        int min=(int)Math.clamp(6000.0*breed().tier()*breed().layCoefficient,1,100000000);
        layTime=Math.max(1,(int)((min+random.nextInt(min))*(11-growth())/10.0));
    }
    @Override public void aiStep() {
        // Keep vanilla movement and wings, suppress its separate egg loot table.
        eggTime=2;
        super.aiStep();
        if(!(level() instanceof ServerLevel server) || !isAlive() || isBaby() || isChickenJockey()) return;
        if(layTime<0) resetLayTime();
        if(--layTime<=0) {
            if(breed().enabled()) {
                ItemStack rest=HenhouseBlockEntity.collect(server,position(),breed().lay(gain()));
                if(!rest.isEmpty()) spawnAtLocation(server,rest);
                playSound(SoundEvents.CHICKEN_EGG,1,1);
            }
            resetLayTime();
        }
    }
    @Override public InteractionResult mobInteract(Player player,InteractionHand hand) {
        ItemStack held=player.getItemInHand(hand);
        if(held.is(ChickensMod.ANALYZER)) {
            if(level() instanceof ServerLevel) {
                entityData.set(ANALYZED,true);
                if(breed().parent1!=null && breed().parent2!=null) player.sendSystemMessage(Component.literal("Breeding: "+breed().parent1.name+" + "+breed().parent2.name));
                player.sendSystemMessage(Component.literal(breed().name+" | Growth "+growth()+" | Gain "+gain()+" | Strength "+strength()+" | Next lay: "+Math.max(0,layTime/20)+"s"));
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player,hand);
    }
    @Override protected void dropFromLootTable(ServerLevel server,net.minecraft.world.damagesource.DamageSource source,boolean playerKill) {
        var key=net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE,ChickensMod.id("entities/resource_chicken"));
        dropFromLootTable(server,source,playerKill,key,stack->{
            if(stack.is(Items.FEATHER)) {
                ItemStack replacement=new ItemStack(breed().dropItem==null?breed().layItem:breed().dropItem);
                replacement.setCount(replacement.getCount()*stack.getCount());
                spawnAtLocation(server,replacement);
            } else spawnAtLocation(server,stack);
        });
    }
    @Override protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);out.putInt("Growth",growth());out.putInt("Gain",gain());out.putInt("Strength",strength());out.putInt("LayTime",layTime);out.putBoolean("Analyzed",entityData.get(ANALYZED));
    }
    @Override protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);stats(in.getIntOr("Growth",1),in.getIntOr("Gain",1),in.getIntOr("Strength",1));layTime=Math.clamp(in.getIntOr("LayTime",-1),-1,200000000);entityData.set(ANALYZED,in.getBooleanOr("Analyzed",false));
    }
}
