//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.packet.*;
import ctn.simpleanimator.core.network.packet.batch.ClientUpdateAnimationPacket;
import ctn.simpleanimator.core.network.packet.batch.ClientUpdateAnimatorPacket;
import ctn.simpleanimator.core.network.packet.batch.ClientUpdateInteractionPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum NetworkPackets {
  ANIMATOR_UPDATE(AnimatorDataPacket.class, AnimatorDataPacket::new, NetworkDirection.ALL),
  ANIMATOR_PLAY(AnimatorPlayPacket.class, AnimatorPlayPacket::new, NetworkDirection.ALL),
  ANIMATOR_STOP(AnimatorStopPacket.class, AnimatorStopPacket::new, NetworkDirection.ALL),
  INTERACT_INVITE(InteractInvitePacket.class, InteractInvitePacket::new, NetworkDirection.ALL),
  INTERACT_ACCEPT(InteractAcceptPacket.class, InteractAcceptPacket::new, NetworkDirection.ALL),
  INTERACT_CANCEL(InteractCancelPacket.class, InteractCancelPacket::new, NetworkDirection.ALL),
  CLIENT_UPDATE_ANIMATION(ClientUpdateAnimationPacket.class, ClientUpdateAnimationPacket::new, NetworkDirection.PLAY_TO_CLIENT),
  CLIENT_UPDATE_INTERACTION(ClientUpdateInteractionPacket.class, ClientUpdateInteractionPacket::new, NetworkDirection.PLAY_TO_CLIENT),
  CLIENT_UPDATE_ANIMATOR(ClientUpdateAnimatorPacket.class, ClientUpdateAnimatorPacket::new, NetworkDirection.PLAY_TO_CLIENT);

  private final PacketType<?> packet;

  <T extends IPacket> NetworkPackets(Class<T> type, Function<FriendlyByteBuf, T> decoder, NetworkDirection direction) {
    this.packet = new PacketType<>(type, decoder, IPacket::handle, direction);
  }

  public static <T extends IPacket> CustomPacketPayload.Type<T> createType(Class<T> type) {
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath("simple_animator", type.getSimpleName().toLowerCase(Locale.ROOT));
    return new CustomPacketPayload.Type<>(location);
  }

  public static void register() {
    INetwork network = SimpleAnimator.getNetwork();

    for (NetworkPackets value : values()) {
      network.register(value.packet);
    }

  }

  public PacketType<?> getPacket() {
    return this.packet;
  }

  public record PacketType<T extends IPacket>(CustomPacketPayload.Type<T> type,
                                              StreamCodec<FriendlyByteBuf, T> codec,
                                              BiConsumer<T, NetworkContext> handler,
                                              NetworkDirection direction) {

    public PacketType(@NotNull Class<T> type, @NotNull Function<FriendlyByteBuf, T> codec, @NotNull BiConsumer<T, NetworkContext> handler, @NotNull NetworkDirection direction) {
      this(NetworkPackets.createType(type), StreamCodec.of((byteBuf, packet) -> packet.write(byteBuf), codec::apply), handler, direction);
    }
  }
}
