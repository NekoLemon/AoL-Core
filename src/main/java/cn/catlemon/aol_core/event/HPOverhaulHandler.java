package cn.catlemon.aol_core.event;

public class HPOverhaulHandler {
	public HPOverhaulHandler() {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
	}

	public void finalize() {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(this);
	}
}
