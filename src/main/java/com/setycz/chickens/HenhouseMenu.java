package com.setycz.chickens;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;

public final class HenhouseMenu extends AbstractContainerMenu {
    public static final int HOUSE_SLOTS=11;
    private final Container house;
    private final ContainerData fuel;

    public HenhouseMenu(int id,Inventory player) {
        this(id,player,new SimpleContainer(HOUSE_SLOTS),new SimpleContainerData(1));
    }
    public HenhouseMenu(int id,Inventory player,HenhouseBlockEntity house) {
        this(id,player,house,new ContainerData() {
            public int get(int index) { return index==0?house.energy():0; }
            public void set(int index,int value) { /* Server fuel is changed only by collection. */ }
            public int getCount() { return 1; }
        });
    }
    private HenhouseMenu(int id,Inventory player,Container house,ContainerData fuel) {
        super(ChickensMod.HENHOUSE_MENU,id);
        checkContainerSize(house,HOUSE_SLOTS);checkContainerDataCount(fuel,1);
        this.house=house;this.fuel=fuel;house.startOpen(player.player);
        addSlot(new Slot(house,0,25,19) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.is(Items.HAY_BLOCK); }
        });
        addSlot(outputSlot(house,1,25,55));
        for(int row=0;row<3;row++) for(int col=0;col<3;col++)
            addSlot(outputSlot(house,2+row*3+col,98+col*18,17+row*18));
        addStandardInventorySlots(player,8,84);
        addDataSlots(fuel);
    }
    private static Slot outputSlot(Container house,int slot,int x,int y) {
        return new Slot(house,slot,x,y) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        };
    }
    public int fuelRemaining() { return Math.clamp(fuel.get(0),0,100); }
    public int fuelPixels() { return fuelRemaining()*57/100; }
    public int hayCount() { return house.getItem(0).is(Items.HAY_BLOCK)?house.getItem(0).getCount():0; }
    public boolean compostFull() { var stack=house.getItem(1);return !stack.isEmpty() && (!stack.is(Items.DIRT)||stack.getCount()>=stack.getMaxStackSize()); }
    @Override public boolean stillValid(Player player) { return house.stillValid(player); }
    @Override public void removed(Player player) { super.removed(player);house.stopOpen(player); }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0 || index>=slots.size())return ItemStack.EMPTY;
        Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        ItemStack source=slot.getItem(),original=source.copy();
        boolean moved;
        if(index<HOUSE_SLOTS) moved=moveItemStackTo(source,HOUSE_SLOTS,slots.size(),true);
        else if(source.is(Items.HAY_BLOCK)) moved=moveItemStackTo(source,0,1,false);
        else if(index<38) moved=moveItemStackTo(source,38,47,false);
        else moved=moveItemStackTo(source,11,38,false);
        if(!moved || source.getCount()==original.getCount())return ItemStack.EMPTY;
        if(source.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();
        slot.onTake(player,source);return original;
    }
}
