package cn.catlemon.aol_core.config;

import cn.catlemon.aol_core.AoLCore;
import net.minecraftforge.common.config.Config;

public class AoLCoreConfig {
	
	@Config(modid = AoLCore.MODID, type = Config.Type.INSTANCE, name = "Adventure_of_Lemon/Main")
	@Config.LangKey("gui." + AoLCore.MODID + ".config.title")
	public static class MainConfig {
		
		@Config.Comment("Game Mechanics")
		@Config.LangKey("gui." + AoLCore.MODID + ".config.mechanics")
		public static final SubCategoryMechanics mechanics = new SubCategoryMechanics();
		
		public static class SubCategoryMechanics {
			@Config.Comment("Modify the vanilla regeneration mechanics to a completely new one. (Default: true)")
			@Config.LangKey("gui." + AoLCore.MODID + ".config.mechanics.regenerationoverhaul")
			@Config.RequiresWorldRestart
			public boolean regenerationOverhaul = true;
		}
	}
	
}
