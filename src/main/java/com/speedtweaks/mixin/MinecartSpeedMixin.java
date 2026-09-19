package com.speedtweaks.mixin;

import com.speedtweaks.config.SpeedConfig;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class MinecartSpeedMixin {
	@Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
	private void speedtweaks$overrideMaxSpeed(CallbackInfoReturnable<Double> cir) {
		cir.setReturnValue(SpeedConfig.INSTANCE.effectiveMinecartSpeed());
	}
}
