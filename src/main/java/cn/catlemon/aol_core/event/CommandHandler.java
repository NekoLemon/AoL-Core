package cn.catlemon.aol_core.event;

import cn.catlemon.aol_core.command.*;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommandHandler {

	public CommandHandler(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSkill());
		event.registerServerCommand(new CommandSkillPoint());		
	}
}
