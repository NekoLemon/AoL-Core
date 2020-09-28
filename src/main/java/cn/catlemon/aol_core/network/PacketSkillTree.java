package cn.catlemon.aol_core.network;

import javax.annotation.Nullable;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillTree;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSkillTree implements IMessage {
	public NBTTagCompound compound;
	
	@Override
	public void fromBytes(ByteBuf buf) {
		compound = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, compound);
	}
	
	public static class Handler implements IMessageHandler<PacketSkillTree, IMessage> {
		@Nullable
		@Override
		public IMessage onMessage(PacketSkillTree message, MessageContext ctx) {
			final NBTBase nbt = message.compound.getTag(CapabilityHandler.TAGSKILLTREE);
			EntityPlayer player = AoLCore.proxy.getPlayer(ctx);
			if (player == null)
				return null;
			if (player.hasCapability(CapabilityHandler.capSkillTree, null)) {
				ISkillTree skillTree = player.getCapability(CapabilityHandler.capSkillTree, null);
				Capability.IStorage<ISkillTree> storage = CapabilityHandler.capSkillTree.getStorage();
				storage.readNBT(CapabilityHandler.capSkillTree, skillTree, null, nbt);
			}
			return null;
		}
	}
}
