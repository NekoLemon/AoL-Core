package cn.catlemon.aol_core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.event.AoLEventHandler;
import cn.catlemon.aol_core.event.CommandHandler;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.skill.AoLSkillPageHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = AoLCore.MODID, name = AoLCore.NAME, version = AoLCore.VERSION, guiFactory = "cn.catlemon.aol_core.config.AoLCoreConfigGUI")
public class AoLCore {
	public static final String MODID = "aol_core";
	public static final String NAME = "Adventure of Lemon";
	public static final String VERSION = "0.0.1";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		new AoLSkillPageHandler(event);
		new CapabilityHandler(event);
		new NetworkHandler(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		new AoLEventHandler(event);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		new CommandHandler(event);
	}
}
