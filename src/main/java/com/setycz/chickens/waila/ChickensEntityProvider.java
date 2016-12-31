package com.setycz.chickens.waila;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.chicken.EntityChickensChicken;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by setyc on 20.02.2016.
 */
@SuppressWarnings("unused")
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
        EntityChickensChicken chicken = (EntityChickensChicken) entity;

        currenttip.add(I18n.translateToLocalFormatted("entity.ChickensChicken.tier", chicken.getTier()));

        if (chicken.getStatsAnalyzed() || ChickensMod.instance.getAlwaysShowStats()) {
            currenttip.add(I18n.translateToLocalFormatted("entity.ChickensChicken.growth", chicken.getGrowth()));
            currenttip.add(I18n.translateToLocalFormatted("entity.ChickensChicken.gain", chicken.getGain()));
            currenttip.add(I18n.translateToLocalFormatted("entity.ChickensChicken.strength", chicken.getStrength()));
        }

        if (!chicken.isChild()) {
            int layProgress = chicken.getLayProgress();
            if (layProgress <= 0) {
                currenttip.add(I18n.translateToLocal("entity.ChickensChicken.nextEggSoon"));
            } else {
                currenttip.add(I18n.translateToLocalFormatted("entity.ChickensChicken.layProgress", layProgress));
            }
        }

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