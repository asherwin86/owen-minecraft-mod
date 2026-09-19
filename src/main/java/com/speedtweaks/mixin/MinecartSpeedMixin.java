package com.speedtweaks.mixin;

import com.speedtweaks.config.SpeedConfig;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class MinecartSpeedMixin {
	// Vanilla only follows one rail block per tick, so it is only reliable up to ~0.4 blocks/tick.
	private static final double STEP = 0.4;
	private static final int MAX_STEPS = 250;
	private static final double MIN_MOVING = 0.02;
	private static final double POWERED_BOOST = 0.06;

	@Unique
	private boolean speedtweaks$stepping;

	@Shadow
	protected abstract void moveOnRail(BlockPos pos, BlockState state);

	@Shadow
	protected abstract void applySlowdown();

	@Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
	private void speedtweaks$overrideMaxSpeed(CallbackInfoReturnable<Double> cir) {
		cir.setReturnValue(SpeedConfig.INSTANCE.effectiveMinecartSpeed());
	}

	@Inject(method = "moveOnRail", at = @At("HEAD"), cancellable = true)
	private void speedtweaks$moveOnRail(BlockPos pos, BlockState state, CallbackInfo ci) {
		if (speedtweaks$stepping) {
			return;
		}
		AbstractMinecartEntity self = (AbstractMinecartEntity) (Object) this;
		Vec3d v = self.getVelocity();
		double speed = v.horizontalLength();
		if (speed < MIN_MOVING) {
			return;
		}

		SpeedConfig cfg = SpeedConfig.INSTANCE;
		double target = Math.min(cfg.effectiveMinecartSpeed(), MAX_STEPS * STEP);
		boolean keep = cfg.keepMinecartSpeed && !speedtweaks$isBrake(state);
		double newSpeed = keep ? target : Math.min(speed, target);
		Vec3d dir = new Vec3d(v.x / speed, 0, v.z / speed);

		if (newSpeed <= STEP) {
			if (keep) {
				self.setVelocity(dir.x * newSpeed, v.y, dir.z * newSpeed);
			}
			return;
		}

		int n = (int) Math.ceil(newSpeed / STEP);
		double stepSpeed = newSpeed / n;
		boolean powered = false;
		boolean braked = false;

		speedtweaks$stepping = true;
		try {
			BlockPos p = pos;
			BlockState s = state;
			for (int i = 0; i < n; i++) {
				if (i > 0) {
					Vec3d ahead = self.getPos().add(dir.x * stepSpeed, 0, dir.z * stepSpeed);
					BlockPos aheadPos = new BlockPos(MathHelper.floor(ahead.x), MathHelper.floor(ahead.y), MathHelper.floor(ahead.z));
					if (!self.getWorld().isChunkLoaded(aheadPos)) {
						break;
					}
					p = speedtweaks$railPos(self);
					s = self.getWorld().getBlockState(p);
					if (!AbstractRailBlock.isRail(s)) {
						break;
					}
				}
				if (s.isOf(Blocks.POWERED_RAIL)) {
					if (s.get(PoweredRailBlock.POWERED)) {
						powered = true;
					} else {
						braked = true;
					}
				}
				self.setVelocity(dir.x * stepSpeed, self.getVelocity().y, dir.z * stepSpeed);
				moveOnRail(p, s);
				Vec3d nv = self.getVelocity();
				double h = nv.horizontalLength();
				if (h > 1.0E-6) {
					dir = new Vec3d(nv.x / h, 0, nv.z / h);
				}
			}
		} finally {
			speedtweaks$stepping = false;
		}

		// Each step applied drag/boosts on its own, so set the tick's final speed once here.
		boolean keepOn = cfg.keepMinecartSpeed;
		double finalSpeed = newSpeed;
		if (braked) {
			finalSpeed = keepOn ? 0 : finalSpeed * 0.5;
		} else if (powered && !keepOn) {
			finalSpeed = Math.min(target, finalSpeed + POWERED_BOOST);
		}
		self.setVelocity(dir.x * finalSpeed, 0, dir.z * finalSpeed);
		if (!keepOn) {
			applySlowdown();
		}
		ci.cancel();
	}

	@Unique
	private static boolean speedtweaks$isBrake(BlockState state) {
		return state.isOf(Blocks.POWERED_RAIL) && !state.get(PoweredRailBlock.POWERED);
	}

	@Unique
	private static BlockPos speedtweaks$railPos(AbstractMinecartEntity self) {
		int x = MathHelper.floor(self.getX());
		int y = MathHelper.floor(self.getY());
		int z = MathHelper.floor(self.getZ());
		if (self.getWorld().getBlockState(new BlockPos(x, y - 1, z)).isIn(BlockTags.RAILS)) {
			y--;
		}
		return new BlockPos(x, y, z);
	}
}
