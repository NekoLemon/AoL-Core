package cn.catlemon.aol_core.capability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillBase;
import cn.catlemon.aol_core.api.SkillTreePage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilitySkillTree {
	public static class Storage implements Capability.IStorage<ISkillTree> {
		
		@Override
		public void readNBT(Capability<ISkillTree> capability, ISkillTree instance, EnumFacing side, NBTBase nbt) {
			NBTTagList list = (NBTTagList) nbt;
			if (list == null)
				return;
			int num = list.tagCount();
			for (int i = 0; i < num; i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				if (!compound.hasNoTags())
					instance.setSkillStat(compound.getString("SkillID"), compound.getBoolean("SkillLearned"));
			}
		}
		
		@Nullable
		@Override
		public NBTBase writeNBT(Capability<ISkillTree> capability, ISkillTree instance, EnumFacing side) {
			NBTTagList list = new NBTTagList();
			for (String pageID : instance.getPageSet()) {
				SkillTreePage page = instance.getPage(pageID);
				for (String skillID : page.getSkillSet()) {
					SkillBase skill = page.getSkill(skillID);
					NBTTagCompound compound = new NBTTagCompound();
					compound.setString("SkillID", skill.getSkillID());
					compound.setBoolean("SkillLearned", skill.isLearned());
					list.appendTag(compound);
				}
			}
			return list;
		}
		
	}
	
	public static class Implementation implements ISkillTree {
		Map<String, SkillTreePage> _pages;
		
		public Implementation() {
			_pages = new HashMap<String, SkillTreePage>();
			AoLEventLoader.AOL_EVENT_BUS.post(new AoLEventLoader.SkillTreeInitializeEvent(this));
			solveDependents();
		}
		
		public void solveDependents() {
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				for (String skillID : page.getSkillSet()) {
					SkillBase skill = page.getSkill(skillID);
					Set<String> dependenciesList = skill.getSkillDependencies();
					for (String dependenciesID : dependenciesList) {
						getSkill(dependenciesID).addSkillDependents(skillID);
					}
				}
			}
		}
		
		@Override
		public SkillBase getSkill(String skillID) {
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillID))
					return page.getSkill(skillID);
			}
			return null;
		}
		
		@Override
		public Set<String> getPageSet() {
			Set<String> pageList = new HashSet<String>();
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet())
				pageList.add(entry.getKey());
			return pageList;
		}
		
		@Override
		public boolean hasPage(@Nonnull String pageID) {
			return _pages.containsKey(pageID);
		}
		
		@Override
		public boolean addPage(@Nonnull SkillTreePage page) {
			if (_pages.containsKey(page.getSkillTreePageID()))
				return false;
			_pages.put(page.getSkillTreePageID(), page);
			return true;
		}
		
		@Override
		public SkillTreePage getPage(String pageID) {
			if (!_pages.containsKey(pageID))
				return null;
			return _pages.get(pageID);
		}
		
		@Override
		public void setSkillStat(String skillID, boolean status) {
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillID)) {
					SkillBase skill = page.getSkill(skillID);
					if (status)
						skill.learn();
					else
						skill.forget();
				}
			}
		}
		
		@Override
		public boolean learnSkill(EntityPlayer player, String skillID) {
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillID))
					return page.learnSkill(player, skillID);
			}
			return false;
		}
		
		@Override
		public boolean learnSkill(EntityPlayer player, String skillID, boolean ignoreCondition) {
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillID))
					return page.learnSkill(player, skillID, ignoreCondition);
			}
			return false;
		}
		
		@Override
		public boolean forgetSkill(EntityPlayer player, String skillID) {
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillID))
					return page.forgetSkill(player, skillID);
			}
			return false;
		}
		
		@Override
		public boolean forgetSkill(EntityPlayer player, String skillID, boolean ignoreCondition) {
			for (Map.Entry<String, SkillTreePage> entry : _pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillID))
					return page.forgetSkill(player, skillID, ignoreCondition);
			}
			return false;
		}
		
	}
	
	public static class ProviderPlayer implements ICapabilitySerializable<NBTTagCompound>, ICapabilityProvider {
		private ISkillTree skillTree = new Implementation();
		
		private Capability.IStorage<ISkillTree> storage = CapabilityHandler.capSkillTree.getStorage();
		
		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return CapabilityHandler.capSkillTree.equals(capability);
		}
		
		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			if (CapabilityHandler.capSkillTree.equals(capability)) {
				@SuppressWarnings("unchecked")
				T result = (T) skillTree;
				return result;
			}
			return null;
		}
		
		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setTag(CapabilityHandler.TAKSKILLTREE,
					storage.writeNBT(CapabilityHandler.capSkillTree, skillTree, null));
			return compound;
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			NBTTagList list = (NBTTagList) nbt.getTag(CapabilityHandler.TAKSKILLTREE);
			storage.readNBT(CapabilityHandler.capSkillTree, skillTree, null, list);
		}
		
	}
}
