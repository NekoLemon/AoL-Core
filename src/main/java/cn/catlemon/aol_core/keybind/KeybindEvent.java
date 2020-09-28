package cn.catlemon.aol_core.keybind;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.gui.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeybindEvent {
	public KeybindEvent(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (KeybindHandler.showSkill.isPressed()) {
			Minecraft.getMinecraft().player.openGui(AoLCore.instance, GuiHandler.Gui_SKILLTREE,
					Minecraft.getMinecraft().world, 0, 1, 0);
		}
	}
}
