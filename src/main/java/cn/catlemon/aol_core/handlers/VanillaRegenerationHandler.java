package cn.catlemon.aol_core.handlers;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import cn.catlemon.aol_core.config.AoLCoreConfig;

@Mod.EventBusSubscriber(modid = "aol_core")
public final class VanillaRegenerationHandler {
	@SubscribeEvent
	public static void switchVanillaRegeneration(PlayerEvent.PlayerLoggedInEvent event) {
		event.player.getEntityWorld().getGameRules().setOrCreateGameRule("naturalRegeneration",
				(AoLCoreConfig.MainConfig.mechanics.regenerationOverhaul ? "false" : "true"));
	}
}
