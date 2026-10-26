package com.setycz.chickens;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
/** Eggs are consumed, so fluid extraction removes the item without exchanging an empty container. */
public final class LiquidEggStorage implements ExtractionOnlyStorage<FluidVariant>,SingleSlotStorage<FluidVariant> {
 private final ContainerItemContext context;
 private final ItemVariant egg;
 private final FluidVariant fluid;
 public LiquidEggStorage(ContainerItemContext context,FluidVariant fluid){this.context=context;this.egg=context.getItemVariant();this.fluid=fluid;}
 @Override public long extract(FluidVariant resource,long maxAmount,TransactionContext tx){
  StoragePreconditions.notBlankNotNegative(resource,maxAmount);
  if(!fluid.equals(resource) || maxAmount<FluidConstants.BUCKET || !egg.equals(context.getItemVariant()))return 0;
  return context.extract(egg,1,tx)==1?FluidConstants.BUCKET:0;
 }
 @Override public boolean isResourceBlank(){return getAmount()==0;}
 @Override public FluidVariant getResource(){return isResourceBlank()?FluidVariant.blank():fluid;}
 @Override public long getAmount(){return egg.equals(context.getItemVariant()) && context.getAmount()>0?FluidConstants.BUCKET:0;}
 @Override public long getCapacity(){return FluidConstants.BUCKET;}
}
