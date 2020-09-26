package cn.catlemon.aol_core.skill;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillExample2 extends SkillBase {
	
	public SkillExample2() {
		_skillID = AoLCore.MODID + ".example2";
		_skillDependencies.add(AoLCore.MODID + ".example");
	}
	
	@SubscribeEvent
	public void test(AoLEventLoader.TestEvent event) {
		event.sender.sendMessage(new TextComponentString("收到信号2"));
	}
	
}
