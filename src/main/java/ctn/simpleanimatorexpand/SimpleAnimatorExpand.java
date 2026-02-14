package ctn.simpleanimatorexpand;

import com.mojang.logging.LogUtils;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.neoforge.network.NeoForgeNetworkImpl;
import ctn.simpleanimator.neoforge.proxy.NeoForgeClientProxy;
import ctn.simpleanimator.neoforge.proxy.NeoForgeCommonProxy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(SimpleAnimatorExpand.MODID)
public class SimpleAnimatorExpand {

  public static final String MODID = "simpleanimatorexpand";
  public static final Logger LOGGER = LogUtils.getLogger();

  public SimpleAnimatorExpand(IEventBus modEventBus, ModContainer modContainer) {
    SimpleAnimator.init(FMLEnvironment.dist.isClient(), NeoForgeCommonProxy::setup, NeoForgeClientProxy::setup, new NeoForgeNetworkImpl());
  }
}
