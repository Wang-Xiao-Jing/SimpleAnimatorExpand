//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client.state;

import ctn.simpleanimator.api.animation.AnimationState;
import ctn.simpleanimator.core.client.ClientAnimator;

public class StateLoop implements IAnimationState {
  public boolean shouldEnd(ClientAnimator animator) {
    return !animator.getAnimation().repeatable() || animator.getNextState() != AnimationState.LOOP;
  }

  public AnimationState getNext(ClientAnimator animator) {
    return animator.getAnimation().hasExitAnimation() ? AnimationState.EXIT : AnimationState.IDLE;
  }
}
