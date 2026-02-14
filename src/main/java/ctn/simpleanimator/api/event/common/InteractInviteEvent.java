//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event.common;

import ctn.simpleanimator.api.event.ICancelable;
import ctn.simpleanimator.api.event.SAEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class InteractInviteEvent extends SAEvent {
  private final Player inviter;
  private final Player target;
  private final ResourceLocation interactionID;

  protected InteractInviteEvent(Player inviter, Player target, ResourceLocation interactionID) {
    this.inviter = inviter;
    this.target = target;
    this.interactionID = interactionID;
  }

  public Player getInviter() {
    return this.inviter;
  }

  public Player getTarget() {
    return this.target;
  }

  public ResourceLocation getInteractionID() {
    return this.interactionID;
  }

  public static class Pre extends InteractInviteEvent implements ICancelable {
    public Pre(Player inviter, Player target, ResourceLocation interactionID) {
      super(inviter, target, interactionID);
    }
  }

  public static class Post extends InteractInviteEvent {
    public Post(Player inviter, Player target, ResourceLocation interactionID) {
      super(inviter, target, interactionID);
    }
  }
}
