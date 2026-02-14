//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.neoforge.proxy;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeClientProxy {
  private final ClientProxy proxy = SimpleAnimator.getClient();
  private boolean canClear = false;

  public static void setup() {
    NeoForge.EVENT_BUS.register(new NeoForgeClientProxy());
  }

  private NeoForgeClientProxy() {
  }

  @SubscribeEvent
  public void onClientTick(ClientTickEvent.Pre event) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level != null) {
      this.canClear = true;
      if (this.proxy.getNavigator().isNavigating()) {
        this.proxy.getNavigator().tick();
      }
    } else if (this.canClear) {
      this.proxy.getAnimatorManager().clear();
      this.canClear = false;
    }

  }
}
