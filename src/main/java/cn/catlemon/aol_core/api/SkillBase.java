package cn.catlemon.aol_core.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public abstract class SkillBase {
	public static final String NOTALLOWEDTOLEARN = "not_allowed_to_learn";
	public static final String NOTALLOWEDTOFORGET = "not_allowed_to_forget";
	
	private boolean _learned = false;
	protected String _skillID = null;
	protected ResourceLocation _skillIcon = null;
	protected Vector<Integer> _skillIconCor = null;
	protected Map<String, Integer> _skillPointRequirement = new HashMap<String, Integer>();
	protected Set<String> _skillDependencies = new HashSet<String>();
	protected Set<String> _skillDependents = new HashSet<String>();
	
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
