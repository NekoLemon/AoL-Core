package cn.catlemon.aol_core.command;

import java.util.List;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillPoint;
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

public class CommandSkillPoint extends CommandBase {
	
	/*
	 * private ITextComponent getSPTranslation(String skillPointTypeId) { return new
	 * TextComponentTranslation(skillPointTypeId); // return
	 * TranslateHandler.translateWithDefault("misc.skillpoint.unknown", //
	 * "misc.skillpoint." + skillPointTypeId.toLowerCase(), //
	 * skillPointTypeId.toLowerCase()); }
	 */
	
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
					NetworkHandler.network.sendTo(
							new PacketTranslate(new TranslateText(null,
									"command." + AoLCore.MODID + ".skillpoint.list.each",
									new TranslateText("misc.skillpoint.unknown",
											"misc.skillpoint." + typeList.get(i).toLowerCase(),
											typeList.get(i).toLowerCase()),
									skillPoint.getSPNum(typeList.get(i)))),
							player);
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
				if (!args[1].matches("[A-Za-z0-9_\\-\\.]+")) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.formaterror.2"));
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.add.usage");
				}
				skillPoint.addSPNum(player, args[1].toLowerCase(), num);
				NetworkHandler.network
						.sendTo(new PacketTranslate(
								new TranslateText(null, "command." + AoLCore.MODID + ".skillpoint.add.success",
										new TranslateText("misc.skillpoint.unknown",
												"misc.skillpoint." + args[1].toLowerCase(), args[1].toLowerCase()),
										args[2])),
								player);
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
				if (!args[1].matches("[A-Za-z0-9_\\-\\.]+")) {
					sender.sendMessage(
							new TextComponentTranslation("command." + AoLCore.MODID + ".skillpoint.formaterror.2"));
					throw new WrongUsageException("command." + AoLCore.MODID + ".skillpoint.sub.usage");
				}
				if (skillPoint.subSPNum(player, args[1].toLowerCase(), num))
					NetworkHandler.network.sendTo(new PacketTranslate(
							new TranslateText(null, "command." + AoLCore.MODID + ".skillpoint.sub.success",
									new TranslateText("misc.skillpoint.unknown",
											"misc.skillpoint." + args[1].toLowerCase(), args[1].toLowerCase()),
									args[2])),
							player);
				else
					NetworkHandler.network.sendTo(new PacketTranslate(
							new TranslateText(null, "command." + AoLCore.MODID + ".skillpoint.sub.fail",
									new TranslateText("misc.skillpoint.unknown",
											"misc.skillpoint." + args[1].toLowerCase(), args[1].toLowerCase()),
									args[2])),
							player);
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