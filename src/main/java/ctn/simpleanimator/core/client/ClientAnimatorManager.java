//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client;

import ctn.simpleanimator.core.animation.AnimatorManager;
import ctn.simpleanimator.core.network.packet.AnimatorDataPacket;
import ctn.simpleanimator.core.network.packet.batch.ClientUpdateAnimatorPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientAnimatorManager extends AnimatorManager<ClientAnimator> {
  public @NotNull ClientAnimator getLocalAnimator() {
    return this.createIfAbsent(Minecraft.getInstance().player.getUUID());
  }

  public ClientAnimator createIfAbsent(UUID player) {
    ClientAnimator animator = this.animators.get(player);
    if (!this.animators.containsKey(player)) {
      animator = new ClientAnimator(player);
      this.animators.put(player, animator);
    }

    return animator;
  }

  public void tick(float delta) {
    for (ClientAnimator animator : this.animators.values()) {
      animator.tick(delta);
    }

  }

  public void handleUpdateAnimator(ClientUpdateAnimatorPacket packet) {
    this.reset();

    for (AnimatorDataPacket data : packet.getAnimators()) {
      this.createIfAbsent(data.getOwner()).sync(data);
    }

    this.getLocalAnimator();
  }
}
