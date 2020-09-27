package cn.catlemon.aol_core.gui;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.gui.skilltree.GuiSkillTree;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {
	
	public static final int Gui_SKILLTREE = 1;
	
	public GuiHandler(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(AoLCore.instance, this);
	}
	
	@Override
	public Object getClientGuiElement(int Id, EntityPlayer player, World world, int x, int y, int z) {
		switch (Id) {
			case Gui_SKILLTREE:
				return new GuiSkillTree(player, x, y);
			default:
				return null;
		}
	}
	
	@Override
	public Object getServerGuiElement(int Id, EntityPlayer player, World world, int x, int y, int z) {
		switch (Id) {
			default:
				return null;
		}
	}
	
}
