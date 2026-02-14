//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.animation;

import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.packet.AnimatorDataPacket;
import ctn.simpleanimator.core.network.packet.batch.ClientUpdateAnimatorPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AnimatorManager<T extends Animator> {
  protected final Map<UUID, T> animators = new Object2ObjectOpenHashMap<>();

  public @Nullable T getAnimator(UUID uuid) {
    return this.animators.get(uuid);
  }

  public T createIfAbsent(UUID uuid) {
    return this.animators.computeIfAbsent(uuid, (uid) -> (T) new Animator(uid));
  }

  public void clear() {
    this.animators.clear();
  }

  public void reset() {
    for (T value : this.animators.values()) {
      value.reset(false);
    }

  }

  public boolean exist(UUID player) {
    return this.animators.containsKey(player);
  }

  public void remove(UUID uuid) {
    this.animators.remove(uuid);
  }

  public void tick(float delta) {
  }

  public void sync(ServerPlayer player) {
    Stream<UUID> var10002 = this.animators.keySet().stream();
    UUID var10003 = player.getUUID();
    Objects.requireNonNull(var10003);
    var10002 = var10002.filter(Predicate.not(var10003::equals));
    Map<UUID, T> var4 = this.animators;
    Objects.requireNonNull(var4);
    ClientUpdateAnimatorPacket packet = new ClientUpdateAnimatorPacket(var10002.map(var4::get).map(AnimatorDataPacket::new).toList());
    SimpleAnimator.getNetwork().sendToPlayer(packet, player);
  }
}
