//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.model;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.client.ClientAnimator;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({PlayerModel.class})
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
  @Shadow
  @Final
  public ModelPart leftPants;
  @Shadow
  @Final
  public ModelPart rightPants;
  @Shadow
  @Final
  public ModelPart leftSleeve;
  @Shadow
  @Final
  public ModelPart rightSleeve;
  @Shadow
  @Final
  public ModelPart jacket;

  public PlayerModelMixin(ModelPart pRoot) {
    super(pRoot);
  }

  @Inject(
    method = {"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"},
    at = {@At("HEAD")}
  )
  public void resetModelParts(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
    this.head.resetPose();
    this.body.resetPose();
    this.leftArm.resetPose();
    this.rightArm.resetPose();
    this.leftLeg.resetPose();
    this.rightLeg.resetPose();
  }

  @Inject(
    method = {"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"},
    at = {@At("RETURN")}
  )
  public void process(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
    ClientAnimator animator = SimpleAnimator.getClient().getClientAnimatorManager().getAnimator(pEntity.getUUID());
    if (animator != null && animator.isRunning()) {
      animator.process((PlayerModel) (Object) this, (Player) pEntity);
      this.hat.copyFrom(this.head);
      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
    }

  }
}
