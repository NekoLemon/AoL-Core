package cn.catlemon.aol_core.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillBase;
import cn.catlemon.aol_core.api.SkillTreePage;
import net.minecraft.entity.player.EntityPlayerMP;
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
					instance.setSkillStat(compound.getString("SkillId"), compound.getBoolean("SkillLearned"));
			}
		}
		
		@Nullable
		@Override
		public NBTBase writeNBT(Capability<ISkillTree> capability, ISkillTree instance, EnumFacing side) {
			NBTTagList list = new NBTTagList();
			for (String pageId : instance.getPageSet()) {
				SkillTreePage page = instance.getPage(pageId);
				for (String skillId : page.getSkillSet()) {
					SkillBase skill = page.getSkill(skillId);
					NBTTagCompound compound = new NBTTagCompound();
					compound.setString("SkillId", skill.getSkillId());
					compound.setBoolean("SkillLearned", skill.isLearned());
					list.appendTag(compound);
				}
			}
			return list;
		}
		
	}
	
	public static class Implementation implements ISkillTree {
		Map<String, SkillTreePage> pages = new HashMap<String, SkillTreePage>();
		
		public Implementation() {
			AoLEventLoader.AOL_EVENT_BUS.post(new AoLEventLoader.SkillTreeInitializeEvent(this));
			solveDependents();
		}
		
		public void solveDependents() {
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				for (String skillId : page.getSkillSet()) {
					SkillBase skill = page.getSkill(skillId);
					Set<String> dependenciesList = skill.getSkillDependencies();
					for (String dependenciesId : dependenciesList) {
						getSkill(dependenciesId).addSkillDependents(skillId);
					}
				}
			}
		}
		
		@Override
		public SkillBase getSkill(String skillId) {
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillId))
					return page.getSkill(skillId);
			}
			return null;
		}
		
		@Override
		public Set<String> getPageSet() {
			Set<String> pageSet = new HashSet<String>();
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet())
				pageSet.add(entry.getKey());
			return pageSet;
		}
		
		@Override
		public List<String> getPageList() {
			List<String> pageList = new ArrayList<String>(getPageSet());
			Collections.sort(pageList);
			return pageList;
		}
		
		@Override
		public boolean hasPage(@Nonnull String pageId) {
			return this.pages.containsKey(pageId);
		}
		
		@Override
		public boolean addPage(@Nonnull SkillTreePage page) {
			if (this.pages.containsKey(page.getSkillTreePageId()))
				return false;
			this.pages.put(page.getSkillTreePageId(), page);
			return true;
		}
		
		@Override
		public SkillTreePage getPage(String pageId) {
			if (!this.pages.containsKey(pageId))
				return null;
			return this.pages.get(pageId);
		}
		
		@Override
		public void setSkillStat(String skillId, boolean status) {
			// 不应该在这个文件以外的任何地方调用
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillId)) {
					SkillBase skill = page.getSkill(skillId);
					if (status)
						skill.learn(null);
					else
						skill.forget(null);
				}
			}
		}
		
		@Override
		public boolean learnSkill(EntityPlayerMP player, String skillId) {
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillId)) {
					boolean result = page.learnSkill(player, skillId);
					SkillTreeHandler.sync(player);
					return result;
				}
			}
			return false;
		}
		
		@Override
		public boolean learnSkill(EntityPlayerMP player, String skillId, boolean ignoreCondition) {
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillId)) {
					boolean result = page.learnSkill(player, skillId, ignoreCondition);
					SkillTreeHandler.sync(player);
					return result;
				}
			}
			return false;
		}
		
		@Override
		public boolean forgetSkill(EntityPlayerMP player, String skillId) {
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillId)) {
					boolean result = page.forgetSkill(player, skillId);
					SkillTreeHandler.sync(player);
					return result;
				}
			}
			return false;
		}
		
		@Override
		public boolean forgetSkill(EntityPlayerMP player, String skillId, boolean ignoreCondition) {
			for (Map.Entry<String, SkillTreePage> entry : this.pages.entrySet()) {
				SkillTreePage page = entry.getValue();
				if (page.hasSkill(skillId)) {
					boolean result = page.forgetSkill(player, skillId, ignoreCondition);
					SkillTreeHandler.sync(player);
					return result;
				}
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
			compound.setTag(CapabilityHandler.TAGSKILLTREE,
					storage.writeNBT(CapabilityHandler.capSkillTree, skillTree, null));
			return compound;
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			NBTTagList list = (NBTTagList) nbt.getTag(CapabilityHandler.TAGSKILLTREE);
			storage.readNBT(CapabilityHandler.capSkillTree, skillTree, null, list);
		}
		
	}
}
