package cn.catlemon.aol_core.capability;

import java.util.List;

public interface ISkillPoint {
	public List<String> getSPTypeList();
	
	public void reset();
	
	public int getSPNum(String skillPointType);
	
	public int setSPNum(String skillPointType, int skillPointNum);
	
	public boolean addSPNum(String skillPointType, int skillPointNum);
	
	public boolean subSPNum(String skillPointType, int skillPointNum);
}
