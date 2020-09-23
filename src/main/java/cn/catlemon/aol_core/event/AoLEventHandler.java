package cn.catlemon.aol_core.event;

import cn.catlemon.aol_core.config.AoLCoreConfig;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public final class AoLEventHandler {

	public AoLEventHandler(FMLInitializationEvent event) {
		AoLEventHandler.loadUnreloadableHandlers();
		AoLEventHandler.reloadHandlers();
	}

	private static HPOverhaulHandler _AoLHPRelatedHandler;

	public static void loadUnreloadableHandlers() {
		//
	}

	public static void reloadHandlers() {
		if (AoLCoreConfig.MainConfig.mechanics.regenerationOverhaul) {
			if (_AoLHPRelatedHandler == null) {
				_AoLHPRelatedHandler = new HPOverhaulHandler();
			}
		} else {
			if (_AoLHPRelatedHandler != null) {
				_AoLHPRelatedHandler.finalize();
			}
		}
	}
}
