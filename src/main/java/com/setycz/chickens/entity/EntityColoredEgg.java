package com.setycz.chickens.entity;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.registry.ChickensRegistry;
import com.setycz.chickens.registry.ChickensRegistryItem;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by setyc on 13.02.2016.
 */
public class EntityColoredEgg extends EntityThrowable {
    private static final DataParameter<String> CHICKEN_TYPE = EntityDataManager.createKey(EntityColoredEgg.class, DataSerializers.STRING);
    public static final String TYPE_NBT = "Type";
    
    private static ItemStack itemstack = ItemStack.EMPTY;

    public EntityColoredEgg(World worldIn) {
        super(worldIn);
    }

    public EntityColoredEgg(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityColoredEgg(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public void setChickenType(String type) {
        this.dataManager.set(CHICKEN_TYPE, type);
    }

    private String getChickenType() {
        return this.dataManager.get(CHICKEN_TYPE);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CHICKEN_TYPE, "");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setString(TYPE_NBT, getChickenType());
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        setChickenType(tagCompound.getString(TYPE_NBT));
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (!this.world.isRemote) {
            
            if (this.rand.nextInt(8) == 0)
            {
            	int i = 1;

            	if (this.rand.nextInt(32) == 0) {
            		i = 4;
            	}

            	for (int j = 0; j < i; ++j) {
            		EntityChickensChicken entityChicken = new EntityChickensChicken(this.world);
            		entityChicken.setChickenType(getChickenType());
            		entityChicken.setGrowingAge(-24000);
            		entityChicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            		this.world.spawnEntity(entityChicken);
            	}
            }


            this.world.setEntityState(this, (byte)3);
            this.setDead();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 3)
        {
            double d0 = 0.08D;

            for (int i = 0; i < 8; ++i)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(Items.EGG));
            }
        }
    }
    
	public ItemStack getItemStacktoRender() {
			ChickensRegistryItem registry = ChickensRegistry.getByRegistryName(getChickenType());
			itemstack = new ItemStack(ChickensMod.coloredEgg, 1, registry.getDyeMetadata());
		return itemstack;
	}

}
