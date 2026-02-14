//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.proxy;

import ctn.simpleanimator.core.client.ClientAnimatorManager;
import ctn.simpleanimator.core.client.ClientPlayerNavigator;
import ctn.simpleanimator.core.config.ClientConfiguration;

public class ClientProxy extends CommonProxy {
  private final ClientPlayerNavigator navigator = new ClientPlayerNavigator();
  private final Runnable setup;
  private final ClientConfiguration config;

  public ClientProxy(Runnable common, Runnable client) {
    super(new ClientAnimatorManager(), common);
    this.setup = client;
    this.config = ClientConfiguration.load();
  }

  public ClientPlayerNavigator getNavigator() {
    return this.navigator;
  }

  public ClientAnimatorManager getClientAnimatorManager() {
    return (ClientAnimatorManager) this.getAnimatorManager();
  }

  public void setup() {
    super.setup();
    this.setup.run();
  }

  public ClientConfiguration getClientConfiguration() {
    return this.config;
  }
}
