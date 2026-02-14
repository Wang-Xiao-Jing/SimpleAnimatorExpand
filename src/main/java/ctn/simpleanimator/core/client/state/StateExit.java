//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client.state;

import ctn.simpleanimator.api.animation.AnimationState;
import ctn.simpleanimator.core.client.ClientAnimator;

public class StateExit implements IAnimationState {
  public AnimationState getNext(ClientAnimator animator) {
    return AnimationState.IDLE;
  }
}
