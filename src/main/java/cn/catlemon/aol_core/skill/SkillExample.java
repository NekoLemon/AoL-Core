package cn.catlemon.aol_core.skill;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.Coordinate;
import cn.catlemon.aol_core.api.SkillBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillExample extends SkillBase {
	
	public SkillExample() {
		super(AoLCore.MODID + ".example");
		skillPointRequirement.put(AoLCore.MODID + ".normal", 1);
		skillCor = new Coordinate<Integer>(0);
		skillIcon = new AoLCore.AoLResourceLocation("textures/skill/example.png");
	}
	
	@SubscribeEvent
	public void test(AoLEventLoader.TestEvent event) {
		event.sender.sendMessage(new TextComponentString("收到信号"));
	}
	
}
