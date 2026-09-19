package com.speedtweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpeedConfig {
	public static final float VANILLA_FLY_SPEED = 0.05f;
	public static final double VANILLA_MINECART_SPEED = 0.4;

	public static final float MAX_FLY_SPEED = 5.0f;
	public static final double MAX_MINECART_SPEED = 10.0;

	// "Infinite" is a very large finite value: real infinity/NaN would corrupt entity positions and crash the game.
	private static final float INFINITE_FLY_SPEED = 1000f;
	private static final double INFINITE_MINECART_SPEED = 1000.0;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("speedtweaks.json");

	public static SpeedConfig INSTANCE = new SpeedConfig();

	public boolean infiniteFly = false;
	public float flySpeed = VANILLA_FLY_SPEED;
	public boolean infiniteMinecart = false;
	public double minecartSpeed = VANILLA_MINECART_SPEED;
	public boolean keepMinecartSpeed = false;
	public boolean syncRealTime = false;

	public float effectiveFlySpeed() {
		return infiniteFly ? INFINITE_FLY_SPEED : flySpeed;
	}

	public double effectiveMinecartSpeed() {
		return infiniteMinecart ? INFINITE_MINECART_SPEED : minecartSpeed;
	}

	public static void load() {
		if (!Files.exists(PATH)) {
			save();
			return;
		}
		try {
			SpeedConfig loaded = GSON.fromJson(Files.readString(PATH), SpeedConfig.class);
			if (loaded != null) {
				INSTANCE = loaded;
			}
		} catch (Exception e) {
			INSTANCE = new SpeedConfig();
		}
	}

	public static void save() {
		try {
			Files.writeString(PATH, GSON.toJson(INSTANCE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
