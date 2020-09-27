package cn.catlemon.aol_core.config;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.event.AoLEventHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AoLCore.MODID)
public final class ConfigChangedHandler {
	@SubscribeEvent
	public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(AoLCore.MODID)) {
			ConfigManager.sync(AoLCore.MODID, Config.Type.INSTANCE);
			AoLEventHandler.reloadHandlers();
		}
	}
}
