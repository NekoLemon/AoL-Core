package cn.catlemon.aol_core.skill;

import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillTreePage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AoLSkillPageHandler {
	public static SkillTreePage pageExample=new SkillTreePage("aol_core.example");
	public AoLSkillPageHandler(FMLPreInitializationEvent event) {
		AoLEventLoader.AOL_EVENT_BUS.register(this);
		pageExample.addSkill(new SkillExample());
	}
	
	@SubscribeEvent
	public void onSkillTreeInitialize(AoLEventLoader.SkillTreeInitializeEvent event) {
		event.addPage(pageExample);
	}
}
