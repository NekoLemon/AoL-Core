package cn.catlemon.aol_core.skill;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.Coordinate;
import cn.catlemon.aol_core.api.SkillBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillExample2 extends SkillBase {
	
	public SkillExample2() {
		super(AoLCore.MODID + ".example2");
		skillPointRequirement.put(AoLCore.MODID + ".normal", 1);
		skillPointRequirement.put(AoLCore.MODID + ".special", 1);
		skillDependencies.add(AoLCore.MODID + ".example");
		skillCor = new Coordinate<Integer>(1, 1);
		learnLevel = 4;
	}
	
	@SubscribeEvent
	public void test(AoLEventLoader.TestEvent event) {
		event.sender.sendMessage(new TextComponentString("收到信号2"));
	}
	
}
