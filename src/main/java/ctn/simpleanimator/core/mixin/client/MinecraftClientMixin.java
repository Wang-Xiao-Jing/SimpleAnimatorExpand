//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.animation.Animator;
import ctn.simpleanimator.core.mixin.accessor.KeyMappingAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({Minecraft.class})
public abstract class MinecraftClientMixin {
  @Shadow
  @Final
  public Options options;
  @Shadow
  public @Nullable LocalPlayer player;
  @Shadow
  @Final
  private DeltaTracker.Timer timer;
  @Shadow
  public @Nullable ClientLevel level;

  @Shadow
  public abstract boolean isPaused();

  @Inject(
    method = {"handleKeybinds()V"},
    at = {@At("HEAD")}
  )
  public void cancelKeyInput(CallbackInfo ci) {
    Animator animator = ((IAnimateHandler) this.player).simpleanimator$getAnimator();
    if (animator.isRunning() && animator.getAnimation().isOverrideHands()) {
      ((KeyMappingAccessor) this.options.keyUse).simpleanimator$release();
      ((KeyMappingAccessor) this.options.keyAttack).simpleanimator$release();
    }

  }

  @Inject(
    method = {"runTick(Z)V"},
    at = {@At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/DeltaTracker$Timer;advanceTime(JZ)I",
      shift = Shift.AFTER
    )}
  )
  public void tickAnimators(boolean bl, CallbackInfo ci) {
    if (!this.isPaused() && this.level != null) {
      SimpleAnimator.getClient().getAnimatorManager().tick(this.timer.getGameTimeDeltaTicks() / 20.0F);
    }

  }
}
