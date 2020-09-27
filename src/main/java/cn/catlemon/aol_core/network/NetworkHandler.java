package cn.catlemon.aol_core.network;

import cn.catlemon.aol_core.AoLCore;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkHandler {
	public static SimpleNetworkWrapper network;
	
	public NetworkHandler(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel(AoLCore.MODID);
		network.registerMessage(new PacketSkillPoint.Handler(), PacketSkillPoint.class, 1, Side.CLIENT);
		network.registerMessage(new PacketSkillTree.Handler(), PacketSkillTree.class, 1, Side.CLIENT);
		network.registerMessage(new PacketTranslate.Handler(), PacketTranslate.class, 1, Side.CLIENT);
	}
}
