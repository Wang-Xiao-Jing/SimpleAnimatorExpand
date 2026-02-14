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

public class InteractCancelPacket extends UserPacket {
  public static final CustomPacketPayload.Type<InteractCancelPacket> TYPE = NetworkPackets.createType(InteractCancelPacket.class);

  public InteractCancelPacket(FriendlyByteBuf byteBuf) {
    super(byteBuf);
  }

  public InteractCancelPacket(UUID uuid) {
    super(uuid);
  }

  protected void update(@NotNull ServerPlayer sender) {
    SimpleAnimator.getProxy().getInteractionManager().cancel(this.owner);
    SimpleAnimator.getNetwork().sendToPlayers(this, sender);
  }

  @OnlyIn(Dist.CLIENT)
  protected void sync() {
    SimpleAnimator.getProxy().getInteractionManager().cancel(this.owner);
  }

  public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
