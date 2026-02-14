//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event.common;

import ctn.simpleanimator.api.event.SAEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CancelInteractEvent extends SAEvent {
  private final Player player;
  private final UUID target;
  private final ResourceLocation interactionID;

  public CancelInteractEvent(Player player, UUID other, ResourceLocation interactionID) {
    this.player = player;
    this.target = other;
    this.interactionID = interactionID;
  }

  public Player getPlayer() {
    return this.player;
  }

  public UUID getTarget() {
    return this.target;
  }

  public ResourceLocation getInteractionID() {
    return this.interactionID;
  }
}
