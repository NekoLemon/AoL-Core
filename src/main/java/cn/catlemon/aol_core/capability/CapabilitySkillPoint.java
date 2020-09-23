package cn.catlemon.aol_core.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cn.catlemon.aol_core.api.ISkillPoint;
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
			int num = list.tagCount();
			for (int i = 0; i < num; i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				if (!compound.hasNoTags()) {
					instance.setSkillPoint(compound.getString("SkillPointType"), compound.getInteger("SkillPointNum"));
				}
			}

		}

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<ISkillPoint> capability, ISkillPoint instance, EnumFacing side) {
			NBTTagList list = new NBTTagList();
			List<String> skillPointTypeList = instance.getSkillPointTypeList();
			for (String skillPointType : skillPointTypeList) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("SkillPointType", skillPointType);
				compound.setInteger("SkillPointNum", instance.getSkillPoint(skillPointType));
				list.appendTag(compound);
			}
			return list;

		}

	}

	public static class Implementation implements ISkillPoint {
		private Map<String, Integer> _data = new HashMap<String, Integer>();

		public Implementation() {
			_data.put("normal", 0);
			_data.put("special", 0);
		}

		@Override
		public List<String> getSkillPointTypeList() {
			List<String> list = new ArrayList<String>();
			for (Map.Entry<String, Integer> entry : _data.entrySet()) {
				list.add(entry.getKey());
			}
			Collections.sort(list);
			return list;
		}

		@Override
		public int getSkillPoint(String skillPointType) {
			if (!_data.containsKey(skillPointType))
				return 0;
			return _data.get(skillPointType);
		}

		@Override
		public int setSkillPoint(String skillPointType, int skillPointNum) {
			if (_data.containsKey(skillPointType)) {
				int oldNum = _data.get(skillPointType);
				_data.replace(skillPointType, skillPointNum);
				return skillPointNum - oldNum;
			}
			_data.put(skillPointType, skillPointNum);
			return skillPointNum;
		}

		@Override
		public boolean addSkillPoint(String skillPointType, int skillPointNum) {
			assert (skillPointNum >= 0);
			if (!_data.containsKey(skillPointType)) {
				_data.put(skillPointType, skillPointNum);
				return true;
			}
			_data.replace(skillPointType, _data.get(skillPointType) + skillPointNum);
			return false;
		}

		@Override
		public boolean subSkillPoint(String skillPointType, int skillPointNum) {
			assert (skillPointNum >= 0);
			if (!_data.containsKey(skillPointType) || _data.get(skillPointType) < skillPointNum)
				return false;
			_data.replace(skillPointType, _data.get(skillPointType) - skillPointNum);
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
			compound.setTag(CapabilityHandler.tagSkillPoint,
					storage.writeNBT(CapabilityHandler.capSkillPoint, skillPoint, null));
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			NBTTagList list = (NBTTagList) nbt.getTag(CapabilityHandler.tagSkillPoint);
			storage.readNBT(CapabilityHandler.capSkillPoint, skillPoint, null, list);
		}

	}
}
