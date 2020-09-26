package cn.catlemon.aol_core.command;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillTreePage;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillTree;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSkill extends CommandBase {
	// TODO Completely refactor NEED
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0)
			throw new WrongUsageException("command." + AoLCore.MODID + ".skill.usage");
		EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
		if (!player.hasCapability(CapabilityHandler.capSkillTree, null)) {
			AoLCore.LOGGER.error("玩家\"" + player.getName() + "\"没有SkillTree的Capability，请寻求mod作者的帮助。");
			return;
		}
		ISkillTree skillTree = player.getCapability(CapabilityHandler.capSkillTree, null);
		switch (args[0]) {
			case "list": {
				if (args.length == 1) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skill.list.tree.pre"));
					for (String pageID : skillTree.getPageSet()) {
						sender.sendMessage(new TextComponentTranslation("misc.skilltreepage." + pageID.toLowerCase()));
					}
					return;
				}
				if (!skillTree.hasPage(args[1])) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skill.list.skill.fail"));
					return;
				}
				sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skill.list.skill.pre"));
				SkillTreePage page = skillTree.getPage(args[1]);
				for (String skillID : page.getSkillSet()) {
					sender.sendMessage(new TextComponentTranslation("misc.skill." + skillID.toLowerCase()));
				}
				return;
			}
			case "learn": {
				if (args.length == 1) {
					throw new WrongUsageException("command." + AoLCore.MODID + ".skill.learn.usage");
				}
				if (args.length == 2) {
					if (skillTree.learnSkill(player, args[1]))
						sender.sendMessage(
								new TextComponentTranslation("command." + AoLCore.MODID + ".skill.learn.success"));
					else
						sender.sendMessage(
								new TextComponentTranslation("command." + AoLCore.MODID + ".skill.learn.fail"));
					return;
				}
				boolean ignore = CommandBase.parseBoolean(args[2]);
				if (skillTree.learnSkill(player, args[1], ignore))
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skill.learn.success"));
				else
					sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skill.learn.fail"));
				return;
			}
			case "forget": {
				if (args.length == 1) {
					throw new WrongUsageException("command." + AoLCore.MODID + ".skill.forget.usage");
				}
				if (args.length == 2) {
					if (skillTree.forgetSkill(player, args[1]))
						sender.sendMessage(
								new TextComponentTranslation("command." + AoLCore.MODID + ".skill.forget.success"));
					else
						sender.sendMessage(
								new TextComponentTranslation("command." + AoLCore.MODID + ".skill.forget.fail"));
					return;
				}
				boolean ignore = CommandBase.parseBoolean(args[2]);
				if (skillTree.forgetSkill(player, args[1], ignore))
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skill.forget.success"));
				else
					sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skill.forget.fail"));
				return;
			}
			case "test": {
				AoLEventLoader.AOL_EVENT_BUS.post(new AoLEventLoader.TestEvent(sender));
				return;
			}
			default: {
				throw new WrongUsageException("command." + AoLCore.MODID + ".skill.usage");
			}
		}
	}
	
	@Override
	public String getName() {
		return "skill";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "command." + AoLCore.MODID + ".skill.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
	
}
