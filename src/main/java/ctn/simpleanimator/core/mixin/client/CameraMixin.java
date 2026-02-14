//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import ctn.simpleanimator.api.animation.ModelBone;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.client.ClientAnimator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({Camera.class})
public abstract class CameraMixin {
  @Shadow
  private Vec3 position;
  @Shadow
  private float xRot;
  @Shadow
  private float yRot;

  @Shadow
  protected abstract void setPosition(double var1, double var3, double var5);

  @Shadow
  protected abstract void setRotation(float var1, float var2);

  @Shadow
  public abstract Quaternionf rotation();

  @Inject(
    method = {"setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"},
    at = {@At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/Camera;setPosition(DDD)V",
      shift = Shift.AFTER
    )}
  )
  public void applyAnimation(BlockGetter pLevel, Entity pEntity, boolean pDetached, boolean pThirdPersonReverse, float pPartialTick, CallbackInfo ci) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (player != null) {
      ClientAnimator animator = SimpleAnimator.getClient().getClientAnimatorManager().getLocalAnimator();
      if (animator.isRunning() && animator.isProcessed() && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
        Vector3f position = animator.getCameraPosition();
        Vec2 vec2 = new Vec2(0.0F, player.yBodyRot);
        float f = Mth.cos((vec2.y + 90.0F) * ((float) Math.PI / 180F));
        float f1 = Mth.sin((vec2.y + 90.0F) * ((float) Math.PI / 180F));
        Vec3 vec31 = new Vec3(f, 0.0F, f1);
        Vec3 vec33 = new Vec3(-f1, 0.0F, f);
        double d0 = vec31.x * (double) position.z + vec33.x * (double) position.x;
        double d2 = vec31.z * (double) position.z + vec33.z * (double) position.x;
        this.setPosition(this.position.x + d0, this.position.y + (double) position.y, this.position.z + d2);
        if (animator.getAnimation().isOverride(ModelBone.HEAD)) {
          Vector3f rotation = animator.getCameraRotation();
          float yRot = player.yHeadRot - player.yBodyRot;
          float xRot = player.getXRot();
          this.setRotation(this.yRot + rotation.y * (180F / (float) Math.PI) - yRot, this.xRot + rotation.x * (180F / (float) Math.PI) - xRot);
          this.rotation().rotateZ(rotation.z);
        }
      }

    }
  }
}
