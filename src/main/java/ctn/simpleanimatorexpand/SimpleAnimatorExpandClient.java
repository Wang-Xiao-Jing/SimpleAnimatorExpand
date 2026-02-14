package ctn.simpleanimatorexpand;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

@Mod(value = SimpleAnimatorExpand.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SimpleAnimatorExpand.MODID, value = Dist.CLIENT)
public class SimpleAnimatorExpandClient {
  public SimpleAnimatorExpandClient(ModContainer container) {
  }
}
