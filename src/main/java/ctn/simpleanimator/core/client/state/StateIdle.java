//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client.state;

import ctn.simpleanimator.api.animation.AnimationState;
import ctn.simpleanimator.core.client.ClientAnimator;

public class StateIdle implements IAnimationState {
  public void enter(ClientAnimator animator) {
    animator.reset(true);
  }

  public AnimationState getNext(ClientAnimator animator) {
    return animator.getAnimation().hasEnterAnimation() ? AnimationState.ENTER : AnimationState.LOOP;
  }

  public <T> T getDest(T keyframe, T curr) {
    return curr;
  }

  public <T> T getSrc(T cache, T curr) {
    return curr;
  }
}
