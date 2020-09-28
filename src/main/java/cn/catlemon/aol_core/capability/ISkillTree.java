package cn.catlemon.aol_core.capability;

import java.util.List;
import java.util.Set;

import cn.catlemon.aol_core.api.SkillBase;
import cn.catlemon.aol_core.api.SkillTreePage;
import net.minecraft.entity.player.EntityPlayerMP;

public interface ISkillTree {
	public Set<String> getPageSet();
	
	public List<String> getPageList();
	
	public SkillBase getSkill(String skillId);
	
	public boolean addPage(SkillTreePage page);
	
	public boolean hasPage(String pageId);
	
	public SkillTreePage getPage(int guiId);
	
	public SkillTreePage getPage(String pageId);
	
	public void setSkillStat(String skillId, boolean status);
	
	public boolean learnSkill(EntityPlayerMP player, String skillId);
	
	public boolean learnSkill(EntityPlayerMP player, String skillId, boolean ignoreCondition);
	
	public boolean forgetSkill(EntityPlayerMP player, String skillId);
	
	public boolean forgetSkill(EntityPlayerMP player, String skillId, boolean ignoreCondition);
}
