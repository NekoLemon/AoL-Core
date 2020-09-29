package cn.catlemon.aol_core.capability;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.network.PacketSkillPoint;
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
public final class SkillPointHandler {
	@SubscribeEvent
	public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer) {
			ICapabilitySerializable<NBTTagCompound> providerSkillPoint = new CapabilitySkillPoint.ProviderPlayer();
			event.addCapability(new ResourceLocation(AoLCore.MODID, "skillpoint"), providerSkillPoint);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerClone(Clone event) {
		Capability<ISkillPoint> capabilitySkillPoint = CapabilityHandler.capSkillPoint;
		Capability.IStorage<ISkillPoint> storageSkillPoint = capabilitySkillPoint.getStorage();
		if (event.getOriginal().hasCapability(capabilitySkillPoint, null)
				&& event.getEntityPlayer().hasCapability(capabilitySkillPoint, null)) {
			NBTBase nbt = storageSkillPoint.writeNBT(capabilitySkillPoint,
					event.getOriginal().getCapability(capabilitySkillPoint, null), null);
			storageSkillPoint.readNBT(capabilitySkillPoint,
					event.getEntityPlayer().getCapability(capabilitySkillPoint, null), null, nbt);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			sync(player);
		}
	}
	
	public static void sync(EntityPlayerMP player) {
		if (player.hasCapability(CapabilityHandler.capSkillPoint, null)) {
			PacketSkillPoint message = new PacketSkillPoint();
			ISkillPoint skillPoint = player.getCapability(CapabilityHandler.capSkillPoint, null);
			Capability.IStorage<ISkillPoint> storage = CapabilityHandler.capSkillPoint.getStorage();
			message.compound = new NBTTagCompound();
			message.compound.setTag(CapabilityHandler.TAGSKILLPOINT,
					storage.writeNBT(CapabilityHandler.capSkillPoint, skillPoint, null));
			NetworkHandler.network.sendTo(message, (EntityPlayerMP) player);
		}
	}
}
