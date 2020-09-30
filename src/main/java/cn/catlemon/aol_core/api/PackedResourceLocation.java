package cn.catlemon.aol_core.api;

import javax.annotation.Nonnull;

import cn.catlemon.aol_core.AoLCore;
import net.minecraft.util.ResourceLocation;

public abstract class PackedResourceLocation {
	public ResourceLocation resourceLocation = null;
	
	public Coordinate<Integer> offset = new Coordinate<Integer>(0);
	
	public Integer width = null;
	
	public Integer height = null;
	
	public Integer canvasWidth = null;
	
	public Integer canvasHeight = null;
	
	public boolean customSized = false;
	
	public PackedResourceLocation(String ModId, String location) {
		AoLCore.LOGGER.fatal(ModId);
		this.resourceLocation = new ResourceLocation(ModId, location);
	}
	
	public PackedResourceLocation(String ModId, String location, @Nonnull Coordinate<Integer> offset) {
		this.resourceLocation = new ResourceLocation(ModId, location);
		this.offset = offset;
	}
	
	public PackedResourceLocation(String ModId, String location, @Nonnull Coordinate<Integer> offset,
			@Nonnull Integer width, @Nonnull Integer height) {
		this.resourceLocation = new ResourceLocation(ModId, location);
		this.offset = offset;
		this.width = width;
		this.height = height;
	}
	
	public PackedResourceLocation(String ModId, String location, @Nonnull Coordinate<Integer> offset,
			@Nonnull Integer width, @Nonnull Integer height, @Nonnull Integer canvasWidth,
			@Nonnull Integer canvasHeight) {
		this.resourceLocation = new ResourceLocation(ModId, location);
		this.offset = offset;
		this.width = width;
		this.height = height;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.customSized = true;
	}
}
