package cn.catlemon.aol_core.capability;

import java.util.Map;
import java.util.Set;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class CapabilityHandler {
	public final static String TAGSKILLPOINT = AoLCore.MODID + ":skillPoint";
	public final static String TAKSKILLTREE = AoLCore.MODID + ":skillTree";
	
	@CapabilityInject(ISkillPoint.class)
	public static Capability<ISkillPoint> capSkillPoint;
	
	@CapabilityInject(ISkillTree.class)
	public static Capability<ISkillTree> capSkillTree;
	
	public CapabilityHandler(FMLPreInitializationEvent event) {
		CapabilityManager.INSTANCE.register(ISkillPoint.class, new CapabilitySkillPoint.Storage(),
				CapabilitySkillPoint.Implementation::new);
		CapabilityManager.INSTANCE.register(ISkillTree.class, new CapabilitySkillTree.Storage(),
				CapabilitySkillTree.Implementation::new);
		AoLEventLoader.AOL_EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onCapabilitySkillPointInitialize(AoLEventLoader.CapabilitySkillPointInitializeEvent event) {
		String[] defaultSPType = new String[] { AoLCore.MODID + ".normal", AoLCore.MODID + ".special" };
		event.addDefaultSPType(defaultSPType);
	}
	
	public boolean dependenciesCheck(EntityPlayer player, String skillID) {
		if (!player.hasCapability(capSkillTree, null))
			return false;
		ISkillTree skillTree = player.getCapability(capSkillTree, null);
		SkillBase skill = skillTree.getSkill(skillID);
		if (skill == null)
			return false;
		Set<String> dependenciesList = skill.getSkillDependencies();
		for (String dependenciesID : dependenciesList) {
			if (dependenciesID.equals(SkillBase.NOTALLOWEDTOLEARN) || skillTree.getSkill(dependenciesID) == null
					|| !skillTree.getSkill(dependenciesID).isLearned())
				return false;
		}
		return true;
	}
	
	public boolean dependentsCheck(EntityPlayer player, String skillID) {
		if (!player.hasCapability(capSkillTree, null))
			return false;
		ISkillTree skillTree = player.getCapability(capSkillTree, null);
		SkillBase skill = skillTree.getSkill(skillID);
		if (skill == null)
			return false;
		Set<String> dependentsList = skill.getSkillDependents();
		for (String dependentsID : dependentsList) {
			if (dependentsID.equals(SkillBase.NOTALLOWEDTOFORGET)
					|| (!(skillTree.getSkill(dependentsID) == null) && skillTree.getSkill(dependentsID).isLearned()))
				return false;
		}
		return true;
	}
	
	@SubscribeEvent
	public void onLearnSkill(AoLEventLoader.LearnSkillEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		String skillID = event.getSkillID();
		if (!dependenciesCheck(player, skillID) || !player.hasCapability(capSkillTree, null)
				|| !player.hasCapability(capSkillPoint, null)) {
			event.setCanceled(true);
			return;
		}
		ISkillTree skillTree = player.getCapability(capSkillTree, null);
		ISkillPoint skillPoint = player.getCapability(capSkillPoint, null);
		SkillBase skill = skillTree.getSkill(skillID);
		Map<String, Integer> spList = skill.getSkillPointRequirement();
		for (Map.Entry<String, Integer> entry : spList.entrySet()) {
			if (skillPoint.getSPNum(entry.getKey()) < entry.getValue()) {
				event.setCanceled(true);
				return;
			}
		}
		for (Map.Entry<String, Integer> entry : spList.entrySet()) {
			skillPoint.subSPNum(entry.getKey(), entry.getValue());
		}
	}
	
	@SubscribeEvent
	public void onForgetSkill(AoLEventLoader.ForgetSkillEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		String skillID = event.getSkillID();
		if (!dependentsCheck(player, skillID) || !player.hasCapability(capSkillTree, null)
				|| !player.hasCapability(capSkillPoint, null)) {
			event.setCanceled(true);
			return;
		}
		ISkillTree skillTree = player.getCapability(capSkillTree, null);
		ISkillPoint skillPoint = player.getCapability(capSkillPoint, null);
		SkillBase skill = skillTree.getSkill(skillID);
		Map<String, Integer> spList = skill.getSkillPointRequirement();
		for (Map.Entry<String, Integer> entry : spList.entrySet()) {
			skillPoint.addSPNum(entry.getKey(), entry.getValue());
		}
	}
}
