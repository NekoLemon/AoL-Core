package cn.catlemon.aol_core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.SERVER)
public class ServerProxy implements IProxy {
	
	@Override
	public EntityPlayer getPlayer(MessageContext ctx) {
		return ctx.getServerHandler().player;
	}
	
}
