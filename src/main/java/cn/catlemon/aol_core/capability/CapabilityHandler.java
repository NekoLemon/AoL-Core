package cn.catlemon.aol_core.capability;

import cn.catlemon.aol_core.AoLCore;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class CapabilityHandler {
	public final static String tagSkillPoint=AoLCore.MODID+":skillPoint";
	
	@CapabilityInject(ISkillPoint.class)
	public static Capability<ISkillPoint> capSkillPoint;

	@SuppressWarnings("deprecation")
	public CapabilityHandler(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(ISkillPoint.class, new CapabilitySkillPoint.Storage(),
				CapabilitySkillPoint.Implementation.class);
	}
}
