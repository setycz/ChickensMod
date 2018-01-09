package com.setycz.chickens.top;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.chicken.EntityChickensChicken;

import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TheOneProbePlugin implements IProbeInfoEntityProvider  {

	@Override
	public String getID() {

		return ChickensMod.MODID +":chickenstop";
	}

	@Override
	public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) 
	{
		
		if(entity instanceof EntityChickensChicken) {
			EntityChickensChicken chicken = (EntityChickensChicken) entity;
			
			probeInfo.text(translate("entity.ChickensChicken.top.tier") +" "+ chicken.getTier());
			
	        if (chicken.getStatsAnalyzed() || ChickensMod.instance.getAlwaysShowStats()) {
	        	probeInfo.text(translate("entity.ChickensChicken.top.growth") +" "+ chicken.getGrowth());
	        	probeInfo.text(translate("entity.ChickensChicken.top.gain") +" "+ chicken.getGain());
	        	probeInfo.text(translate("entity.ChickensChicken.top.strength") +" "+ chicken.getStrength());
			}
	        
	        if (!chicken.isChild()) {
	            int layProgress = chicken.getLayProgress();
	            if (layProgress <= 0) {
	            	probeInfo.text(translate("entity.ChickensChicken.top.nextEggSoon"));
	            } else {
	            	probeInfo.text(translate("entity.ChickensChicken.top.layProgress") +" "+layProgress + translate("entity.ChickensChicken.top.layProgressEnd"));
	            }
	        }
		}
		
	}
	
	
	public static String translate(String text) {
		return IProbeInfo.STARTLOC + text + IProbeInfo.ENDLOC;
	}
		
	public static class GetTheOneProbe implements Function<ITheOneProbe, Void> 
	{

		public static ITheOneProbe theOneProbe;

		@Nullable
		@Override
		public Void apply (ITheOneProbe input) {

			theOneProbe = input;
			theOneProbe.registerEntityProvider(new TheOneProbePlugin());
			return null;
		}
	}
}
