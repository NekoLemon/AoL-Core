package cn.catlemon.aol_core.handlers;

import cn.catlemon.aol_core.config.AoLCoreConfig;

public final class AoLHandler {

	private static AoLHPRelatedHandler _AoLHPRelatedHandler;

	public static void startHandlers() {
		createHandlersOnce();
		changeHandlers();
	}

	public static void createHandlersOnce() {
		//
	}

	public static void changeHandlers() {
		if (AoLCoreConfig.MainConfig.mechanics.regenerationOverhaul) {
			if (_AoLHPRelatedHandler == null) {
				_AoLHPRelatedHandler = new AoLHPRelatedHandler();
			}
		} else {
			if (_AoLHPRelatedHandler != null) {
				_AoLHPRelatedHandler.finalize();
			}
		}
	}
}
