package cn.catlemon.aol_core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy implements IProxy {
	
	@Override
	public EntityPlayer getPlayer(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().player : ctx.getServerHandler().player);
	}
	
}
