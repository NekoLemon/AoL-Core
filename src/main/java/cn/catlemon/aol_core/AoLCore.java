package cn.catlemon.aol_core;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import cn.catlemon.aol_core.handlers.AoLHandler;

@Mod(modid = AoLCore.MODID, name = AoLCore.NAME, version = AoLCore.VERSION, guiFactory = "cn.catlemon.aol_core.config.AoLCoreConfigGUI")
public class AoLCore {
	public static final String MODID = "aol_core";
	public static final String NAME = "Adventure of Lemon";
	public static final String VERSION = "0.0.1";

	private static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		AoLHandler.startHandlers();;
	}
}
