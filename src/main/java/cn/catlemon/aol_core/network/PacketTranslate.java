package cn.catlemon.aol_core.network;

import javax.annotation.Nullable;

import cn.catlemon.aol_core.command.TranslateText;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketTranslate implements IMessage {
	private TranslateText _text;
	
	public PacketTranslate() {
	}
	
	public PacketTranslate(TranslateText text) {
		_text = text;
	}
	
	@SideOnly(Side.CLIENT)
	public ITextComponent getTextComponent() {
		return _text.getTextComponent();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound compound = ByteBufUtils.readTag(buf);
		_text = TranslateText.deserialize(compound);
		
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, _text.serialize());
	}
	
	public static class Handler implements IMessageHandler<PacketTranslate, IMessage> {
		@Nullable
		@Override
		public IMessage onMessage(PacketTranslate message, MessageContext mctx) {
			if (message._text == null
					|| (message._text.getDefaultLangKey().equals("") && message._text.getKey().equals(""))) {
				return null;
			}
			if (mctx.side == Side.CLIENT) {
				Minecraft.getMinecraft().addScheduledTask(new Runnable() {
					@Override
					public void run() {
						EntityPlayerSP player = Minecraft.getMinecraft().player;
						player.sendMessage(message.getTextComponent());
					}
				});
			}
			return null;
		}
		
	}
}
