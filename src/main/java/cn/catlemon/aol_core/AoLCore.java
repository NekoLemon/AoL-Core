package cn.catlemon.aol_core;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.catlemon.aol_core.advancement.TriggerHandler;
import cn.catlemon.aol_core.api.Coordinate;
import cn.catlemon.aol_core.api.PackedResourceLocation;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.command.CommandHandler;
import cn.catlemon.aol_core.event.AoLEventHandler;
import cn.catlemon.aol_core.gui.GuiHandler;
import cn.catlemon.aol_core.keybind.KeybindHandler;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.proxy.IProxy;
import cn.catlemon.aol_core.skill.AoLSkillPageHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = AoLCore.MODID, name = AoLCore.NAME, version = AoLCore.VERSION, guiFactory = "cn.catlemon.aol_core.config.AoLCoreConfigGui")
public class AoLCore {
	public static final String MODID = "aol_core";
	public static final String NAME = "Adventure of Lemon";
	public static final String VERSION = "0.0.1";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	@Instance(MODID)
	public static AoLCore instance;
	
	@SidedProxy(clientSide = "cn.catlemon.aol_core.proxy.ClientProxy", serverSide = "cn.catlemon.aol_core.proxy.ServerProxy")
	public static IProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		new AoLResourceLocation("test");
		new AoLSkillPageHandler(event);
		new CapabilityHandler(event);
		new NetworkHandler(event);
		new TriggerHandler(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		new AoLEventHandler(event);
		new GuiHandler(event);
		if (event.getSide().isClient()) {
			new KeybindHandler(event);
		}
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		new CommandHandler(event);
	}
	
	public static class AoLResourceLocation extends PackedResourceLocation {
		
		public AoLResourceLocation(String location) {
			super(MODID, location);
		}
		
		public AoLResourceLocation(String location, @Nonnull Coordinate<Integer> offset) {
			super(MODID, location, offset);
		}
		
		public AoLResourceLocation(String location, @Nonnull Coordinate<Integer> offset, @Nonnull Integer width,
				@Nonnull Integer height) {
			super(MODID, location, offset, width, height);
		}
		
		public AoLResourceLocation(String location, @Nonnull Coordinate<Integer> offset, @Nonnull Integer width,
				@Nonnull Integer height, @Nonnull Integer canvasWidth, @Nonnull Integer canvasHeight) {
			super(MODID, location, offset, width, height, canvasWidth, canvasHeight);
		}
		
	}
}
