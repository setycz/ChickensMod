package com.setycz.chickens.waila;

import com.setycz.chickens.chicken.EntityChickensChicken;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by setyc on 20.02.2016.
 */
public class ChickensEntityProvider implements IWailaEntityProvider {

    private static final ChickensEntityProvider INSTANCE = new ChickensEntityProvider();

    public static void load(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(INSTANCE, EntityChickensChicken.class);
    }

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        EntityChickensChicken chicken = (EntityChickensChicken)entity;
        currenttip.add(StatCollector.translateToLocalFormatted("entity.ChickensChicken.tier", chicken.getTier() + 1));
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        return null;
    }
}
