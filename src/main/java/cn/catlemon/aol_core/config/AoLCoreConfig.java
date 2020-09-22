package cn.catlemon.aol_core.config;

import net.minecraftforge.common.config.Config;

public class AoLCoreConfig {

	@Config(modid = "aol_core", type = Config.Type.INSTANCE, name = "Adventure_of_Lemon/Main")
	@Config.LangKey("gui.aolconfig.core.title")
	public static class MainConfig {

		@Config.Comment("Game Mechanics")
		@Config.LangKey("gui.aolconfig.core.mechanics")
		public static final SubCategoryMechanics mechanics = new SubCategoryMechanics();

		public static class SubCategoryMechanics {
			@Config.Comment("Modify the vanilla regeneration mechanics to a completely new one. (Default: true)")
			@Config.LangKey("gui.aolconfig.core.mechanics.regenerationOverhaul")
			@Config.RequiresWorldRestart
			public boolean regenerationOverhaul = true;
		}
	}

}
