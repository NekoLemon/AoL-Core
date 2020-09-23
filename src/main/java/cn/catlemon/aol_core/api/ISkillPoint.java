package cn.catlemon.aol_core.api;

import java.util.List;
import java.util.Map;

public interface ISkillPoint {
	public List<String> getSkillPointTypeList();

	public int getSkillPoint(String skillPointType);

	public int setSkillPoint(String skillPointType, int skillPointNum);

	public boolean addSkillPoint(String skillPointType, int skillPointNum);

	public boolean subSkillPoint(String skillPointType, int skillPointNum);
}
