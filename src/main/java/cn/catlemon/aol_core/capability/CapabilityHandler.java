package cn.catlemon.aol_core.capability;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.AoLEventLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class CapabilityHandler {
	public final static String tagSkillPoint = AoLCore.MODID + ":skillPoint";
	public final static String tagSkillTree = AoLCore.MODID + ":skillTree";
	
	@CapabilityInject(ISkillPoint.class)
	public static Capability<ISkillPoint> capSkillPoint;
	
	@CapabilityInject(ISkillTree.class)
	public static Capability<ISkillTree> capSkillTree;
	
	@SuppressWarnings("deprecation")
	public CapabilityHandler(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(ISkillPoint.class, new CapabilitySkillPoint.Storage(),
				CapabilitySkillPoint.Implementation.class);
		CapabilityManager.INSTANCE.register(ISkillTree.class, new CapabilitySkillTree.Storage(),
				CapabilitySkillTree.Implementation.class);
		AoLEventLoader.AOL_EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onCapabilitySkillPointInitialize(AoLEventLoader.CapabilitySkillPointInitializeEvent event) {
		String[] defaultSPType = new String[] { AoLCore.MODID + ".normal", AoLCore.MODID + ".special" };
		event.addDefaultSPType(defaultSPType);
	}
}
