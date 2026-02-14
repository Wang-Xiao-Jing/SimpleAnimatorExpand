//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.animation;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record Interaction(@NotNull ResourceLocation invite, @NotNull ResourceLocation requester,
                          @NotNull ResourceLocation receiver) {
  public static void toNetwork(FriendlyByteBuf byteBuf, Interaction interaction) {
    byteBuf.writeResourceLocation(interaction.invite);
    byteBuf.writeResourceLocation(interaction.requester);
    byteBuf.writeResourceLocation(interaction.receiver);
  }

  public static Interaction fromNetwork(FriendlyByteBuf byteBuf) {
    ResourceLocation invite = byteBuf.readResourceLocation();
    ResourceLocation requester = byteBuf.readResourceLocation();
    ResourceLocation receiver = byteBuf.readResourceLocation();
    return new Interaction(invite, requester, receiver);
  }
}
