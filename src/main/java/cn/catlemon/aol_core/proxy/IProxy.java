package cn.catlemon.aol_core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IProxy {
	EntityPlayer getPlayer(MessageContext ctx);
}