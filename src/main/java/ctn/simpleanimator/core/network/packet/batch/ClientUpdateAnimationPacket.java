//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network.packet.batch;

import com.google.common.collect.ImmutableMap;
import ctn.simpleanimator.api.animation.Animation;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.ISync;
import ctn.simpleanimator.core.network.NetworkPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ClientUpdateAnimationPacket implements ISync {
  public static final Type<ClientUpdateAnimationPacket> TYPE = NetworkPackets.createType(ClientUpdateAnimationPacket.class);
  private final Map<ResourceLocation, Animation> animations;

  public ClientUpdateAnimationPacket(Map<ResourceLocation, Animation> map) {
    this.animations = ImmutableMap.copyOf(map);
  }

  public ClientUpdateAnimationPacket(FriendlyByteBuf byteBuf) {
    this.animations = byteBuf.readMap(FriendlyByteBuf::readResourceLocation, Animation::fromNetwork);
  }

  public void write(FriendlyByteBuf byteBuf) {
    byteBuf.writeMap(this.animations, FriendlyByteBuf::writeResourceLocation, Animation::toNetwork);
    SimpleAnimator.LOGGER.info("Buffer Capacity: {} / {}", byteBuf.writerIndex(), byteBuf.capacity());
  }

  public void sync() {
    SimpleAnimator.getClient().getAnimationManager().handleUpdateAnimations(this);
  }

  public Map<ResourceLocation, Animation> getAnimations() {
    return this.animations;
  }

  public @NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
