package cn.catlemon.aol_core.capability;

import cn.catlemon.aol_core.api.ISkillPoint;
import cn.catlemon.aol_core.capability.CapabilitySkillPoint.Implementation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CapabilityHandler {
	public final static String tagSkillPoint="skillPoint";
	
	@CapabilityInject(ISkillPoint.class)
	public static Capability<ISkillPoint> capSkillPoint;

	@SuppressWarnings("deprecation")
	public CapabilityHandler(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(ISkillPoint.class, new CapabilitySkillPoint.Storage(),
				Implementation.class);
	}
}
