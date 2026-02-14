//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network.packet.batch;

import ctn.simpleanimator.core.network.IPacket;
import ctn.simpleanimator.core.network.NetworkContext;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class PacketCache implements IPacket {
  private final FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
  private CustomPacketPayload.Type<? extends CustomPacketPayload> type;

  public void reset(@NotNull IPacket packet) {
    this.type = packet.type();
    this.byteBuf.clear();
    packet.write(this.byteBuf);
  }

  public boolean ready() {
    return this.type != null && this.byteBuf.isReadable();
  }

  public void write(FriendlyByteBuf byteBuf) {
    byteBuf.writeBytes(this.byteBuf.duplicate());
  }

  public void handle(NetworkContext context) {
  }

  public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
    return this.type;
  }
}
