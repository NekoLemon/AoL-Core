package cn.catlemon.aol_core.network;

import cn.catlemon.aol_core.AoLCore;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkHandler {
	public static SimpleNetworkWrapper network;
	
	public NetworkHandler(FMLPreInitializationEvent event) {
		int id = 0;
		network = NetworkRegistry.INSTANCE.newSimpleChannel(AoLCore.MODID);
		network.registerMessage(PacketSkillPoint.Handler.class, PacketSkillPoint.class, id++, Side.CLIENT);
		network.registerMessage(PacketSkillTree.Handler.class, PacketSkillTree.class, id++, Side.CLIENT);
		network.registerMessage(PacketTranslate.Handler.class, PacketTranslate.class, id++, Side.CLIENT);
		network.registerMessage(PacketEvent.Handler.class, PacketEvent.class, id++, Side.SERVER);
	}
	
	public static void sendToAll(IMessage message) {
		network.sendToAll(message);
	}
	
	public static void sendToServer(IMessage message) {
		network.sendToServer(message);
	}
	
	public static void sync(World world, IMessage message) {
		if (!world.isRemote) {
			sendToAll(message);
		} else {
			sendToServer(message);
		}
	}
}
