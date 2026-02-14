//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public abstract class BiPacket implements IPacket {
  public void handle(NetworkContext context) {
    switch (context.direction()) {
      case PLAY_TO_CLIENT:
        this.sync();
        break;
      case PLAY_TO_SERVER:
        if (context.sender() != null) {
          this.update(context.sender());
        }
    }

  }

  protected abstract void update(@NotNull ServerPlayer var1);

  @OnlyIn(Dist.CLIENT)
  protected abstract void sync();
}
