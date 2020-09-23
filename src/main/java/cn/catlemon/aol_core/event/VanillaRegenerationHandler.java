package cn.catlemon.aol_core.event;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.config.AoLCoreConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = AoLCore.MODID)
public final class VanillaRegenerationHandler {
	@SubscribeEvent
	public static void switchVanillaRegeneration(PlayerEvent.PlayerLoggedInEvent event) {
		event.player.getEntityWorld().getGameRules().setOrCreateGameRule("naturalRegeneration",
				(AoLCoreConfig.MainConfig.mechanics.regenerationOverhaul ? "false" : "true"));
	}
}
