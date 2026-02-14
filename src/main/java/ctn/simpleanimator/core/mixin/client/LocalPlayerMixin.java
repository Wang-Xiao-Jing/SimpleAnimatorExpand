//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.client;

import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.api.IInteractHandler;
import ctn.simpleanimator.api.animation.Animation;
import ctn.simpleanimator.api.animation.ModelBone;
import ctn.simpleanimator.api.animation.RequestHolder;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.client.ClientAnimator;
import ctn.simpleanimator.core.client.ClientPlayerNavigator;
import ctn.simpleanimator.core.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin({LocalPlayer.class})
public class LocalPlayerMixin {
  @Shadow
  public Input input;

  @Inject(
    method = {"aiStep()V"},
    at = {@At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/player/Input;tick(ZF)V",
      shift = Shift.AFTER
    )}
  )
  public void simpleanimator$limitMove(CallbackInfo ci) {
    ClientProxy client = SimpleAnimator.getClient();
    boolean hasInput = this.input.forwardImpulse != 0.0F || this.input.leftImpulse != 0.0F || this.input.jumping || this.input.shiftKeyDown;
    if (hasInput) {
      ClientPlayerNavigator navigator = client.getNavigator();
      if (navigator.isNavigating()) {
        navigator.stop(false);
      }

      LocalPlayer player = Minecraft.getInstance().player;
      RequestHolder request = ((IInteractHandler) player).simpleanimator$getRequest();
      if (request.hasRequest()) {
        ((IInteractHandler) player).simpleanimator$cancelInteract(true);
        this.input.forwardImpulse = 0.0F;
        this.input.leftImpulse = 0.0F;
        this.input.jumping = false;
        this.input.shiftKeyDown = false;
        return;
      }

      ClientAnimator animator = (ClientAnimator) ((IAnimateHandler) player).simpleanimator$getAnimator();
      if (animator.isRunning()) {
        Animation animation = animator.getAnimation();
        if (!animation.isMovable()) {
          this.input.forwardImpulse = 0.0F;
          this.input.leftImpulse = 0.0F;
          this.input.jumping = false;
          this.input.shiftKeyDown = false;
        } else {
          if (animation.isOverride(ModelBone.BODY)) {
            this.input.shiftKeyDown = false;
          }

          if (animation.isOverride(ModelBone.LEFT_LEG) || animation.isOverride(ModelBone.RIGHT_LEG)) {
            this.input.forwardImpulse = 0.0F;
            this.input.leftImpulse = 0.0F;
          }
        }

        if (animation.isAbortable() && animator.canStop()) {
          ((IAnimateHandler) player).simpleanimator$stopAnimate(true);
        }
      }
    }

  }
}
