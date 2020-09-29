package cn.catlemon.aol_core.network;

import javax.annotation.Nullable;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEvent implements IMessage {
	public static final String LEARN_SKILL_EVENT = "learn_skill";
	
	private NBTTagCompound compound = new NBTTagCompound();
	
	public PacketEvent() {
		
	}
	
	public PacketEvent(String eventType, String... args) {
		compound.setString("event_type", eventType);
		if (args.length > 0) {
			NBTTagList list = new NBTTagList();
			for (String arg : args) {
				NBTTagCompound carg = new NBTTagCompound();
				carg.setString("s", arg);
				list.appendTag(carg);
			}
			compound.setTag("args", list);
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		compound = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, compound);
	}
	
	public static class Handler implements IMessageHandler<PacketEvent, IMessage> {
		@Nullable
		@Override
		public IMessage onMessage(PacketEvent message, MessageContext ctx) {
			if (!(AoLCore.proxy.getPlayer(ctx) instanceof EntityPlayerMP))
				return null;
			EntityPlayerMP player = (EntityPlayerMP) AoLCore.proxy.getPlayer(ctx);
			String eventType = message.compound.getString("event_type");
			String[] args = null;
			if (message.compound.hasKey("args")) {
				NBTTagList list = (NBTTagList) message.compound.getTag("args");
				args = new String[list.tagCount()];
				for (int i = 0; i < args.length; i++) {
					args[i] = list.getCompoundTagAt(i).getString("s");
				}
			}
			switch (eventType) {
				case PacketEvent.LEARN_SKILL_EVENT:
					if (args.length > 0)
						player.getCapability(CapabilityHandler.capSkillTree, null).learnSkill(player, args[0]);
					break;
				default:
					break;
			}
			return null;
		}
	}
	
}
