//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network.packet;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.NetworkPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InteractAcceptPacket extends UserPacket {
  public static final CustomPacketPayload.Type<InteractAcceptPacket> TYPE = NetworkPackets.createType(InteractAcceptPacket.class);
  private final UUID target;
  private final boolean forced;

  public InteractAcceptPacket(FriendlyByteBuf byteBuf) {
    super(byteBuf);
    this.target = byteBuf.readUUID();
    this.forced = byteBuf.readBoolean();
  }

  public InteractAcceptPacket(UUID requester, UUID receiver, boolean forced) {
    super(requester);
    this.target = receiver;
    this.forced = forced;
  }

  public void write(FriendlyByteBuf buffer) {
    super.write(buffer);
    buffer.writeUUID(this.target);
    buffer.writeBoolean(this.forced);
  }

  public void update(@NotNull ServerPlayer sender) {
    Player requester = sender.level().getPlayerByUUID(this.owner);
    if (requester != null) {
      if (this.forced || SimpleAnimator.getProxy().getInteractionManager().accept(requester, sender, false)) {
        SimpleAnimator.getNetwork().sendToPlayers(this, sender);
      }

    }
  }

  protected void sync() {
    ClientLevel level = Minecraft.getInstance().level;
    Player requester = level.getPlayerByUUID(this.owner);
    Player target = level.getPlayerByUUID(this.target);
    if (requester != null && target != null) {
      SimpleAnimator.getProxy().getInteractionManager().accept(requester, target, this.forced);
    }
  }

  public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
