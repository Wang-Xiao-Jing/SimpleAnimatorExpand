//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PlayerUtils {
  public static boolean isRiding(Entity player) {
    return player != null && player.getVehicle() != null;
  }

  public static Vector3f normalizeRadians(Vector3f vector3f) {
    return vector3f.set(normalizeRadians(vector3f.x), normalizeRadians(vector3f.y), normalizeRadians(vector3f.z));
  }

  public static double normalizeAngle(float angle) {
    angle %= 360.0F;
    if (angle >= 180.0F) {
      angle -= 360.0F;
    }

    if (angle < -180.0F) {
      angle += 360.0F;
    }

    return angle;
  }

  public static float normalizeRadians(float rad) {
    rad %= ((float) Math.PI * 2F);
    if ((double) rad > Math.PI) {
      rad -= ((float) Math.PI * 2F);
    } else if ((double) rad < -Math.PI) {
      rad += ((float) Math.PI * 2F);
    }

    return rad;
  }

  public static float getLookAtRotY(Player player, Vec3 vec3) {
    Vec3 vec32 = Anchor.EYES.apply(player);
    double d = vec3.x - vec32.x;
    double f = vec3.z - vec32.z;
    return Mth.wrapDegrees((float) (Mth.atan2(f, d) * (double) (180F / (float) Math.PI)) - 90.0F);
  }

  public static Vec3 getRelativePositionWorldSpace(Player player, double forward, double left) {
    Vec2 vec2 = new Vec2(0.0F, player.yBodyRot);
    Vec3 vec3 = player.position();
    float f = Mth.cos((vec2.y + 90.0F) * ((float) Math.PI / 180F));
    float f1 = Mth.sin((vec2.y + 90.0F) * ((float) Math.PI / 180F));
    double d0 = (double) f * forward - (double) f1 * left;
    double d2 = (double) f1 * forward + (double) f * left;
    return new Vec3(vec3.x + d0, vec3.y, vec3.z + d2);
  }

  public static Vec3 getRelativePosition(Player player, double forward, double left) {
    Vec2 vec2 = new Vec2(0.0F, player.yBodyRot);
    float f = Mth.cos((vec2.y + 90.0F) * ((float) Math.PI / 180F));
    float f1 = Mth.sin((vec2.y + 90.0F) * ((float) Math.PI / 180F));
    return new Vec3((double) f * forward - (double) f1 * left, 0.0F, (double) f1 * forward + (double) f * left);
  }

  public static boolean canPositionStand(Vec3 vec3, Level level, float down) {
    AABB box = new AABB(vec3.x - (double) 0.3F, vec3.y - (double) down, vec3.z - (double) 0.3F, vec3.x + (double) 0.3F, vec3.y, vec3.z + (double) 0.3F);
    return level.getBlockCollisions(null, box).iterator().hasNext();
  }

  public static boolean canPositionPass(Vec3 vec3, Level level) {
    AABB box = new AABB(vec3.x - (double) 0.3F, vec3.y + (double) 0.1F, vec3.z - (double) 0.3F, vec3.x + (double) 0.3F, vec3.y + (double) 1.7F, vec3.z + (double) 0.3F);
    return !level.getBlockCollisions(null, box).iterator().hasNext();
  }

  public static boolean isPositionSave(Vec3 vec3, Level level) {
    return canPositionStand(vec3, level, 0.1F) && canPositionPass(vec3, level);
  }

  public static double distanceSqr2D(Vec3 src, Vec3 dest) {
    double x = dest.x - src.x;
    double z = dest.z - src.z;
    return x * x + z * z;
  }

  public static boolean inSameDimension(Player a, Player b) {
    return a.level() == b.level();
  }
}
