package cn.catlemon.aol_core.skill;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillExample extends SkillBase {

	public SkillExample() {
		_skillID="aol_core.example";		
	}
	
	@SubscribeEvent
	public void test(AoLEventLoader.TestEvent event) {
		event.sender.sendMessage(new TextComponentString("收到信号"));
	}

}
