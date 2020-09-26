package cn.catlemon.aol_core.api;

import cn.catlemon.aol_core.advancement.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class TriggerHandler {
	public static final LearnSkillTrigger learnSkillTrigger=new LearnSkillTrigger();
	public static final ForgetSkillTrigger forgetSkillTrigger=new ForgetSkillTrigger();
	public TriggerHandler(FMLPreInitializationEvent event) {
		registerTriggers(learnSkillTrigger,forgetSkillTrigger);
	}
	
	private static void registerTriggers(ICriterionTrigger<?>... triggers) {
		for (ICriterionTrigger<?> trigger : triggers) {
			CriteriaTriggers.register(trigger);
		}
	}
}
