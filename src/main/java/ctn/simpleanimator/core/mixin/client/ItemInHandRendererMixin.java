//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.core.client.ClientAnimator;
import ctn.simpleanimator.core.client.util.IModelUpdater;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({ItemInHandRenderer.class})
public class ItemInHandRendererMixin implements IModelUpdater {
  @Shadow
  @Final
  private EntityRenderDispatcher entityRenderDispatcher;

  @Unique
  public void simpleAnimator$update(@NotNull AbstractClientPlayer pPlayer) {
    ClientAnimator animator = (ClientAnimator) ((IAnimateHandler) pPlayer).simpleanimator$getAnimator();
    if (animator.isRunning()) {
      PlayerRenderer playerrenderer = (PlayerRenderer) this.entityRenderDispatcher.getRenderer(pPlayer);
      PlayerModel<AbstractClientPlayer> model = playerrenderer.getModel();
      float yaw = pPlayer.yHeadRot - pPlayer.yBodyRot;
      float pitch = pPlayer.getXRot();
      model.setupAnim(pPlayer, 0.0F, 0.0F, 0.0F, yaw, pitch);
    }

  }

  @Inject(
    method = {"renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/player/LocalPlayer;I)V"},
    at = {@At("HEAD")}
  )
  public void simpleanimator$apply(float f, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LocalPlayer pPlayer, int i, CallbackInfo ci) {
    this.simpleAnimator$update(pPlayer);
  }
}
