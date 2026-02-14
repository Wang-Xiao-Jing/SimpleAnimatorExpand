//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api;

import ctn.simpleanimator.api.animation.RequestHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface IInteractHandler {
  boolean simpleanimator$inviteInteract(@NotNull Player var1, @NotNull ResourceLocation var2, boolean var3);

  boolean simpleanimator$acceptInteract(@NotNull Player var1, boolean var2, boolean var3);

  void simpleanimator$cancelInteract(boolean var1);

  boolean simpleanimator$hasRequest();

  @NotNull RequestHolder simpleanimator$getRequest();
}
