//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network;

import net.minecraft.server.level.ServerPlayer;

public interface INetwork {
  String PROTOCOL_VERSION = "1";

  void sendToPlayer(IPacket var1, ServerPlayer var2);

  void sendToAllPlayers(IPacket var1, ServerPlayer var2);

  void sendToPlayersExcept(IPacket var1, ServerPlayer... var2);

  void sendToPlayers(IPacket var1, ServerPlayer var2);

  void update(IPacket var1);

  <T extends IPacket> void register(NetworkPackets.PacketType<T> var1);
}
