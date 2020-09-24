package cn.catlemon.aol_core.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public abstract class SkillBase {
	private boolean _learned = false;
	private String _skillID;
	private ResourceLocation _skillIcon;
	private Vector<Integer> _skillIconCor;
	private Map<String, Integer> _skillPointRequirement;
	private Set<String> _skillDependencies;
	private Set<String> _skillDependents;

	public SkillBase(@Nonnull String skillID, ResourceLocation skillIcon, @Nonnull Vector<Integer> skillIconCor,
			Map<String, Integer> skillPointRequirement, Set<String> skillDependencies, Set<String> skillDependents) {
		this._skillID = skillID;
		this._skillIcon = skillIcon;
		this._skillIconCor = skillIconCor;
		this._skillPointRequirement = (skillPointRequirement == null) ? new HashMap<String, Integer>()
				: skillPointRequirement;
		this._skillDependencies = (skillDependencies == null) ? new HashSet<String>() : skillDependencies;
		this._skillDependents = (skillDependents == null) ? new HashSet<String>() : skillDependents;
	}

	public String getSkillID() {
		return _skillID;
	}

	public ResourceLocation getSkillIcon() {
		return _skillIcon;
	}

	public Vector<Integer> getSkillIconLocation() {
		return _skillIconCor;
	}

	public boolean isLearned() {
		return _learned;
	}

	public Map<String, Integer> getSkillPointRequirement() {
		return _skillPointRequirement;
	}

	public Set<String> getSkillDependencies() {
		return _skillDependencies;
	}

	public boolean addSkillDependents(String skillID) {
		if (_skillDependents.contains(skillID))
			return false;
		_skillDependents.add(skillID);
		return true;
	}

	public Set<String> getSkillDependents() {
		return _skillDependents;
	}

	public void learn() {
		_learned = true;
		MinecraftForge.EVENT_BUS.register(this);
		AoLEventLoader.AOL_EVENT_BUS.register(this);
	}

	public void forget() {
		_learned = false;
		MinecraftForge.EVENT_BUS.unregister(this);
		AoLEventLoader.AOL_EVENT_BUS.unregister(this);
	}

}
