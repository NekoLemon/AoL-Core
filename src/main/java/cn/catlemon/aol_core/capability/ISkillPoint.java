package cn.catlemon.aol_core.capability;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ISkillPoint {
	public List<String> getSPTypeList();
	
	public void reset();
	
	public int getSPNum(String skillPointType);
	
	public int setSPNum(String skillPointType, int skillPointNum);
	
	public boolean addSPNum(EntityPlayerMP player, String skillPointType, int skillPointNum);
	
	public boolean subSPNum(EntityPlayerMP player, String skillPointType, int skillPointNum);
}
