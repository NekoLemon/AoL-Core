package cn.catlemon.aol_core.command;

import java.util.List;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.ISkillPoint;
import cn.catlemon.aol_core.capability.CapabilityHandler;
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
			sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.fail"));
			return;
		}
		ISkillPoint skillPoint = player.getCapability(CapabilityHandler.capSkillPoint, null);
		switch (args[0]) {
			case "list": {
				List<String> typeList = skillPoint.getSkillPointTypeList();
				if (typeList.size() == 0) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.list.fail"));
					return;
				}
				sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.list.pre"));
				for (int i = 0; i < typeList.size(); i++) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.list.each",
									new TextComponentTranslation(
											"misc." + AoLCore.MODID + ".skillpoint." + typeList.get(i).toLowerCase())
													.getFormattedText(),
									skillPoint.getSkillPoint(typeList.get(i))));
				}
				break;
			}
			case "reset": {
				List<String> typeList = skillPoint.getSkillPointTypeList();
				for (int i = 0; i < typeList.size(); i++) {
					skillPoint.setSkillPoint(typeList.get(i), 0);
				}
				sender.sendMessage(
						new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.reset.success"));
				break;
			}
			case "add": {
				if (args.length < 3)
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.add.usage");
				int num = CommandBase.parseInt(args[2]);
				if (num < 0)
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.add.usage");
				skillPoint.addSkillPoint(args[1].toLowerCase(), num);
				sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.add.success",
						new TextComponentTranslation("misc." + AoLCore.MODID + ".skillpoint." + args[1].toLowerCase())
								.getFormattedText(),
						args[2]));
				break;
			}
			case "sub": {
				if (args.length < 3)
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.sub.usage");
				int num = CommandBase.parseInt(args[2]);
				if (num < 0)
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.sub.usage");
				skillPoint.subSkillPoint(args[1].toLowerCase(), num);
				sender.sendMessage(new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.sub.success",
						new TextComponentTranslation("misc." + AoLCore.MODID + ".skillpoint." + args[1].toLowerCase())
								.getFormattedText(),
						args[2]));
				break;
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
		}
		return null;
	}

}