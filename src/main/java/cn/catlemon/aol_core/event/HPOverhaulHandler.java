package cn.catlemon.aol_core.event;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class HPOverhaulHandler {
	public HPOverhaulHandler() {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
	}

	public void finalize() {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(this);
	}
}
