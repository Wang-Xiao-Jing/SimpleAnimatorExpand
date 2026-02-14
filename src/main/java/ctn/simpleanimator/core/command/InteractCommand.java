//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import ctn.simpleanimator.api.IInteractHandler;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.packet.InteractAcceptPacket;
import ctn.simpleanimator.core.network.packet.InteractInvitePacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.stream.Stream;

public class InteractCommand {
  private static final SuggestionProvider<CommandSourceStack> SUGGEST_INTERACTION = (context, builder) -> SharedSuggestionProvider.suggestResource(SimpleAnimator.getProxy().getAnimationManager().getInteractionNames(), builder);
  private static final SuggestionProvider<CommandSourceStack> SUGGEST_PLAYER = (context, builder) -> SharedSuggestionProvider.suggest(getPlayerNames(context.getSource()), builder);

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("interact").requires((stack) -> stack.hasPermission(2)).then(Commands.literal("accept").then(Commands.argument("requester", EntityArgument.player()).suggests(SUGGEST_PLAYER).executes(InteractCommand::accept))).then(Commands.literal("invite").then(Commands.argument("target", EntityArgument.player()).suggests(SUGGEST_PLAYER).then(Commands.argument("interaction", ResourceLocationArgument.id()).suggests(SUGGEST_INTERACTION).executes(InteractCommand::invite)))));
  }

  private static int invite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    if (!source.isPlayer()) {
      source.sendFailure(Component.translatable("animator.commands.failed.invalid_source"));
      return 0;
    } else {
      ServerPlayer player = source.getPlayer();
      ServerPlayer target = EntityArgument.getPlayer(context, "target");
      if (player == target) {
        source.sendFailure(Component.translatable("animator.commands.failed.same_player"));
        return 0;
      } else {
        ResourceLocation location = ResourceLocationArgument.getId(context, "interaction");
        if (!((IInteractHandler) player).simpleanimator$inviteInteract(target, location, false)) {
          return 0;
        } else {
          SimpleAnimator.getNetwork().sendToAllPlayers(new InteractInvitePacket(player.getUUID(), target.getUUID(), location), player);
          return 1;
        }
      }
    }
  }

  private static int accept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    if (!source.isPlayer()) {
      source.sendFailure(Component.translatable("animator.commands.failed.invalid_source"));
      return 0;
    } else {
      ServerPlayer player = source.getPlayer();
      ServerPlayer requester = EntityArgument.getPlayer(context, "requester");
      InteractAcceptPacket packet = new InteractAcceptPacket(requester.getUUID(), player.getUUID(), false);
      if (!((IInteractHandler) player).simpleanimator$acceptInteract(requester, false, false)) {
        SimpleAnimator.getNetwork().sendToPlayer(packet, player);
        return 0;
      } else {
        SimpleAnimator.getNetwork().sendToAllPlayers(packet, player);
        return 1;
      }
    }
  }

  private static Stream<String> getPlayerNames(CommandSourceStack sources) {
    ServerPlayer serverPlayer = sources.getPlayer();
    return sources.getServer().getPlayerList().getPlayers().stream().filter((player) -> serverPlayer != player).map(Player::getGameProfile).map(GameProfile::getName);
  }
}
