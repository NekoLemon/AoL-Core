package cn.catlemon.aol_core.gui.skilltree;

import cn.catlemon.aol_core.api.SkillTreePage;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillPoint;
import cn.catlemon.aol_core.capability.ISkillTree;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.network.PacketSkillPoint;
import cn.catlemon.aol_core.network.PacketSkillTree;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

public class GuiSkillTree extends GuiScreen {
	EntityPlayer player;
	SkillTreePage page;
	int learnLevel;
	
	public GuiSkillTree(EntityPlayer player, int pageGuiId, int learnLevel) {
		this.player = player;
		this.page = player.getCapability(CapabilityHandler.capSkillTree, null).getPage(pageGuiId);
		this.learnLevel = learnLevel;
	}
	
	private void sync() {
		{
			PacketSkillPoint message = new PacketSkillPoint();
			ISkillPoint skillPoint = player.getCapability(CapabilityHandler.capSkillPoint, null);
			Capability.IStorage<ISkillPoint> storage = CapabilityHandler.capSkillPoint.getStorage();
			message.compound = new NBTTagCompound();
			message.compound.setTag(CapabilityHandler.TAGSKILLPOINT,
					storage.writeNBT(CapabilityHandler.capSkillPoint, skillPoint, null));
			NetworkHandler.sync(player.world, message);
		}
		{
			
			PacketSkillTree message = new PacketSkillTree();
			ISkillTree skillTree = player.getCapability(CapabilityHandler.capSkillTree, null);
			Capability.IStorage<ISkillTree> storage = CapabilityHandler.capSkillTree.getStorage();
			message.compound = new NBTTagCompound();
			message.compound.setTag(CapabilityHandler.TAGSKILLTREE,
					storage.writeNBT(CapabilityHandler.capSkillTree, skillTree, null));
			NetworkHandler.sync(player.world, message);
		}
	}
	
	@Override
	public void onGuiClosed() {
		sync();
		super.onGuiClosed();
	}
}
