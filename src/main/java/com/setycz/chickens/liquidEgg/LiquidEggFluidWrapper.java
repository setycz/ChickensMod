package com.setycz.chickens.liquidEgg;

import com.setycz.chickens.LiquidEggRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * Created by setyc on 13.12.2016.
 */
@SuppressWarnings("WeakerAccess")
public class LiquidEggFluidWrapper implements IFluidHandler, ICapabilityProvider {

    private final ItemStack container;

    public LiquidEggFluidWrapper(ItemStack container) {
        this.container = container;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new FluidTankProperties[]{new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME)};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack fluidStack = getFluid();
        if (!resource.isFluidEqual(fluidStack)) {
            return null;
        }

        return drain(resource.amount, doDrain);
    }

    private FluidStack getFluid() {
        Fluid fluid = LiquidEggRegistry.findById(container.getMetadata()).getFluid();
        return new FluidStack(fluid, Fluid.BUCKET_VOLUME);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (container.stackSize < 1 || maxDrain < Fluid.BUCKET_VOLUME) {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (doDrain) {
            container.stackSize--;
        }
        return fluidStack;
    }
}
