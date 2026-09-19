package com.speedtweaks;

import com.speedtweaks.config.SpeedConfig;
import net.fabricmc.api.ModInitializer;

public class SpeedTweaks implements ModInitializer {
	public static final String MOD_ID = "speedtweaks";

	@Override
	public void onInitialize() {
		SpeedConfig.load();
	}
}
