//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client.util;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public interface CubeListBuilderManipulator {
  CubeListBuilder simpleAnimator$addBox(float var1, float var2, float var3, float var4, float var5, float var6, CubeDeformation var7, Set<Direction> var8);
}
