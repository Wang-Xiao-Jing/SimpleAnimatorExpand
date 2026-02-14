//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IPacket extends CustomPacketPayload {
  void write(FriendlyByteBuf var1);

  void handle(NetworkContext var1);
}
