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

import cn.catlemon.aol_core.advancement.TriggerHandler;
import cn.catlemon.aol_core.api.AoLEventLoader;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilitySkillPoint {
	public static class Storage implements Capability.IStorage<ISkillPoint> {
		
		@Override
		public void readNBT(Capability<ISkillPoint> capability, ISkillPoint instance, EnumFacing side, NBTBase nbt) {
			NBTTagList list = (NBTTagList) nbt;
			if (list == null)
				return;
			int num = list.tagCount();
			for (int i = 0; i < num; i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				if (!compound.hasNoTags()) {
					instance.setSPNum(compound.getString("SkillPointType"), compound.getInteger("SkillPointNum"));
				}
			}
			
		}
		
		@Nullable
		@Override
		public NBTBase writeNBT(Capability<ISkillPoint> capability, ISkillPoint instance, EnumFacing side) {
			NBTTagList list = new NBTTagList();
			List<String> skillPointTypeList = instance.getSPTypeList();
			for (String skillPointType : skillPointTypeList) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("SkillPointType", skillPointType);
				compound.setInteger("SkillPointNum", instance.getSPNum(skillPointType));
				list.appendTag(compound);
			}
			return list;
			
		}
		
	}
	
	public static class Implementation implements ISkillPoint {
		private Map<String, Integer> data = new HashMap<String, Integer>();
		
		private static Set<String> defaultType = new HashSet<String>();
		
		public Implementation() {
			AoLEventLoader.CapabilitySkillPointInitializeEvent event = new AoLEventLoader.CapabilitySkillPointInitializeEvent(
					defaultType);
			AoLEventLoader.AOL_EVENT_BUS.post(event);
			for (String type : defaultType)
				this.data.put(type, 0);
			
		}
		
		@Override
		public List<String> getSPTypeList() {
			List<String> list = new ArrayList<String>();
			for (Map.Entry<String, Integer> entry : this.data.entrySet()) {
				list.add(entry.getKey());
			}
			Collections.sort(list);
			return list;
		}
		
		@Override
		public void reset() {
			this.data.clear();
			for (String type : defaultType)
				this.data.put(type, 0);
		}
		
		@Override
		public int getSPNum(String skillPointType) {
			assert (skillPointType.matches("[A-Za-z0-9_\\-\\.]+"));
			skillPointType = skillPointType.toLowerCase();
			if (!this.data.containsKey(skillPointType))
				return 0;
			return this.data.get(skillPointType);
		}
		
		@Override
		public int setSPNum(String skillPointType, int skillPointNum) {
			// 不应该在这个文件以外的任何地方调用
			assert (skillPointType.matches("[A-Za-z0-9_\\-\\.]+"));
			skillPointType = skillPointType.toLowerCase();
			if (this.data.containsKey(skillPointType)) {
				int oldNum = this.data.get(skillPointType);
				this.data.replace(skillPointType, skillPointNum);
				return skillPointNum - oldNum;
			}
			this.data.put(skillPointType, skillPointNum);
			return skillPointNum;
		}
		
		@Override
		public boolean addSPNum(EntityPlayerMP player, String skillPointType, int skillPointNum) {
			assert (skillPointType.matches("[A-Za-z0-9_\\-\\.]+"));
			skillPointType = skillPointType.toLowerCase();
			assert (skillPointNum >= 0);
			if (!this.data.containsKey(skillPointType)) {
				this.data.put(skillPointType, skillPointNum);
				SkillPointHandler.sync(player);
				return true;
			}
			this.data.replace(skillPointType, this.data.get(skillPointType) + skillPointNum);
			if (skillPointNum != 0)
				TriggerHandler.getSkillPointTrigger.trigger(player, skillPointType);
			SkillPointHandler.sync(player);
			return false;
		}
		
		@Override
		public boolean subSPNum(EntityPlayerMP player, String skillPointType, int skillPointNum) {
			assert (skillPointType.matches("[A-Za-z0-9_\\-\\.]+"));
			skillPointType = skillPointType.toLowerCase();
			assert (skillPointNum >= 0);
			if (!this.data.containsKey(skillPointType) || this.data.get(skillPointType) < skillPointNum)
				return false;
			this.data.replace(skillPointType, this.data.get(skillPointType) - skillPointNum);
			SkillPointHandler.sync(player);
			return true;
		}
	}
	
	public static class ProviderPlayer implements ICapabilitySerializable<NBTTagCompound>, ICapabilityProvider {
		private ISkillPoint skillPoint = new Implementation();
		
		private Capability.IStorage<ISkillPoint> storage = CapabilityHandler.capSkillPoint.getStorage();
		
		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return CapabilityHandler.capSkillPoint.equals(capability);
		}
		
		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			if (CapabilityHandler.capSkillPoint.equals(capability)) {
				@SuppressWarnings("unchecked")
				T result = (T) skillPoint;
				return result;
			}
			return null;
		}
		
		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setTag(CapabilityHandler.TAGSKILLPOINT,
					storage.writeNBT(CapabilityHandler.capSkillPoint, skillPoint, null));
			return compound;
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			NBTTagList list = (NBTTagList) nbt.getTag(CapabilityHandler.TAGSKILLPOINT);
			storage.readNBT(CapabilityHandler.capSkillPoint, skillPoint, null, list);
		}
		
	}
}
