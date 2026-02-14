//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network.packet.batch;

import com.google.common.collect.ImmutableMap;
import ctn.simpleanimator.api.animation.Interaction;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.ISync;
import ctn.simpleanimator.core.network.NetworkPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ClientUpdateInteractionPacket implements ISync {
  public static final Type<ClientUpdateInteractionPacket> TYPE = NetworkPackets.createType(ClientUpdateInteractionPacket.class);
  private final Map<ResourceLocation, Interaction> interactions;

  public ClientUpdateInteractionPacket(Map<ResourceLocation, Interaction> interactions) {
    this.interactions = ImmutableMap.copyOf(interactions);
  }

  public ClientUpdateInteractionPacket(FriendlyByteBuf byteBuf) {
    this.interactions = byteBuf.readMap(FriendlyByteBuf::readResourceLocation, Interaction::fromNetwork);
  }

  public void write(FriendlyByteBuf byteBuf) {
    byteBuf.writeMap(this.interactions, FriendlyByteBuf::writeResourceLocation, Interaction::toNetwork);
  }

  public void sync() {
    SimpleAnimator.getClient().getAnimationManager().handleUpdateInteractions(this);
  }

  public Map<ResourceLocation, Interaction> getInteractions() {
    return this.interactions;
  }

  public @NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
