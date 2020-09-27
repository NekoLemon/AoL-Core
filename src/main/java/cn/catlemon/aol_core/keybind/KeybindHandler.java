package cn.catlemon.aol_core.keybind;

import org.lwjgl.input.Keyboard;

import cn.catlemon.aol_core.AoLCore;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class KeybindHandler {
	
	private static final String CATEGORY = "key.categories." + AoLCore.MODID;
	
	public static KeyBinding showSkill;
	
	public KeybindHandler(FMLInitializationEvent event) {
		new KeybindEvent(event);
		showSkill = new KeyBinding("key." + AoLCore.MODID + ".showskill", Keyboard.KEY_J, CATEGORY);
		ClientRegistry.registerKeyBinding(showSkill);
	}
}
