package cn.catlemon.aol_core.capability;

import java.util.Set;

import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillTreePage;

public interface ISkillTree {
	public Set<String> getPageList();

	public boolean addPage(SkillTreePage page);

	public boolean hasPage(String pageID);

	public SkillTreePage getPage(String pageID);

	public void setSkillStat(String skillID, boolean status);

	public boolean learnSkill(String skillID);

	public boolean learnSkill(String skillID, boolean ignoreCondition);

	public boolean forgetSkill(String skillID);

	public boolean forgetSkill(String skillID, boolean ignoreCondition);
}
