//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network.packet;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.NetworkPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AnimatorPlayPacket extends UserPacket {
  public static final CustomPacketPayload.Type<AnimatorPlayPacket> TYPE = NetworkPackets.createType(AnimatorPlayPacket.class);
  public final ResourceLocation animation;

  public AnimatorPlayPacket(FriendlyByteBuf byteBuf) {
    super(byteBuf);
    this.animation = byteBuf.readResourceLocation();
  }

  public AnimatorPlayPacket(UUID uuid, ResourceLocation animation) {
    super(uuid);
    this.animation = animation;
  }

  public void write(FriendlyByteBuf buffer) {
    super.write(buffer);
    buffer.writeResourceLocation(this.animation);
  }

  public void update(@NotNull ServerPlayer sender) {
    SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(this.owner).play(this.animation);
    SimpleAnimator.getNetwork().sendToPlayers(this, sender);
  }

  @OnlyIn(Dist.CLIENT)
  public void sync() {
    SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(this.owner).play(this.animation);
  }

  public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
