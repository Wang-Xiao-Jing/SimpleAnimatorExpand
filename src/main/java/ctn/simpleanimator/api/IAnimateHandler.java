//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api;

import ctn.simpleanimator.core.animation.Animator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IAnimateHandler {
  boolean simpleanimator$isRunning();

  boolean simpleanimator$playAnimate(@NotNull ResourceLocation var1, boolean var2);

  boolean simpleanimator$stopAnimate(boolean var1);

  @NotNull Animator simpleanimator$getAnimator();
}
