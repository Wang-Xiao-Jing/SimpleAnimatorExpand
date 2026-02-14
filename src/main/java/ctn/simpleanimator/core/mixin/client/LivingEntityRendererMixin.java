//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import ctn.simpleanimator.api.animation.ModelBone;
import ctn.simpleanimator.core.PlayerUtils;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.client.ClientAnimator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({LivingEntityRenderer.class})
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
  @Unique
  @Nullable ClientAnimator simpleAnimator$animator;

  @Inject(
    method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
    at = {@At(
      value = "INVOKE",
      target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
      ordinal = 1
    )}
  )
  public void translateRoot(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
    if (!PlayerUtils.isRiding(pEntity)) {
      this.simpleAnimator$animator = SimpleAnimator.getClient().getClientAnimatorManager().getAnimator(pEntity.getUUID());
      if (this.simpleAnimator$animator != null && this.simpleAnimator$animator.isRunning() && this.simpleAnimator$animator.isProcessed()) {
        ClientAnimator.Cache root = this.simpleAnimator$animator.getCache(ModelBone.ROOT);
        pPoseStack.mulPose((new Quaternionf()).rotationXYZ(root.rotation().x, root.rotation().y, root.rotation().z));
        pPoseStack.translate(0.0F, root.position().y / -16.0F, 0.0F);
      }

    }
  }
}
