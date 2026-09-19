package com.speedtweaks.client.gui;

import com.speedtweaks.config.SpeedConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SpeedMenuScreen extends Screen {
	private final Screen parent;
	private ButtonWidget infiniteFlyButton;
	private ButtonWidget infiniteMinecartButton;
	private ButtonWidget keepSpeedButton;
	private ValueSlider flySlider;
	private ValueSlider minecartSlider;

	public SpeedMenuScreen(Screen parent) {
		super(Text.literal("Speed Tweaks"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		SpeedConfig cfg = SpeedConfig.INSTANCE;
		int w = 200;
		int x = width / 2 - w / 2;
		int y = height / 2 - 70;

		flySlider = addDrawableChild(new ValueSlider(x, y, w, "Fly Speed",
				SpeedConfig.VANILLA_FLY_SPEED, SpeedConfig.MAX_FLY_SPEED, cfg.flySpeed,
				v -> cfg.flySpeed = (float) v));
		infiniteFlyButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			cfg.infiniteFly = !cfg.infiniteFly;
			refresh();
		}).dimensions(x, y + 24, w, 20).build());

		minecartSlider = addDrawableChild(new ValueSlider(x, y + 60, w, "Minecart Speed",
				SpeedConfig.VANILLA_MINECART_SPEED, SpeedConfig.MAX_MINECART_SPEED, cfg.minecartSpeed,
				v -> cfg.minecartSpeed = v));
		infiniteMinecartButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			cfg.infiniteMinecart = !cfg.infiniteMinecart;
			refresh();
		}).dimensions(x, y + 84, w, 20).build());

		keepSpeedButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			cfg.keepMinecartSpeed = !cfg.keepMinecartSpeed;
			refresh();
		}).dimensions(x, y + 108, w, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
				.dimensions(x, y + 140, w, 20).build());

		refresh();
	}

	private void refresh() {
		SpeedConfig cfg = SpeedConfig.INSTANCE;
		infiniteFlyButton.setMessage(Text.literal("Infinite Fly Speed: " + (cfg.infiniteFly ? "ON" : "OFF")));
		infiniteMinecartButton.setMessage(Text.literal("Infinite Minecart Speed: " + (cfg.infiniteMinecart ? "ON" : "OFF")));
		keepSpeedButton.setMessage(Text.literal("Keep Cart Speed (no powered rails): " + (cfg.keepMinecartSpeed ? "ON" : "OFF")));
		flySlider.active = !cfg.infiniteFly;
		minecartSlider.active = !cfg.infiniteMinecart;
	}

	@Override
	public void close() {
		SpeedConfig.save();
		client.setScreen(parent);
	}

	@Override
	public void render(net.minecraft.client.gui.DrawContext ctx, int mouseX, int mouseY, float delta) {
		renderBackground(ctx);
		ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 100, 0xFFFFFF);
		super.render(ctx, mouseX, mouseY, delta);
	}

	private static class ValueSlider extends SliderWidget {
		private final String label;
		private final double min;
		private final double max;
		private final java.util.function.DoubleConsumer onChange;

		ValueSlider(int x, int y, int width, String label, double min, double max, double current,
					java.util.function.DoubleConsumer onChange) {
			super(x, y, width, 20, Text.empty(), (MathHelper.clamp(current, min, max) - min) / (max - min));
			this.label = label;
			this.min = min;
			this.max = max;
			this.onChange = onChange;
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			setMessage(Text.literal(String.format("%s: %.2f", label, min + value * (max - min))));
		}

		@Override
		protected void applyValue() {
			onChange.accept(min + value * (max - min));
		}
	}
}
