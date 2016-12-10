package com.setycz.chickens.chicken;

import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.SpawnType;
import com.setycz.chickens.henhouse.TileEntityHenhouse;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
public class EntityChickensChicken extends EntityChicken {
    private static final DataParameter<Integer> CHICKEN_TYPE = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.VARINT);
    public static final String TYPE_NBT = "Type";

    public EntityChickensChicken(World worldIn) {
        super(worldIn);
    }

    public ResourceLocation getTexture() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        return chickenDescription.getTexture();
    }

    private ChickensRegistryItem getChickenDescription() {
        return ChickensRegistry.getByType(getChickenTypeInternal());
    }

    public int getTier() {
        return getChickenDescription().getTier();
    }

    @Override
    public String getName() {
        if (this.hasCustomName()) {
            return getCustomNameTag();
        }

        ChickensRegistryItem chickenDescription = getChickenDescription();
        return I18n.translateToLocal("entity." + chickenDescription.getEntityName() + ".name");
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        ChickensRegistryItem mateChickenDescription = ((EntityChickensChicken) ageable).getChickenDescription();

        ChickensRegistryItem childToBeBorn = ChickensRegistry.getRandomChild(chickenDescription, mateChickenDescription);
        if (childToBeBorn == null) {
            return null;
        }

        EntityChickensChicken newChicken = new EntityChickensChicken(this.worldObj);
        newChicken.setChickenType(childToBeBorn.getId());
        return newChicken;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 1) {
            ChickensRegistryItem chickenDescription = getChickenDescription();
            ItemStack itemToLay = chickenDescription.createLayItem();

            itemToLay = TileEntityHenhouse.pushItemStack(itemToLay, worldObj, new Vec3d(posX, posY, posZ));

            if (itemToLay != null) {
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                this.entityDropItem(chickenDescription.createLayItem(), 0);
            }

            resetTimeUntilNextEgg();
        }
        super.onLivingUpdate();
    }

    private void resetTimeUntilNextEgg() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        this.timeUntilNextEgg = (chickenDescription.getMinLayTime()
                + rand.nextInt(chickenDescription.getMaxLayTime() - chickenDescription.getMinLayTime())) * 2;
    }

    @Override
    public boolean getCanSpawnHere() {
        boolean anyInNether = ChickensRegistry.isAnyIn(SpawnType.HELL);
        boolean anyInOverworld = ChickensRegistry.isAnyIn(SpawnType.NORMAL) || ChickensRegistry.isAnyIn(SpawnType.SNOW);
        Biome biome = worldObj.getBiomeForCoordsBody(getPosition());
        return anyInNether && biome == Biomes.HELL || anyInOverworld && super.getCanSpawnHere();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData) {
        livingData = super.onInitialSpawn(difficulty, livingData);
        if (livingData instanceof GroupData) {
            GroupData groupData = (GroupData) livingData;
            setChickenType(groupData.getType());
        } else {
            SpawnType spawnType = getSpawnType();
            List<ChickensRegistryItem> possibleChickens = ChickensRegistry.getPossibleChickensToSpawn(spawnType);
            if (possibleChickens.size() > 0) {
                ChickensRegistryItem chickenToSpawn = possibleChickens.get(rand.nextInt(possibleChickens.size()));

                int type = chickenToSpawn.getId();
                setChickenType(type);
                livingData = new GroupData(type);
            }
        }

        if (rand.nextInt(5) == 0) {
            setGrowingAge(-24000);
        }

        return livingData;
    }

    private SpawnType getSpawnType() {
        Biome biome = worldObj.getBiomeForCoordsBody(getPosition());
        return ChickensRegistry.getSpawnType(biome);
    }

    private static class GroupData implements IEntityLivingData {
        private final int type;

        public GroupData(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public void setChickenType(int type) {
        setChickenTypeInternal(type);
        isImmuneToFire = getChickenDescription().isImmuneToFire();
        resetTimeUntilNextEgg();
    }

    private void setChickenTypeInternal(int type) {
        this.dataManager.set(CHICKEN_TYPE, type);
    }

    private int getChickenTypeInternal() {
        return this.dataManager.get(CHICKEN_TYPE);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(CHICKEN_TYPE, 0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompund) {
        super.writeEntityToNBT(tagCompund);
        tagCompund.setInteger(TYPE_NBT, getChickenTypeInternal());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        setChickenTypeInternal(tagCompund.getInteger(TYPE_NBT));
    }

    @Override
    public int getTalkInterval() {
        return 20 * 60;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        if (this.rand.nextFloat() > 0.1) {
            return;
        }
        super.playStepSound(pos, blockIn);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        ItemStack itemsToDrop = getChickenDescription().createDropItem();
        int count = 1 + rand.nextInt(1 + lootingModifier);
        itemsToDrop.stackSize *= count;
        entityDropItem(itemsToDrop, 0);

        if (this.isBurning()) {
            this.dropItem(Items.COOKED_CHICKEN, 1);
        } else {
            this.dropItem(Items.CHICKEN, 1);
        }
    }
}
