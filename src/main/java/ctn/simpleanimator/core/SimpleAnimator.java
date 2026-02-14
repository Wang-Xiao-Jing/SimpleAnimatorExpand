//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import ctn.simpleanimator.api.event.ISAEventBus;
import ctn.simpleanimator.core.event.SAEventBusImpl;
import ctn.simpleanimator.core.network.INetwork;
import ctn.simpleanimator.core.proxy.ClientProxy;
import ctn.simpleanimator.core.proxy.CommonProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;

public abstract class SimpleAnimator {
  public static final ISAEventBus EVENT_BUS = new SAEventBusImpl();
  public static final String MOD_ID = "simple_animator";
  public static final Logger LOGGER = LogUtils.getLogger();
  public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
  private static CommonProxy proxy;
  private static INetwork network;
  private static boolean client;

  public static void init(boolean isClient, Runnable common, Runnable client, INetwork inetwork) {
    network = inetwork;
    proxy = isClient ? new ClientProxy(common, client) : new CommonProxy(common);
    proxy.setup();
    SimpleAnimator.client = isClient;
  }

  public static CommonProxy getProxy() {
    return proxy;
  }

  @OnlyIn(Dist.CLIENT)
  public static ClientProxy getClient() {
    return (ClientProxy) proxy;
  }

  public static INetwork getNetwork() {
    return network;
  }

  public static boolean isClient() {
    return client;
  }
}
