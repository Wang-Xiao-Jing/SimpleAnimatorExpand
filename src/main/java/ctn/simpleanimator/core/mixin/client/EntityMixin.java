//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.core.animation.Animator;
import ctn.simpleanimator.core.client.ClientAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin({Entity.class})
public abstract class EntityMixin {
  @Shadow
  public double xo;
  @Shadow
  public double yo;
  @Shadow
  public double zo;

  @Shadow
  protected abstract Vec3 calculateViewVector(float var1, float var2);

  @Shadow
  public abstract Vec3 position();

  @Shadow
  public abstract Level level();

  @Shadow
  public abstract float getViewXRot(float var1);

  @Shadow
  public abstract float getViewYRot(float var1);

  @Shadow
  public abstract double getX();

  @Shadow
  public abstract double getY();

  @Shadow
  public abstract double getZ();

  @Shadow
  public abstract float getEyeHeight();

  @Inject(
    method = {"turn(DD)V"},
    at = {@At("HEAD")},
    cancellable = true
  )
  public void limitTurn(double pYRot, double pXRot, CallbackInfo ci) {
    if ((Object) this == Minecraft.getInstance().player) {
      Animator animator = ((IAnimateHandler) this).simpleanimator$getAnimator();
      if (animator.isRunning() && animator.getAnimation().isOverrideHead()) {
        ci.cancel();
      }

    }
  }

  @Inject(
    method = {"getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"},
    at = {@At("HEAD")},
    cancellable = true
  )
  public void getEyePositionDuringAnimating(float f, CallbackInfoReturnable<Vec3> cir) {
    if ((Object) this == Minecraft.getInstance().player) {
      ClientAnimator animator = (ClientAnimator) ((IAnimateHandler) this).simpleanimator$getAnimator();
      if (animator.isRunning() && animator.isProcessed() && !animator.getAnimation().isOverrideHead()) {
        Vector3f position = animator.getCameraPosition();
        double d0 = Mth.lerp(f, this.xo, this.getX()) + (double) position.x;
        double d1 = Mth.lerp(f, this.yo, this.getY()) + (double) this.getEyeHeight() + (double) position.y;
        double d2 = Mth.lerp(f, this.zo, this.getZ()) + (double) position.z;
        cir.setReturnValue(new Vec3(d0, d1, d2));
        cir.cancel();
      }

    }
  }

  @Inject(
    method = {"getViewVector(F)Lnet/minecraft/world/phys/Vec3;"},
    at = {@At("HEAD")},
    cancellable = true
  )
  public void getViewVectorDuringAnimating(float f, CallbackInfoReturnable<Vec3> cir) {
    if ((Object) this == Minecraft.getInstance().player) {
      ClientAnimator animator = (ClientAnimator) ((IAnimateHandler) this).simpleanimator$getAnimator();
      if (animator.isRunning() && animator.isProcessed() && !animator.getAnimation().isOverrideHead()) {
        Vector3f rotation = animator.getCameraRotation();
        cir.setReturnValue(this.calculateViewVector(this.getViewXRot(f) + rotation.x * (180F / (float) Math.PI), this.getViewYRot(f) + rotation.y * (180F / (float) Math.PI)));
        cir.cancel();
      }

    }
  }
}
