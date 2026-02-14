package ctn.simpleanimatorexpand;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SimpleAnimatorExpand.MODID)
public class SimpleAnimatorExpand {

  public static final String MODID = "simpleanimatorexpand";
  public static final Logger LOGGER = LogUtils.getLogger();

  public SimpleAnimatorExpand(IEventBus modEventBus, ModContainer modContainer) {
  }
}
