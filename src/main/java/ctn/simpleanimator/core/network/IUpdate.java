//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network;

import net.minecraft.server.level.ServerPlayer;

public interface IUpdate extends IPacket {
  default void handle(NetworkContext context) {
    if (context.direction() == NetworkDirection.PLAY_TO_SERVER) {
      this.update(context.sender());
    }

  }

  void update(ServerPlayer var1);
}
