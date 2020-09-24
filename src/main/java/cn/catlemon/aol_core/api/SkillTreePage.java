package cn.catlemon.aol_core.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.catlemon.aol_core.capability.ISkillTree;

public class SkillTreePage {
	private String _skillTreePageID;
	private Map<String, SkillBase> _skills;

	public ISkillTree parent = null;

	public SkillTreePage(String skillTreePageID) {
		_skillTreePageID = skillTreePageID;
		_skills = new HashMap<String, SkillBase>();
	}

	public String getSkillTreePageID() {
		return _skillTreePageID;
	}

	public Set<String> getSkillList() {
		Set<String> skillList = new HashSet<String>();
		for (Map.Entry<String, SkillBase> entry : _skills.entrySet())
			skillList.add(entry.getKey());
		return skillList;
	}

	public boolean addSkill(SkillBase skill) {
		if (_skills.containsKey(skill.getSkillID()))
			return false;
		_skills.put(skill.getSkillID(), skill);
		return true;
	}

	public SkillBase getSkill(String skillID) {
		if (!_skills.containsKey(skillID))
			return null;
		return _skills.get(skillID);
	}

	public boolean hasSkill(String skillID) {
		return _skills.containsKey(skillID);
	}

	public boolean learnSkill(String skillID) {
		if (_skills.get(skillID).isLearned())
			return false;
		// #TODO Add a listener
		AoLEventLoader.LearnSkillEvent event = new AoLEventLoader.LearnSkillEvent(skillID);
		AoLEventLoader.AOL_EVENT_BUS.post(event);
		if (!event.isCanceled()) {
			_skills.get(skillID).learn();
			return true;
		}
		return false;
	}

	public boolean learnSkill(String skillID, boolean ignoreCondition) {
		if (!ignoreCondition)
			return learnSkill(skillID);
		if (_skills.get(skillID).isLearned())
			return false;
		_skills.get(skillID).learn();
		return true;
	}

	public boolean forgetSkill(String skillID) {
		if (!_skills.get(skillID).isLearned())
			return false;
		// #TODO Add a listener
		AoLEventLoader.ForgetSkillEvent event = new AoLEventLoader.ForgetSkillEvent(skillID);
		AoLEventLoader.AOL_EVENT_BUS.post(event);
		if (!event.isCanceled()) {
			_skills.get(skillID).forget();
			return true;
		}
		return false;
	}

	public boolean forgetSkill(String skillID, boolean ignoreCondition) {
		if (!ignoreCondition)
			return forgetSkill(skillID);
		if (!_skills.get(skillID).isLearned())
			return false;
		_skills.get(skillID).forget();
		return true;
	}

}
