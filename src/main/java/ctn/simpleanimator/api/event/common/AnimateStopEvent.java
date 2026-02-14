//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event.common;

import ctn.simpleanimator.api.event.ICancelable;
import ctn.simpleanimator.api.event.SAEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class AnimateStopEvent extends SAEvent {
  private final Player player;
  private final ResourceLocation animationID;

  protected AnimateStopEvent(Player player, ResourceLocation animationID) {
    this.player = player;
    this.animationID = animationID;
  }

  public ResourceLocation getAnimationID() {
    return this.animationID;
  }

  public Player getPlayer() {
    return this.player;
  }

  public static class Pre extends AnimatePlayEvent implements ICancelable {
    public Pre(Player player, ResourceLocation animationID) {
      super(player, animationID);
    }
  }

  public static class Post extends AnimatePlayEvent {
    public Post(Player player, ResourceLocation animationID) {
      super(player, animationID);
    }
  }
}
