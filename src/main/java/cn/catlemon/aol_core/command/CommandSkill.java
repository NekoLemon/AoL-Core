package cn.catlemon.aol_core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.AoLEventLoader;
import cn.catlemon.aol_core.api.SkillBase;
import cn.catlemon.aol_core.api.SkillTreePage;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillTree;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.network.PacketTranslate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSkill extends CommandBase {
	
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
							new TextComponentTranslation("command." + AoLCore.MODID + ".skill.list.page.pre"));
					List<String> pageList = new ArrayList<String>(skillTree.getPageSet());
					Collections.sort(pageList);
					for (String pageID : pageList) {
						NetworkHandler.network.sendTo(new PacketTranslate(new TranslateText("misc.skillpage.unknown",
								"misc.skillpage." + pageID.toLowerCase(), pageID.toLowerCase())), player);
					}
					return;
				}
				if (!skillTree.hasPage(args[1])) {
					NetworkHandler.network.sendTo(
							new PacketTranslate(
									new TranslateText(null, "command." + AoLCore.MODID + ".skill.list.skill.fail",
											new TranslateText("misc.skillpage.unknown",
													"misc.skillpage." + args[1].toLowerCase(), args[1].toLowerCase()))),
							player);
					return;
				}
				NetworkHandler.network.sendTo(
						new PacketTranslate(
								new TranslateText("command." + AoLCore.MODID + ".skill.list.skill.pre",
										"command." + AoLCore.MODID + ".skill.list.skill.pre",
										new TranslateText("misc.skillpage.unknown",
												"misc.skillpage." + args[1].toLowerCase(), args[1].toLowerCase()))),
						player);
				SkillTreePage page = skillTree.getPage(args[1]);
				List<String> skillList = new ArrayList<String>(page.getSkillSet());
				Collections.sort(skillList);
				for (String skillID : skillList) {
					SkillBase skill = page.getSkill(skillID);
					NetworkHandler.network.sendTo(new PacketTranslate(new TranslateText(null,
							"command." + AoLCore.MODID + ".skill.list.skill.each",
							new TranslateText("misc.skill.unknown", "misc.skill." + skillID.toLowerCase(),
									skillID.toLowerCase()),
							new TextComponentTranslation(
									"command." + AoLCore.MODID + ".skill.list.skill.status." + skill.isLearned()))),
							player);
				}
				return;
			}
			case "learn": {
				if (args.length == 1) {
					throw new WrongUsageException("command." + AoLCore.MODID + ".skill.learn.usage");
				}
				if (args.length == 2) {
					if (skillTree.learnSkill(player, args[1]))
						NetworkHandler.network.sendTo(
								new PacketTranslate(
										new TranslateText(null, "command." + AoLCore.MODID + ".skill.learn.success",
												new TranslateText("misc.skill.unknown",
														"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
								player);
					else
						NetworkHandler.network.sendTo(
								new PacketTranslate(
										new TranslateText(null, "command." + AoLCore.MODID + ".skill.learn.fail",
												new TranslateText("misc.skill.unknown",
														"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
								player);
					return;
				}
				boolean ignore = CommandBase.parseBoolean(args[2]);
				if (skillTree.learnSkill(player, args[1], ignore))
					NetworkHandler.network.sendTo(
							new PacketTranslate(
									new TranslateText(null, "command." + AoLCore.MODID + ".skill.learn.success",
											new TranslateText("misc.skill.unknown",
													"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
							player);
				else
					NetworkHandler.network.sendTo(
							new PacketTranslate(
									new TranslateText(null, "command." + AoLCore.MODID + ".skill.learn.fail",
											new TranslateText("misc.skill.unknown",
													"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
							player);
				return;
			}
			case "forget": {
				if (args.length == 1) {
					throw new WrongUsageException("command." + AoLCore.MODID + ".skill.forget.usage");
				}
				if (args.length == 2) {
					if (skillTree.forgetSkill(player, args[1]))
						NetworkHandler.network.sendTo(
								new PacketTranslate(
										new TranslateText(null, "command." + AoLCore.MODID + ".skill.forget.success",
												new TranslateText("misc.skill.unknown",
														"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
								player);
					else
						NetworkHandler.network.sendTo(
								new PacketTranslate(
										new TranslateText(null, "command." + AoLCore.MODID + ".skill.forget.fail",
												new TranslateText("misc.skill.unknown",
														"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
								player);
					return;
				}
				boolean ignore = CommandBase.parseBoolean(args[2]);
				if (skillTree.forgetSkill(player, args[1], ignore))
					NetworkHandler.network.sendTo(
							new PacketTranslate(
									new TranslateText(null, "command." + AoLCore.MODID + ".skill.forget.success",
											new TranslateText("misc.skill.unknown",
													"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
							player);
				else
					NetworkHandler.network.sendTo(
							new PacketTranslate(
									new TranslateText(null, "command." + AoLCore.MODID + ".skill.forget.fail",
											new TranslateText("misc.skill.unknown",
													"misc.skill." + args[1].toLowerCase(), args[1].toLowerCase()))),
							player);
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
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			String[] commands = { "list", "learn", "forget" };
			return CommandBase.getListOfStringsMatchingLastWord(args, commands);
		} else if (args.length == 2 && args[0].equals("list")) {
			try {
				EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
				if (!player.hasCapability(CapabilityHandler.capSkillTree, null)) {
					AoLCore.LOGGER.error("玩家\"" + player.getName() + "\"没有SkillTree的Capability，请寻求mod作者的帮助。");
					return null;
				}
				ISkillTree skillTree = player.getCapability(CapabilityHandler.capSkillTree, null);
				Set<String> typeList = skillTree.getPageSet();
				return CommandBase.getListOfStringsMatchingLastWord(args, typeList);
			} catch (CommandException e) {
				return null;
			}
		} else if (args.length == 2 && (args[0].equals("learn") || args[0].equals("forget"))) {
			try {
				EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
				if (!player.hasCapability(CapabilityHandler.capSkillTree, null)) {
					AoLCore.LOGGER.error("玩家\"" + player.getName() + "\"没有SkillTree的Capability，请寻求mod作者的帮助。");
					return null;
				}
				ISkillTree skillTree = player.getCapability(CapabilityHandler.capSkillTree, null);
				Set<String> skillSet = new HashSet<String>();
				for (String pageID : skillTree.getPageSet()) {
					SkillTreePage page = skillTree.getPage(pageID);
					skillSet.addAll(page.getSkillSet());
				}
				return CommandBase.getListOfStringsMatchingLastWord(args, skillSet);
			} catch (CommandException e) {
				return null;
			}
		}
		return null;
	}
	
}
