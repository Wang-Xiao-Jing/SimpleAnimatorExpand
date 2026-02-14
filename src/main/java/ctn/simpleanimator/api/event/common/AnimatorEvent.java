//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event.common;

import ctn.simpleanimator.api.animation.Animation;
import ctn.simpleanimator.api.event.SAEvent;
import ctn.simpleanimator.core.animation.Animator;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public abstract class AnimatorEvent extends SAEvent {
  private final UUID owner;
  private final ResourceLocation animationID;
  private final Animation animation;
  private final Animator animator;

  public UUID getOwner() {
    return this.owner;
  }

  public ResourceLocation getAnimationID() {
    return this.animationID;
  }

  public Animation getAnimation() {
    return this.animation;
  }

  public Animator getAnimator() {
    return this.animator;
  }

  protected AnimatorEvent(UUID owner, ResourceLocation animationID, Animation animation, Animator animator) {
    this.owner = owner;
    this.animationID = animationID;
    this.animation = animation;
    this.animator = animator;
  }

  public static class Play extends AnimatorEvent {
    public Play(UUID owner, ResourceLocation animationID, Animation animation, Animator animator) {
      super(owner, animationID, animation, animator);
    }
  }

  public static class Stop extends AnimatorEvent {
    public Stop(UUID owner, ResourceLocation animationID, Animation animation, Animator animator) {
      super(owner, animationID, animation, animator);
    }
  }

  public static class Reset extends AnimatorEvent {
    public Reset(UUID owner, ResourceLocation animationID, Animation animation, Animator animator) {
      super(owner, animationID, animation, animator);
    }
  }
}
