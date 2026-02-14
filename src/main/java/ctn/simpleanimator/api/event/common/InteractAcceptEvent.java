//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event.common;

import ctn.simpleanimator.api.event.ICancelable;
import ctn.simpleanimator.api.event.SAEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class InteractAcceptEvent extends SAEvent {
  private final Player inviter;
  private final Player acceptor;
  private final boolean forced;

  protected InteractAcceptEvent(Player inviter, Player acceptor, boolean forced) {
    this.inviter = inviter;
    this.acceptor = acceptor;
    this.forced = forced;
  }

  public Player getInviter() {
    return this.inviter;
  }

  public Player getAcceptor() {
    return this.acceptor;
  }

  public boolean isForced() {
    return this.forced;
  }

  public static class Pre extends InteractAcceptEvent implements ICancelable {
    public Pre(Player inviter, Player acceptor, boolean forced) {
      super(inviter, acceptor, forced);
    }
  }

  public static class Post extends InteractAcceptEvent {
    private final ResourceLocation interactionID;

    public Post(Player inviter, Player acceptor, boolean forced, ResourceLocation interactionID) {
      super(inviter, acceptor, forced);
      this.interactionID = interactionID;
    }

    public ResourceLocation getInteractionID() {
      return this.interactionID;
    }
  }
}
