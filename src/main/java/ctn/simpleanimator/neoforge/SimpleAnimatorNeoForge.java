//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.neoforge;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.neoforge.network.NeoForgeNetworkImpl;
import ctn.simpleanimator.neoforge.proxy.NeoForgeClientProxy;
import ctn.simpleanimator.neoforge.proxy.NeoForgeCommonProxy;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("simple_animator")
public final class SimpleAnimatorNeoForge {
  public SimpleAnimatorNeoForge() {
    SimpleAnimator.init(FMLEnvironment.dist.isClient(), NeoForgeCommonProxy::setup, NeoForgeClientProxy::setup, new NeoForgeNetworkImpl());
  }
}
