//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.proxy;

import ctn.simpleanimator.core.animation.AnimationManager;
import ctn.simpleanimator.core.animation.Animator;
import ctn.simpleanimator.core.animation.AnimatorManager;
import ctn.simpleanimator.core.animation.InteractionManager;
import ctn.simpleanimator.core.config.CommonConfiguration;

public class CommonProxy {
  protected final AnimatorManager<? extends Animator> animatorManager;
  protected final AnimationManager animationManager;
  protected final InteractionManager interactionManager;
  private final Runnable setup;
  private final CommonConfiguration config;

  public CommonProxy(Runnable setup) {
    this(new AnimatorManager<>(), setup);
  }

  protected CommonProxy(AnimatorManager<? extends Animator> animatorManager, Runnable setup) {
    this.animationManager = new AnimationManager();
    this.animatorManager = animatorManager;
    this.interactionManager = new InteractionManager();
    this.setup = setup;
    this.config = CommonConfiguration.load();
  }

  public AnimatorManager<? extends Animator> getAnimatorManager() {
    return this.animatorManager;
  }

  public AnimationManager getAnimationManager() {
    return this.animationManager;
  }

  public InteractionManager getInteractionManager() {
    return this.interactionManager;
  }

  public CommonConfiguration getConfig() {
    return this.config;
  }

  public void setup() {
    this.setup.run();
  }
}
