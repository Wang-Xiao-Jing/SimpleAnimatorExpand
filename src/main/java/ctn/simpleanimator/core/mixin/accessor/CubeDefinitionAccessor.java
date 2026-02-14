//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.mixin.accessor;

import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mixin({CubeDefinition.class})
public interface CubeDefinitionAccessor {
  @Invoker("<init>")
  static CubeDefinition getInstance(@Nullable String string, float f, float g, float h, float i, float j, float k, float l, float m, CubeDeformation cubeDeformation, boolean bl, float n, float o, Set<Direction> set) {
    throw new AssertionError();
  }
}
