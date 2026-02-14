//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.neoforge.network;

import com.google.common.collect.Sets;
import ctn.simpleanimator.core.network.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class NeoForgeNetworkImpl implements INetwork {
  private final PayloadRegistrar registrar = new PayloadRegistrar("1");

  public void sendToPlayer(IPacket packet, ServerPlayer player) {
    PacketDistributor.sendToPlayer(player, packet);
  }

  public void sendToAllPlayers(IPacket packet, ServerPlayer player) {
    PacketDistributor.sendToAllPlayers(packet);
  }

  public void sendToPlayersExcept(IPacket packet, ServerPlayer... except) {
    MinecraftServer server = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer(), "Cannot send clientbound payloads on the client");
    HashSet<ServerPlayer> players = Sets.newHashSet(except);
    Stream<ServerPlayer> var10000 = server.getPlayerList().getPlayers().stream();
    Objects.requireNonNull(players);
    var10000.filter(Predicate.not(players::contains)).forEach((player) -> PacketDistributor.sendToPlayer(player, packet));
  }

  public void sendToPlayers(IPacket packet, ServerPlayer target) {
    MinecraftServer server = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer(), "Cannot send clientbound payloads on the client");
    server.getPlayerList().getPlayers().stream().filter((player) -> target != player).forEach((player) -> PacketDistributor.sendToPlayer(player, packet));
  }

  public void update(IPacket packet) {
    PacketDistributor.sendToServer(packet);
  }

  public <T extends IPacket> void register(NetworkPackets.PacketType<T> packet) {
    switch (packet.direction()) {
      case ALL ->
        this.registrar.playBidirectional(packet.type(), packet.codec(), NeoForgeNetworkImpl::handleBi);
      case PLAY_TO_CLIENT ->
        this.registrar.playToClient(packet.type(), packet.codec(), NeoForgeNetworkImpl::handleClient);
      case PLAY_TO_SERVER ->
        this.registrar.playToServer(packet.type(), packet.codec(), NeoForgeNetworkImpl::handleServer);
    }

  }

  private static void handleBi(IPacket payload, IPayloadContext context) {
    context.enqueueWork(() -> {
      if (context.flow().isClientbound()) {
        payload.handle(new NetworkContext(NetworkDirection.PLAY_TO_CLIENT, null));
      } else {
        payload.handle(new NetworkContext(NetworkDirection.PLAY_TO_SERVER, (ServerPlayer) context.player()));
      }

    });
  }

  private static void handleClient(IPacket payload, IPayloadContext context) {
    context.enqueueWork(() -> payload.handle(new NetworkContext(NetworkDirection.PLAY_TO_CLIENT, null)));
  }

  private static void handleServer(IPacket payload, IPayloadContext context) {
    context.enqueueWork(() -> payload.handle(new NetworkContext(NetworkDirection.PLAY_TO_SERVER, (ServerPlayer) context.player())));
  }
}
