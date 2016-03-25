package com.setycz.chickens.waila;

/**
 * Created by setyc on 20.02.2016.
 */
/*
// TODO: Waila not ported yet
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
        currenttip.add(I18n.translateToLocalFormatted("entity.ChickensChicken.tier", chicken.getTier()));
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
*/