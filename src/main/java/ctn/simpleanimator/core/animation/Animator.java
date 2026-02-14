//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.animation;

import ctn.simpleanimator.api.animation.Animation;
import ctn.simpleanimator.api.animation.AnimationState;
import ctn.simpleanimator.api.event.common.AnimatorEvent;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.packet.AnimatorDataPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class Animator {
  public static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath(SimpleAnimator.MOD_ID, "empty");
  protected final UUID uuid;
  protected ResourceLocation animationLocation;
  protected Animation animation;
  protected AnimationState curState;
  protected AnimationState nextState;
  protected ProcessState procState;
  protected float timer;
  protected float speed = 1.0F;

  public Animator(UUID uuid) {
    this.uuid = uuid;
    this.animationLocation = EMPTY;
    this.curState = AnimationState.IDLE;
    this.nextState = AnimationState.IDLE;
    this.procState = Animator.ProcessState.PROCESS;
    this.timer = 0.0F;
  }

  public void sync(AnimatorDataPacket packet) {
    this.animationLocation = packet.animationLocation;
    this.animation = SimpleAnimator.getProxy().getAnimationManager().getAnimation(packet.animationLocation);
    this.curState = packet.curState;
    this.nextState = packet.nextState;
    this.procState = packet.procState;
    this.timer = packet.timer;
    this.speed = packet.speed;
  }

  public boolean play(ResourceLocation location) {
    if (this.animation != null && !this.animation.isAbortable()) {
      return false;
    } else {
      this.animationLocation = location;
      this.animation = SimpleAnimator.getProxy().getAnimationManager().getAnimation(location);
      this.timer = 0.0F;
      SimpleAnimator.EVENT_BUS.post(new AnimatorEvent.Play(this.uuid, this.animationLocation, this.animation, this));
      return true;
    }
  }

  public boolean stop() {
    if (this.animation == null) {
      return false;
    } else {
      SimpleAnimator.EVENT_BUS.post(new AnimatorEvent.Stop(this.uuid, this.animationLocation, this.animation, this));
      this.timer = 0.0F;
      return true;
    }
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public ResourceLocation getAnimationLocation() {
    return this.animationLocation;
  }

  public Animation getAnimation() {
    return this.animation;
  }

  public AnimationState getCurState() {
    return this.curState;
  }

  public AnimationState getNextState() {
    return this.nextState;
  }

  public ProcessState getProcState() {
    return this.procState;
  }

  public float getTimer() {
    return this.timer;
  }

  public void reset(boolean update) {
    SimpleAnimator.EVENT_BUS.post(new AnimatorEvent.Reset(this.uuid, this.animationLocation, this.animation, this));
    this.timer = 0.0F;
    this.animation = null;
    this.animationLocation = EMPTY;
  }

  public boolean isRunning() {
    return !this.animationLocation.equals(EMPTY) && this.animation != null;
  }

  public boolean isLocal() {
    return false;
  }

  public float getSpeed() {
    return this.speed;
  }

  public enum ProcessState {
    TRANSFER,
    PROCESS
  }
}
