package com.setycz.chickens.coloredEgg;

import com.setycz.chickens.chicken.EntityChickensChicken;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Created by setyc on 13.02.2016.
 */
@SuppressWarnings("WeakerAccess")
public class EntityColoredEgg extends EntityEgg {
    private static final DataParameter<Integer> CHICKEN_TYPE = EntityDataManager.createKey(EntityColoredEgg.class, DataSerializers.VARINT);
    public static final String TYPE_NBT = "Type";

    public EntityColoredEgg(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityColoredEgg(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public void setChickenType(int type) {
        this.dataManager.set(CHICKEN_TYPE, type);
    }

    private int getChickenType() {
        return this.dataManager.get(CHICKEN_TYPE);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CHICKEN_TYPE, 0);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger(TYPE_NBT, getChickenType());
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        setChickenType(tagCompound.getInteger(TYPE_NBT));
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
            int i = 1;

            if (this.rand.nextInt(32) == 0) {
                i = 4;
            }

            for (int j = 0; j < i; ++j) {
                EntityChickensChicken entityChicken = new EntityChickensChicken(this.worldObj);
                entityChicken.setChickenType(getChickenType());
                entityChicken.setGrowingAge(-24000);
                entityChicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                this.worldObj.spawnEntityInWorld(entityChicken);
            }
        }

        for (int k = 0; k < 8; ++k) {
            this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(Items.EGG));
        }

        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }
}
