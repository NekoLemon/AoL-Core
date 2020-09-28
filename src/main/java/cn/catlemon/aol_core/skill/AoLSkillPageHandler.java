package cn.catlemon.aol_core.skill;

import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillTreePage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AoLSkillPageHandler {
	public static SkillTreePage pageExample = new SkillTreePage("aol_core.example");
	public static SkillTreePage pageExample2 = new SkillTreePage("aol_core.example2");
	public static SkillTreePage pageExample3 = new SkillTreePage("aol_core.example3");
	public static SkillTreePage pageExample4 = new SkillTreePage("aol_core.example4");
	public static SkillTreePage pageExample5 = new SkillTreePage("aol_core.example5");
	public static SkillTreePage pageExample6 = new SkillTreePage("aol_core.example6");
	
	public AoLSkillPageHandler(FMLPreInitializationEvent event) {
		AoLEventLoader.AOL_EVENT_BUS.register(this);
		pageExample.addSkill(new SkillExample());
		pageExample.addSkill(new SkillExample2());
	}
	
	@SubscribeEvent
	public void onSkillTreeInitialize(AoLEventLoader.SkillTreeInitializeEvent event) {
		event.addPage(pageExample);
		event.addPage(pageExample2);
		event.addPage(pageExample3);
		event.addPage(pageExample4);
		event.addPage(pageExample5);
		event.addPage(pageExample6);
	}
}
