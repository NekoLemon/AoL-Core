package cn.catlemon.aol_core.api;

import java.util.Set;

import cn.catlemon.aol_core.capability.ISkillTree;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public final class AoLEventLoader {
	public static final EventBus AOL_EVENT_BUS = new EventBus();
	
	// Subscribe this in FMLPreInitializationEvent stage
	public static class CapabilitySkillPointInitializeEvent extends Event {
		private Set<String> defaultSPType;
		
		public boolean addDefaultSPType(String defaultSPTypeName) {
			assert (defaultSPTypeName.matches("[A-Za-z0-9_\\-\\.]+"));
			defaultSPTypeName = defaultSPTypeName.toLowerCase();
			if (defaultSPType.contains(defaultSPTypeName))
				return false;
			defaultSPType.add(defaultSPTypeName);
			return true;
		}
		
		public int addDefaultSPType(String[] defaultSPTypeNameList) {
			int cnt = 0;
			for (String defaultSPTypeName : defaultSPTypeNameList) {
				assert (defaultSPTypeName.matches("[A-Za-z0-9_\\-\\.]+"));
				defaultSPTypeName = defaultSPTypeName.toLowerCase();
				if (!defaultSPType.contains(defaultSPTypeName)) {
					defaultSPType.add(defaultSPTypeName);
					cnt++;
				}
			}
			return cnt;
		}
		
		public int addDefaultSPType(Iterable<String> defaultSPTypeNameList) {
			int cnt = 0;
			for (String defaultSPTypeName : defaultSPTypeNameList) {
				assert (defaultSPTypeName.matches("[A-Za-z0-9_\\-\\.]+"));
				defaultSPTypeName = defaultSPTypeName.toLowerCase();
				if (!defaultSPType.contains(defaultSPTypeName)) {
					defaultSPType.add(defaultSPTypeName);
					cnt++;
				}
			}
			return cnt;
		}
		
		public CapabilitySkillPointInitializeEvent(Set<String> defaultSPType) {
			this.defaultSPType = defaultSPType;
		}
	}
	
	public static class SkillTreeInitializeEvent extends Event {
		private ISkillTree _skillTree;
		
		public SkillTreeInitializeEvent(ISkillTree skillTree) {
			_skillTree = skillTree;
		}
		
		public boolean addPage(SkillTreePage page) {
			return _skillTree.addPage(page);
		}
	}
	
	@Cancelable
	public static class LearnSkillEvent extends PlayerEvent {
		private String _skillID;
		
		public LearnSkillEvent(EntityPlayer player, String skillID) {
			super(player);
			_skillID = skillID;
		}
		
		public final String getSkillID() {
			return _skillID;
		}
	}
	
	@Cancelable
	public static class ForgetSkillEvent extends PlayerEvent {
		private String _skillID;
		
		public ForgetSkillEvent(EntityPlayer player, String skillID) {
			super(player);
			_skillID = skillID;
		}
		
		public final String getSkillID() {
			return _skillID;
		}
	}
	
	public static class TestEvent extends Event {
		public ICommandSender sender;
		
		public TestEvent(ICommandSender sender) {
			this.sender = sender;
		}
	}
}
