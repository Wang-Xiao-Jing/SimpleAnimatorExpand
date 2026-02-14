//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client.state;

import ctn.simpleanimator.api.animation.AnimationState;
import ctn.simpleanimator.core.client.ClientAnimator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public interface IAnimationState {
  default void enter(ClientAnimator animator) {
  }

  default boolean shouldEnd(ClientAnimator animator) {
    return true;
  }

  default void exit(ClientAnimator animator) {
  }

  AnimationState getNext(ClientAnimator var1);

  default <T> T getDest(T keyframe, T curr) {
    return keyframe;
  }

  default <T> T getSrc(T cache, T curr) {
    return cache;
  }

  class Impl {
    static final IAnimationState[] IMPL = new IAnimationState[]{new StateIdle(), new StateEnter(), new StateLoop(), new StateExit()};

    public static @NotNull IAnimationState get(@NotNull AnimationState state) {
      return IMPL[state.ordinal()];
    }
  }
}
