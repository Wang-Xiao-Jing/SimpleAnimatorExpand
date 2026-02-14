//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event.client;

import ctn.simpleanimator.api.animation.Animation;
import ctn.simpleanimator.api.animation.AnimationState;
import ctn.simpleanimator.api.event.SAEvent;
import ctn.simpleanimator.core.client.ClientAnimator;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public abstract class ClientAnimatorStateEvent extends SAEvent {
  private final UUID owner;
  private final ClientAnimator animator;
  private final ResourceLocation animationID;
  private final Animation animation;
  private final AnimationState curState;
  private final AnimationState nextState;
  private final boolean local;

  protected ClientAnimatorStateEvent(UUID owner, ClientAnimator animator, ResourceLocation animationID, Animation animation, AnimationState curState, AnimationState nextState) {
    this.owner = owner;
    this.animator = animator;
    this.animationID = animationID;
    this.animation = animation;
    this.curState = curState;
    this.nextState = nextState;
    this.local = Minecraft.getInstance().getGameProfile().getId().equals(owner);
  }

  public UUID getOwner() {
    return this.owner;
  }

  public ClientAnimator getAnimator() {
    return this.animator;
  }

  public ResourceLocation getAnimationID() {
    return this.animationID;
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

  public boolean isLocal() {
    return this.local;
  }

  public static class Enter extends ClientAnimatorStateEvent {
    public Enter(UUID owner, ClientAnimator animator, ResourceLocation animationID, Animation animation, AnimationState curState, AnimationState nextState) {
      super(owner, animator, animationID, animation, curState, nextState);
    }
  }

  public static class Exit extends ClientAnimatorStateEvent {
    public Exit(UUID owner, ClientAnimator animator, ResourceLocation animationID, Animation animation, AnimationState curState, AnimationState nextState) {
      super(owner, animator, animationID, animation, curState, nextState);
    }
  }

  public static class Loop extends ClientAnimatorStateEvent {
    public Loop(UUID owner, ClientAnimator animator, ResourceLocation animationID, Animation animation, AnimationState curState, AnimationState nextState) {
      super(owner, animator, animationID, animation, curState, nextState);
    }
  }
}
