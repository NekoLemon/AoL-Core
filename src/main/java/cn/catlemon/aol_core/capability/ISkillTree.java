package cn.catlemon.aol_core.capability;

import java.util.Set;

import cn.catlemon.aol_core.api.SkillBase;
import cn.catlemon.aol_core.api.SkillTreePage;
import net.minecraft.entity.player.EntityPlayer;

public interface ISkillTree {
	public Set<String> getPageSet();
	
	public SkillBase getSkill(String skillID);
	
	public boolean addPage(SkillTreePage page);
	
	public boolean hasPage(String pageID);
	
	public SkillTreePage getPage(String pageID);
	
	public void setSkillStat(String skillID, boolean status);
	
	public boolean learnSkill(EntityPlayer player, String skillID);
	
	public boolean learnSkill(EntityPlayer player, String skillID, boolean ignoreCondition);
	
	public boolean forgetSkill(EntityPlayer player, String skillID);
	
	public boolean forgetSkill(EntityPlayer player, String skillID, boolean ignoreCondition);
}
