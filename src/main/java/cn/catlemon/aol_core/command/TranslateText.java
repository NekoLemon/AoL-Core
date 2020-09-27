package cn.catlemon.aol_core.command;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TranslateText {
	private String defaultLangKey;
	private String key;
	private Object[] args;
	
	public TranslateText(@Nullable String defaultLangKey, @Nonnull String key, Object... args) {
		this.defaultLangKey = (defaultLangKey == null) ? key : defaultLangKey;
		this.key = key;
		this.args = args;
	}
	
	public TranslateText(TextComponentTranslation text) {
		this.key = text.getKey();
		this.args = text.getFormatArgs();
	}
	
	@SideOnly(Side.CLIENT)
	private TextComponentTranslation translateTextToTextComponent(TranslateText text) {
		Object[] obj = text.getArgs();
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] instanceof TranslateText)
					obj[i] = translateTextToTextComponent((TranslateText) obj[i]);
			}
		}
		if (new TextComponentTranslation(text.getKey(), text.getArgs()).getUnformattedText().equals(text.getKey()))
			return new TextComponentTranslation(text.getDefaultLangKey(), text.getArgs());
		return new TextComponentTranslation(text.getKey(), text.getArgs());
	}
	
	@SideOnly(Side.CLIENT)
	public ITextComponent getTextComponent() {
		return translateTextToTextComponent(this);
	}
	
	public String getDefaultLangKey() {
		return this.defaultLangKey;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public Object[] getArgs() {
		return this.args;
	}
	
	public String toString() {
		return "TranslateText{default='" + this.defaultLangKey + '\'' + ",key='" + this.key + '\'' + ", args="
				+ Arrays.toString(this.args) + '}';
	}
	
	public NBTTagCompound serialize() {
		return serialize(this);
	}
	
	public static NBTTagCompound serialize(TranslateText text) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("defaultkey", text.getDefaultLangKey());
		compound.setString("key", text.getKey());
		if (text.getArgs() != null && text.getArgs().length > 0) {
			NBTTagList list = new NBTTagList();
			for (Object obj : text.getArgs()) {
				NBTTagCompound arg = new NBTTagCompound();
				if (obj instanceof TranslateText) {
					arg.setTag("tt", serialize((TranslateText) obj));
				} else if (obj instanceof ITextComponent) {
					arg.setString("itc", ITextComponent.Serializer.componentToJson((ITextComponent) obj));
				} else {
					JsonPrimitive jPrimitive = new JsonPrimitive(String.valueOf(obj));
					Gson gson = new Gson();
					arg.setString("json", gson.toJson(jPrimitive));
				}
				list.appendTag(arg);
			}
			compound.setTag("args", list);
		}
		return compound;
	}
	
	public static TranslateText deserialize(NBTBase nbt) {
		NBTTagCompound compound = (NBTTagCompound) nbt;
		String defaultKey = compound.getString("defaultkey");
		String key = compound.getString("key");
		if (compound.hasKey("args")) {
			NBTTagList list = (NBTTagList) compound.getTag("args");
			Object[] obj = new Object[list.tagCount()];
			for (int i = 0; i < obj.length; i++) {
				NBTTagCompound arg = list.getCompoundTagAt(i);
				if (arg.hasKey("tt"))
					obj[i] = deserialize(arg.getTag("tt"));
				else if (arg.hasKey("itc"))
					obj[i] = ITextComponent.Serializer.jsonToComponent(arg.getString("itc"));
				else if (arg.hasKey("json")) {
					JsonParser jsonParser = new JsonParser();
					obj[i] = new TextComponentString(jsonParser.parse(arg.getString("json")).getAsString());
				} else
					obj[i] = null;
			}
			return new TranslateText(defaultKey, key, obj);
		}
		return new TranslateText(defaultKey, key);
	}
	
}
