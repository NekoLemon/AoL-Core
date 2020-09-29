package cn.catlemon.aol_core.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import cn.catlemon.aol_core.advancement.TriggerHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public abstract class SkillBase {
	public static final String NOTALLOWEDTOLEARN = "not_allowed_to_learn";
	public static final String NOTALLOWEDTOFORGET = "not_allowed_to_forget";
	
	private boolean learned = false;
	private String skillId = null;
	protected PackedResourceLocation skillIcon = null;
	protected PackedResourceLocation skillBackground = null;
	protected Coordinate<Integer> skillCor = null;
	protected int learnLevel = 1;
	protected Map<String, Integer> skillPointRequirement = new HashMap<String, Integer>();
	protected Set<String> skillDependencies = new HashSet<String>();
	private Set<String> skillDependents = new HashSet<String>();
	
	public SkillBase(@Nonnull String skillId) {
		this.skillId = skillId;
	}
	
	public String getSkillId() {
		return this.skillId;
	}
	
	public Coordinate<Integer> getSkillLocation() {
		return this.skillCor;
	}
	
	public PackedResourceLocation getSkillIcon() {
		return this.skillIcon;
	}
	
	public PackedResourceLocation getSkillBackground() {
		return this.skillBackground;
	}
	
	public boolean isLearned() {
		return this.learned;
	}
	
	public int getLearnLevel() {
		return this.learnLevel;
	}
	
	public Map<String, Integer> getSkillPointRequirement() {
		return this.skillPointRequirement;
	}
	
	public Set<String> getSkillDependencies() {
		return this.skillDependencies;
	}
	
	public boolean addSkillDependents(String skillId) {
		if (this.skillDependents.contains(skillId))
			return false;
		this.skillDependents.add(skillId);
		return true;
	}
	
	public Set<String> getSkillDependents() {
		return this.skillDependents;
	}
	
	public void learn(EntityPlayerMP player) {
		this.learned = true;
		MinecraftForge.EVENT_BUS.register(this);
		AoLEventLoader.AOL_EVENT_BUS.register(this);
		if (player != null)
			TriggerHandler.learnSkillTrigger.trigger(player, this.skillId);
	}
	
	public void forget(EntityPlayerMP player) {
		this.learned = false;
		MinecraftForge.EVENT_BUS.unregister(this);
		AoLEventLoader.AOL_EVENT_BUS.unregister(this);
		if (player != null)
			TriggerHandler.forgetSkillTrigger.trigger(player, this.skillId);
	}
	
}
