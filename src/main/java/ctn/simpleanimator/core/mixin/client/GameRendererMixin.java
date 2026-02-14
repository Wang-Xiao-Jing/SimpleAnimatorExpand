//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.core.animation.Animator;
import ctn.simpleanimator.core.client.util.IModelUpdater;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin({GameRenderer.class})
public class GameRendererMixin {
  @Shadow
  @Final
  private Camera mainCamera;
  @Shadow
  @Final
  public ItemInHandRenderer itemInHandRenderer;
  @Shadow
  @Final
  Minecraft minecraft;

  @Inject(
    method = {"shouldRenderBlockOutline()Z"},
    at = {@At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/LivingEntity;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"
    )},
    cancellable = true
  )
  public void simpleanimator$dontRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
    if (this.minecraft.player != null) {
      ((IModelUpdater) this.itemInHandRenderer).simpleAnimator$update(this.minecraft.player);
      Animator animator = ((IAnimateHandler) this.minecraft.player).simpleanimator$getAnimator();
      if (animator.isRunning() && animator.getAnimation().isOverrideHead()) {
        cir.setReturnValue(false);
        cir.cancel();
      }

    }
  }
}
