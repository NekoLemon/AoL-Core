package cn.catlemon.aol_core.event;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.CapabilitySkillTree;
import cn.catlemon.aol_core.capability.ISkillTree;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.network.PacketSkillTree;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid = AoLCore.MODID)
public final class SkillTreeHandler {
	@SubscribeEvent
	public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			ICapabilitySerializable<NBTTagCompound> providerSkillTree = new CapabilitySkillTree.ProviderPlayer();
			event.addCapability(new ResourceLocation(AoLCore.MODID + ":skilltree"), providerSkillTree);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerClone(Clone event) {
		Capability<ISkillTree> capabilitySkillTree = CapabilityHandler.capSkillTree;
		Capability.IStorage<ISkillTree> storageSkillTree = capabilitySkillTree.getStorage();
		if (event.getOriginal().hasCapability(capabilitySkillTree, null)
				&& event.getEntityPlayer().hasCapability(capabilitySkillTree, null)) {
			NBTBase nbt = storageSkillTree.writeNBT(capabilitySkillTree,
					event.getOriginal().getCapability(capabilitySkillTree, null), null);
			storageSkillTree.readNBT(capabilitySkillTree,
					event.getEntityPlayer().getCapability(capabilitySkillTree, null), null, nbt);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			if (player.hasCapability(CapabilityHandler.capSkillTree, null)) {
				PacketSkillTree message = new PacketSkillTree();
				ISkillTree skillTree = player.getCapability(CapabilityHandler.capSkillTree, null);
				Capability.IStorage<ISkillTree> storage = CapabilityHandler.capSkillTree.getStorage();
				message.compound = new NBTTagCompound();
				message.compound.setTag(CapabilityHandler.TAKSKILLTREE,
						storage.writeNBT(CapabilityHandler.capSkillTree, skillTree, null));
				NetworkHandler.network.sendTo(message, (EntityPlayerMP) player);
			}
		}
	}
}
