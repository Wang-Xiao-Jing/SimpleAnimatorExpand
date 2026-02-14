//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network.packet;

import ctn.simpleanimator.core.network.BiPacket;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class UserPacket extends BiPacket {
  protected final UUID owner;

  public UserPacket(FriendlyByteBuf byteBuf) {
    this.owner = byteBuf.readUUID();
  }

  public UserPacket(UUID uuid) {
    this.owner = uuid;
  }

  public void write(FriendlyByteBuf buffer) {
    buffer.writeUUID(this.owner);
  }

  public UUID getOwner() {
    return this.owner;
  }
}
