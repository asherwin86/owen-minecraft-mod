package com.speedtweaks;

import com.speedtweaks.config.SpeedConfig;
import com.speedtweaks.config.RealTime;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.World;

public class SpeedTweaks implements ModInitializer {
	public static final String MOD_ID = "speedtweaks";

	@Override
	public void onInitialize() {
		SpeedConfig.load();
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (SpeedConfig.INSTANCE.syncRealTime && world.getRegistryKey() == World.OVERWORLD) {
				world.setTimeOfDay(RealTime.minecraftTimeOfDay());
			}
		});
	}
}
