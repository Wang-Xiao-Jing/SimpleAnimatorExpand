//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.accessor;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@OnlyIn(Dist.CLIENT)
@Mixin({KeyMapping.class})
public interface KeyMappingAccessor {
  @Invoker("release")
  void simpleanimator$release();
}
