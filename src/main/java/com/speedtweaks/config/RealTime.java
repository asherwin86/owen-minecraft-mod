package com.speedtweaks.config;

import java.time.LocalTime;

public final class RealTime {
	private RealTime() {}

	// Real 06:00 is Minecraft sunrise (tick 0), 12:00 is noon (6000), 18:00 is sunset (12000).
	public static long minecraftTimeOfDay() {
		double dayFraction = LocalTime.now().toNanoOfDay() / 86_400_000_000_000.0;
		return ((long) (dayFraction * 24000) + 18000) % 24000;
	}
}
