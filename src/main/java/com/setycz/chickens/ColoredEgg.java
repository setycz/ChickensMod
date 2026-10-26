package com.setycz.chickens;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
public final class ColoredEgg extends ThrownEgg {
 public ColoredEgg(EntityType<? extends ThrownEgg> type,Level level){super(type,level);}
 @Override protected void onHit(HitResult hit){
  // The thrown stack identifies the breed, so it also survives save/reload.
  if(level() instanceof ServerLevel server){
   Breed b=ChickensMod.COLORED_EGGS.get(getItem().getItem());
   if(b!=null && b.enabled() && random.nextInt(8)==0){
    int count=random.nextInt(32)==0?4:1;
    for(int i=0;i<count;i++){
     var c=b.entityType.create(server,EntitySpawnReason.TRIGGERED);
     if(c!=null){c.setBaby(true);c.setPos(getX(),getY(),getZ());server.addFreshEntity(c);}
    }
   }
   server.broadcastEntityEvent(this,(byte)3);discard();
  }
 }
}
