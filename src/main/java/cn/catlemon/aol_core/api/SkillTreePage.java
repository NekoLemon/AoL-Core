package cn.catlemon.aol_core.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;

public class SkillTreePage {
	private String skillTreePageId;
	private Map<String, SkillBase> skills;
	
	public SkillTreePage(String skillTreePageId) {
		this.skillTreePageId = skillTreePageId;
		this.skills = new HashMap<String, SkillBase>();
	}
	
	public String getSkillTreePageId() {
		return this.skillTreePageId;
	}
	
	public Set<String> getSkillSet() {
		Set<String> skillList = new HashSet<String>();
		for (Map.Entry<String, SkillBase> entry : this.skills.entrySet())
			skillList.add(entry.getKey());
		return skillList;
	}
	
	public boolean addSkill(SkillBase skill) {
		if (this.skills.containsKey(skill.getSkillId()))
			return false;
		this.skills.put(skill.getSkillId(), skill);
		return true;
	}
	
	public SkillBase getSkill(String skillId) {
		if (!this.skills.containsKey(skillId))
			return null;
		return this.skills.get(skillId);
	}
	
	public boolean hasSkill(String skillId) {
		return this.skills.containsKey(skillId);
	}
	
	public boolean learnSkill(EntityPlayerMP player, String skillId) {
		if (this.skills.get(skillId).isLearned())
			return false;
		AoLEventLoader.LearnSkillEvent event = new AoLEventLoader.LearnSkillEvent(player, skillId);
		AoLEventLoader.AOL_EVENT_BUS.post(event);
		if (!event.isCanceled()) {
			this.skills.get(skillId).learn(player);
			return true;
		}
		return false;
	}
	
	public boolean learnSkill(EntityPlayerMP player, String skillId, boolean ignoreCondition) {
		if (!ignoreCondition)
			return learnSkill(player, skillId);
		if (this.skills.get(skillId).isLearned())
			return false;
		this.skills.get(skillId).learn(player);
		return true;
	}
	
	public boolean forgetSkill(EntityPlayerMP player, String skillId) {
		if (!this.skills.get(skillId).isLearned())
			return false;
		AoLEventLoader.ForgetSkillEvent event = new AoLEventLoader.ForgetSkillEvent(player, skillId);
		AoLEventLoader.AOL_EVENT_BUS.post(event);
		if (!event.isCanceled()) {
			this.skills.get(skillId).forget(player);
			return true;
		}
		return false;
	}
	
	public boolean forgetSkill(EntityPlayerMP player, String skillId, boolean ignoreCondition) {
		if (!ignoreCondition)
			return forgetSkill(player, skillId);
		if (!this.skills.get(skillId).isLearned())
			return false;
		this.skills.get(skillId).forget(player);
		return true;
	}
	
}
