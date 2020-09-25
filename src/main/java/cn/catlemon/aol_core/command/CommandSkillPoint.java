package cn.catlemon.aol_core.command;

import java.util.List;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillPoint;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSkillPoint extends CommandBase {
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0)
			throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.usage");
		EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
		if (!player.hasCapability(CapabilityHandler.capSkillPoint, null)) {
			AoLCore.LOGGER.error("玩家\"" + player.getName() + "\"没有SkillPoint的Capability，请寻求mod作者的帮助。");
			return;
		}
		ISkillPoint skillPoint = player.getCapability(CapabilityHandler.capSkillPoint, null);
		switch (args[0]) {
			case "list": {
				List<String> typeList = skillPoint.getSPTypeList();
				if (typeList.size() == 0) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.list.fail"));
					return;
				}
				sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.list.pre"));
				for (int i = 0; i < typeList.size(); i++) {
					String skillPointName = new TextComponentTranslation(
							"misc.skillpoint." + typeList.get(i).toLowerCase()).getUnformattedText();
					skillPointName = (skillPointName.equals("misc.skillpoint." + typeList.get(i).toLowerCase()))
							? new TextComponentTranslation("misc.skillpoint.unknown", typeList.get(i).toLowerCase())
									.getFormattedText()
							: skillPointName;
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.list.each",
									skillPointName, skillPoint.getSPNum(typeList.get(i))));
				}
				return;
			}
			case "reset": {
				skillPoint.reset();
				sender.sendMessage(
						new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.reset.success"));
				return;
			}
			case "add": {
				if (args.length < 3)
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.add.usage");
				int num = CommandBase.parseInt(args[2]);
				if (num < 0) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.formaterror.1"));
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.add.usage");
				}
				if (!args[1].matches("[A-Za-z0-9_-]+")) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.formaterror.2"));
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.add.usage");
				}
				String skillPointName = new TextComponentTranslation("misc.skillpoint." + args[1].toLowerCase())
						.getUnformattedText();
				skillPointName = (skillPointName.equals("misc.skillpoint." + args[1].toLowerCase()))
						? new TextComponentTranslation("misc.skillpoint.unknown", args[1].toLowerCase())
								.getFormattedText()
						: skillPointName;
				skillPoint.addSPNum(args[1].toLowerCase(), num);
				sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.add.success",
						skillPointName, args[2]));
				return;
			}
			case "sub": {
				if (args.length < 3)
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.sub.usage");
				int num = CommandBase.parseInt(args[2]);
				if (num < 0) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.formaterror.1"));
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.sub.usage");
				}
				if (!args[1].matches("[A-Za-z0-9_-]+")) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.formaterror.2"));
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.sub.usage");
				}
				String skillPointName = new TextComponentTranslation("misc.skillpoint." + args[1].toLowerCase())
						.getUnformattedText();
				skillPointName = (skillPointName.equals("misc.skillpoint." + args[1].toLowerCase()))
						? new TextComponentTranslation("misc.skillpoint.unknown", args[1].toLowerCase())
								.getFormattedText()
						: skillPointName;
				if (skillPoint.subSPNum(args[1].toLowerCase(), num))
					sender.sendMessage(new TextComponentTranslation(
							"command." + AoLCore.MODID + ".skillpoint.sub.success", skillPointName, args[2]));
				else
					sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.sub.fail",
							skillPointName, args[2]));
				return;
			}
			default: {
				throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.usage");
			}
		}
		
	}
	
	@Override
	public String getName() {
		return "skillpoint";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "command." + AoLCore.MODID + ".skillpoint.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			String[] commands = { "list", "reset", "add", "sub" };
			return CommandBase.getListOfStringsMatchingLastWord(args, commands);
		} else if (args.length == 2 && (args[0].equals("add") || args[0].equals("sub"))) {
			try {
				EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
				if (!player.hasCapability(CapabilityHandler.capSkillPoint, null)) {
					AoLCore.LOGGER.error("玩家\"" + player.getName() + "\"没有SkillPoint的Capability，请寻求mod作者的帮助。");
					return null;
				}
				ISkillPoint skillPoint = player.getCapability(CapabilityHandler.capSkillPoint, null);
				List<String> typeList = skillPoint.getSPTypeList();
				return CommandBase.getListOfStringsMatchingLastWord(args, typeList);
			} catch (CommandException e) {
				return null;
			}
		}
		return null;
	}
	
}