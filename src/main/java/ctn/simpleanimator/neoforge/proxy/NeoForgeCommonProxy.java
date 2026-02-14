//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.neoforge.proxy;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.command.AnimateCommand;
import ctn.simpleanimator.core.command.InteractCommand;
import ctn.simpleanimator.core.network.NetworkPackets;
import ctn.simpleanimator.core.proxy.CommonProxy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.UUID;

public class NeoForgeCommonProxy {
  private final CommonProxy proxy = SimpleAnimator.getProxy();

  public static void setup() {
    NeoForge.EVENT_BUS.register(new NeoForgeCommonProxy());
    IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();

    assert bus != null;

    bus.addListener(NeoForgeCommonProxy::onRegisterPayLoad);
  }

  private static void onRegisterPayLoad(RegisterPayloadHandlersEvent event) {
    NetworkPackets.register();
  }

  private NeoForgeCommonProxy() {
  }

  @SubscribeEvent
  public void onCommandRegister(RegisterCommandsEvent event) {
    AnimateCommand.register(event.getDispatcher());
    InteractCommand.register(event.getDispatcher());
  }

  @SubscribeEvent
  public void onReload(AddReloadListenerEvent event) {
    this.proxy.getAnimatorManager().reset();
    this.proxy.getInteractionManager().reset();
    event.addListener(this.proxy.getAnimationManager());
  }

  @SubscribeEvent
  public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    Player var3 = event.getEntity();
    if (var3 instanceof ServerPlayer serverPlayer) {
      this.proxy.getAnimatorManager().sync(serverPlayer);
    }

  }

  @SubscribeEvent
  public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    UUID uuid = event.getEntity().getUUID();
    this.proxy.getAnimatorManager().remove(uuid);
    this.proxy.getInteractionManager().remove(uuid);
  }

  @SubscribeEvent
  public void onOnDatapackSync(OnDatapackSyncEvent event) {
    if (event.getPlayer() != null) {
      this.proxy.getAnimationManager().sync(event.getPlayer());
    } else {
      this.proxy.getAnimationManager().sync(event.getPlayerList());
    }

  }
}
