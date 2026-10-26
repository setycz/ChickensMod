package com.setycz.chickens;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;
import net.minecraft.world.phys.Vec3;

/** 0 = hay; 1 = compost dirt; 2..10 = collected production. */
public final class HenhouseBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> items=NonNullList.withSize(27,ItemStack.EMPTY);
    private int energy;
    public HenhouseBlockEntity(BlockPos pos,BlockState state) { super(ChickensMod.HENHOUSE_ENTITY,pos,state); }
    @Override protected Component getDefaultName() { return Component.literal("Henhouse — Hay | Dirt | Output"); }
    @Override public int getContainerSize() { return 27; }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> value) { items=value; }
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inv) { return new HenhouseMenu(id,inv,this); }
    @Override public boolean canPlaceItem(int slot,ItemStack s) { return slot==0 && s.is(Items.HAY_BLOCK); }
    @Override public int[] getSlotsForFace(Direction side) { return side==Direction.UP ? new int[]{0} : java.util.stream.IntStream.rangeClosed(1,10).toArray(); }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack s,Direction side) { return side==Direction.UP && canPlaceItem(slot,s); }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack s,Direction side) { return side!=Direction.UP && slot>=1 && slot<=10; }
    public int energy() { return energy; }
    public ItemStack collect(ItemStack input) {
        ItemStack rest=input.copy();
        for(int slot=2;slot<=10 && !rest.isEmpty();slot++) {
            ItemStack at=items.get(slot);
            if(!at.isEmpty() && !ItemStack.isSameItemSameComponents(at,rest)) continue;
            int room=rest.getMaxStackSize()-(at.isEmpty()?0:at.getCount());
            while(room>0 && !rest.isEmpty()) {
                if(energy==0) {
                    ItemStack hay=items.get(0),dirt=items.get(1);
                    if(!hay.is(Items.HAY_BLOCK) || !dirt.isEmpty() && (!dirt.is(Items.DIRT) || dirt.getCount()>=dirt.getMaxStackSize())) return rest;
                    hay.shrink(1);energy=100;
                }
                int amount=Math.min(Math.min(room,rest.getCount()),energy);
                if(at.isEmpty()) { at=rest.split(amount);items.set(slot,at); } else { at.grow(amount);rest.shrink(amount); }
                room-=amount;energy-=amount;
                if(energy==0) { if(items.get(1).isEmpty()) items.set(1,new ItemStack(Items.DIRT)); else items.get(1).grow(1); }
                setChanged();
            }
        }
        return rest;
    }
    public static ItemStack collect(ServerLevel level,Vec3 pos,ItemStack input) {
        // Look at existing loaded chunks only. Never load terrain for collection.
        List<HenhouseBlockEntity> houses=new ArrayList<>();
        int cx=(int)Math.floor(pos.x)>>4,cz=(int)Math.floor(pos.z)>>4;
        for(int x=cx-1;x<=cx+1;x++) for(int z=cz-1;z<=cz+1;z++) {
            var chunk=level.getChunkSource().getChunkNow(x,z);if(chunk==null) continue;
            for(var be:chunk.getBlockEntities().values()) if(be instanceof HenhouseBlockEntity h) {
                var p=h.getBlockPos();if(Math.abs(p.getX()+.5-pos.x)<=5 && Math.abs(p.getY()+.5-pos.y)<=5 && Math.abs(p.getZ()+.5-pos.z)<=5) houses.add(h);
            }
        }
        houses.sort(Comparator.comparingDouble(h->h.getBlockPos().distToCenterSqr(pos)));
        ItemStack rest=input.copy();for(var h:houses) { rest=h.collect(rest);if(rest.isEmpty()) break; }return rest;
    }
    @Override protected void saveAdditional(ValueOutput out) { super.saveAdditional(out);out.putInt("Energy",energy);ContainerHelper.saveAllItems(out,items); }
    @Override protected void loadAdditional(ValueInput in) { super.loadAdditional(in);items=NonNullList.withSize(27,ItemStack.EMPTY);ContainerHelper.loadAllItems(in,items);energy=Math.clamp(in.getIntOr("Energy",0),0,100); }
}
