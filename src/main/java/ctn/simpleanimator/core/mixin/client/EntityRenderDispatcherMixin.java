//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.api.animation.ModelBone;
import ctn.simpleanimator.core.PlayerUtils;
import ctn.simpleanimator.core.client.ClientAnimator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
  @Inject(
    method = {"render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
    at = {@At(
      value = "INVOKE",
      target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
      ordinal = 0,
      shift = Shift.AFTER
    )}
  )
  public <E extends Entity> void applyRootTranslation(E entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
    if (!PlayerUtils.isRiding(entity)) {
      if (entity instanceof IAnimateHandler handler) {
        ClientAnimator animator = (ClientAnimator) handler.simpleanimator$getAnimator();
        if (animator.isRunning() && animator.isProcessed()) {
          ClientAnimator.Cache root = animator.getCache(ModelBone.ROOT);
          Vec3 position = PlayerUtils.getRelativePosition((Player) entity, root.position().z, root.position().x);
          poseStack.translate(position.x / (double) -16.0F, 0.0F, position.z / (double) -16.0F);
        }
      }

    }
  }
}
