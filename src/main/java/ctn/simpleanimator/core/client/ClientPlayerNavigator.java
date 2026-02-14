//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client;

import ctn.simpleanimator.api.IAnimateHandler;
import ctn.simpleanimator.api.event.SAEvent;
import ctn.simpleanimator.api.event.client.ClientNavigatorEvent;
import ctn.simpleanimator.core.PlayerUtils;
import ctn.simpleanimator.core.SimpleAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerNavigator {
  private Player target = null;
  private float forward = 0.0F;
  private float left = 0.0F;
  private Vec3 targetPosition = null;
  private Vec3 lastTargetPosition;
  private Phrase phrase;
  private long timer;
  private boolean navigating;
  private Runnable post;

  public ClientPlayerNavigator() {
    this.lastTargetPosition = Vec3.ZERO;
    this.phrase = ClientPlayerNavigator.Phrase.IDLE;
    this.timer = 0L;
    this.navigating = false;
  }

  public void tick() {
    switch (this.phrase.ordinal()) {
      case 1:
        LocalPlayer player = Minecraft.getInstance().player;
        if (!this.lastTargetPosition.equals(this.target.position())) {
          this.lastTargetPosition = this.target.position();
          this.targetPosition = PlayerUtils.getRelativePositionWorldSpace(this.target, this.forward, this.left);
        }

        if (this.timer++ > 1000L) {
          this.stop(false);
          return;
        }

        if (((IAnimateHandler) player).simpleanimator$getAnimator().isRunning()) {
          return;
        }

        if (player.distanceToSqr(this.targetPosition) < 0.001) {
          player.moveTo(this.targetPosition);
          this.phrase = ClientPlayerNavigator.Phrase.FINISH;
          this.timer = 0L;
          return;
        }

        Vec3 subtract = this.targetPosition.subtract(player.position()).multiply(1.0F, 0.0F, 1.0F);
        Vec3 direction = subtract.normalize().scale(player.getSpeed());
        Vec3 vec3 = subtract.lengthSqr() < direction.lengthSqr() ? subtract : direction;
        if (!PlayerUtils.canPositionStand(player.position().add(vec3), player.level(), 0.5F)) {
          player.setDeltaMovement(0.0F, 0.0F, 0.0F);
          this.stop(false);
          return;
        }

        player.addDeltaMovement(vec3);
        player.lookAt(Anchor.EYES, this.lastTargetPosition.add(0.0F, this.target.getEyeHeight(), 0.0F));
        break;
      case 2:
        if (this.timer++ > 20L) {
          this.timer = 0L;
          SimpleAnimator.LOGGER.info("Finish");
          if (this.post != null) {
            this.post.run();
          }

          this.stop(true);
        }
    }

  }

  public <T extends SAEvent> void navigateTo(Player player, float forward, float left, Runnable post) {
    LocalPlayer local = Minecraft.getInstance().player;
    if (!(SimpleAnimator.EVENT_BUS.post(new ClientNavigatorEvent.Start(player, forward, left)).isCanceled())) {

      if (!PlayerUtils.isRiding(local) || player.onGround() || PlayerUtils.inSameDimension(local, player)) {
        this.phrase = ClientPlayerNavigator.Phrase.RUNNING;
        this.target = player;
        this.forward = forward;
        this.left = left;
        this.post = post;
        this.navigating = true;
        this.timer = 0L;
      }
    }
  }

  public boolean isNavigating() {
    return this.navigating;
  }

  public void stop(boolean finished) {
    SimpleAnimator.EVENT_BUS.post(new ClientNavigatorEvent.End(finished));
    this.navigating = false;
    this.target = null;
    this.targetPosition = null;
    this.lastTargetPosition = Vec3.ZERO;
    this.post = null;
    this.timer = 0L;
    this.phrase = ClientPlayerNavigator.Phrase.IDLE;
  }

  private enum Phrase {
    IDLE,
    RUNNING,
    FINISH
  }
}
