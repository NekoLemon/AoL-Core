package cn.catlemon.aol_core.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "aol_core")
public final class AoLConfigChangedHandler {
	@SubscribeEvent
	public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals("aol_core")) {
			ConfigManager.sync("aol_core", Config.Type.INSTANCE);
			AoLHandler.changeHandlers();
		}
	}
}
