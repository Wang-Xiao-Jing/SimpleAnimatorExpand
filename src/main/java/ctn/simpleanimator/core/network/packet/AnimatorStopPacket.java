//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network.packet;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.NetworkPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AnimatorStopPacket extends UserPacket {
  public static final CustomPacketPayload.Type<AnimatorStopPacket> TYPE = NetworkPackets.createType(AnimatorStopPacket.class);

  public AnimatorStopPacket(FriendlyByteBuf byteBuf) {
    super(byteBuf);
  }

  public AnimatorStopPacket(UUID uuid) {
    super(uuid);
  }

  public void update(@NotNull ServerPlayer sender) {
    SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(this.owner).stop();
    SimpleAnimator.getNetwork().sendToPlayers(this, sender);
  }

  @OnlyIn(Dist.CLIENT)
  public void sync() {
    SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(this.owner).stop();
  }

  public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
