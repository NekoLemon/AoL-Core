package cn.catlemon.aol_core.config;

import java.util.Collections;
import java.util.Set;

import cn.catlemon.aol_core.AoLCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class AoLCoreConfigGUI implements IModGuiFactory {
	@Override
	public void initialize(Minecraft mc) {
		
	}
	
	@Override
	public boolean hasConfigGui() {
		return true;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parent) {
		return new GuiConfig(parent, ConfigElement.from(AoLCoreConfig.MainConfig.class).getChildElements(),
				AoLCore.MODID, false, false, "Adventure of Lemon",
				new TextComponentTranslation("gui." + AoLCore.MODID + ".config.title").getFormattedText());
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}
}
