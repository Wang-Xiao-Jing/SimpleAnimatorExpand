//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin;

import com.mojang.authlib.GameProfile;
import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.api.IInteractHandler;
import ctn.simpleanimator.api.INavigatable;
import ctn.simpleanimator.api.animation.RequestHolder;
import ctn.simpleanimator.api.event.common.*;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.animation.Animator;
import ctn.simpleanimator.core.network.packet.*;
import ctn.simpleanimator.core.proxy.CommonProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Player.class})
public abstract class PlayerMixin extends LivingEntity implements IAnimateHandler, IInteractHandler, INavigatable {
  @Unique
  private Animator simpleanimator$animator;
  @Unique
  private RequestHolder simpleanimator$request;

  protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
    super(entityType, level);
  }

  @Shadow
  public abstract boolean isLocalPlayer();

  @Inject(
    method = {"<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;FLcom/mojang/authlib/GameProfile;)V"},
    at = {@At("RETURN")}
  )
  public void createAnimator(Level level, BlockPos blockPos, float f, GameProfile gameProfile, CallbackInfo ci) {
    CommonProxy proxy = SimpleAnimator.getProxy();
    this.simpleanimator$animator = proxy.getAnimatorManager().createIfAbsent(this.getUUID());
    this.simpleanimator$request = proxy.getInteractionManager().createIfAbsent(this.getUUID());
    this.simpleanimator$animator.reset(false);
    this.simpleanimator$request.reset();
  }

  @Unique
  public boolean simpleanimator$isRunning() {
    return this.simpleanimator$animator.isRunning();
  }

  @Unique
  public boolean simpleanimator$playAnimate(@NotNull ResourceLocation animation, boolean update) {
    if (SimpleAnimator.EVENT_BUS.post(new AnimatePlayEvent.Pre(simpleAnimatorExpand$getPlayerThis(), animation)).isCanceled()) {
      return false;
    } else if (!this.simpleanimator$animator.play(animation)) {
      return false;
    } else {
      this.setYBodyRot(this.getYHeadRot());
      if (update && this.isLocalPlayer()) {
        SimpleAnimator.getNetwork().update(new AnimatorPlayPacket(this.getUUID(), animation));
      }

      SimpleAnimator.EVENT_BUS.post(new AnimatePlayEvent.Post(simpleAnimatorExpand$getPlayerThis(), animation));
      return true;
    }
  }

  @Unique
  public boolean simpleanimator$stopAnimate(boolean update) {
    ResourceLocation animationID = this.simpleanimator$animator.getAnimationLocation();
    if (SimpleAnimator.EVENT_BUS.post(new AnimateStopEvent.Pre(simpleAnimatorExpand$getPlayerThis(), animationID)).isCanceled()) {
      return false;
    } else if (!this.simpleanimator$animator.stop()) {
      return false;
    } else {
      if (update && this.isLocalPlayer()) {
        SimpleAnimator.getNetwork().update(new AnimatorStopPacket(this.getUUID()));
      }

      SimpleAnimator.EVENT_BUS.post(new AnimatePlayEvent.Post(simpleAnimatorExpand$getPlayerThis(), animationID));
      return true;
    }
  }

  @Unique
  public @NotNull Animator simpleanimator$getAnimator() {
    return this.simpleanimator$animator;
  }

  @Unique
  public boolean simpleanimator$inviteInteract(@NotNull Player target, @NotNull ResourceLocation interaction, boolean update) {
    if (SimpleAnimator.EVENT_BUS.post(new InteractInviteEvent.Pre(simpleAnimatorExpand$getPlayerThis(), target, interaction)).isCanceled()) {
      return false;
    } else if (!SimpleAnimator.getProxy().getInteractionManager().invite(simpleAnimatorExpand$getPlayerThis(), target, interaction)) {
      return false;
    } else {
      if (update && this.isLocalPlayer()) {
        SimpleAnimator.getNetwork().update(new InteractInvitePacket(this.getUUID(), target.getUUID(), interaction));
      }

      SimpleAnimator.EVENT_BUS.post(new InteractInviteEvent.Post(simpleAnimatorExpand$getPlayerThis(), target, interaction));
      return true;
    }
  }

  @Unique
  public boolean simpleanimator$acceptInteract(@NotNull Player requester, boolean update, boolean forced) {
    if (SimpleAnimator.EVENT_BUS.post(new InteractAcceptEvent.Pre(requester, simpleAnimatorExpand$getPlayerThis(), forced)).isCanceled() && !forced) {
      return false;
    } else if (!SimpleAnimator.getProxy().getInteractionManager().accept(requester, simpleAnimatorExpand$getPlayerThis(), forced)) {
      return false;
    } else {
      if (update && this.isLocalPlayer()) {
        SimpleAnimator.getNetwork().update(new InteractAcceptPacket(requester.getUUID(), this.getUUID(), forced));
      }

      SimpleAnimator.EVENT_BUS.post(new InteractAcceptEvent.Post(requester, simpleAnimatorExpand$getPlayerThis(), forced, this.simpleanimator$request.getInteraction()));
      return true;
    }
  }

  @Unique
  public void simpleanimator$cancelInteract(boolean update) {
    if (this.simpleanimator$request.hasRequest()) {
      SimpleAnimator.EVENT_BUS.post(new CancelInteractEvent(simpleAnimatorExpand$getPlayerThis(), this.simpleanimator$request.getTarget(), this.simpleanimator$request.getInteraction()));
      this.simpleanimator$animator.stop();
      this.simpleanimator$request.reset();
      if (update && this.isLocalPlayer()) {
        SimpleAnimator.getNetwork().update(new InteractCancelPacket(this.getUUID()));
      }

    }
  }

  @Unique
  private @NotNull Player simpleAnimatorExpand$getPlayerThis() {
    return (Player) (Object) this;
  }

  @Unique
  public boolean simpleanimator$hasRequest() {
    return this.simpleanimator$request.hasRequest();
  }

  @Unique
  public @NotNull RequestHolder simpleanimator$getRequest() {
    return this.simpleanimator$request;
  }

  @Unique
  public void simpleanimator$navigate(Player requester) {
    if (this.isLocalPlayer()) {
      this.simpleanimator$innerNavigate(requester);
    }

  }

  @OnlyIn(Dist.CLIENT)
  private void simpleanimator$innerNavigate(Player requester) {
    SimpleAnimator.getClient().getNavigator().navigateTo(requester, 1.0F, 0.0F, () -> this.simpleanimator$acceptInteract(requester, true, true));
  }
}
