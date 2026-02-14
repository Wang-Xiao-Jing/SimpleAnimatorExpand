//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event.client;

import ctn.simpleanimator.api.event.ICancelable;
import ctn.simpleanimator.api.event.SAEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ClientNavigatorEvent extends SAEvent {
  protected ClientNavigatorEvent() {
  }

  public static class Start extends ClientNavigatorEvent implements ICancelable {
    private final Player target;
    private final float forward;
    private final float left;

    public Start(Player target, float forward, float left) {
      this.target = target;
      this.forward = forward;
      this.left = left;
    }

    public Player getTarget() {
      return this.target;
    }

    public float getForward() {
      return this.forward;
    }

    public float getLeft() {
      return this.left;
    }
  }

  public static class End extends ClientNavigatorEvent {
    private final boolean finished;

    public End(boolean finished) {
      this.finished = finished;
    }

    public boolean isFinished() {
      return this.finished;
    }
  }
}
