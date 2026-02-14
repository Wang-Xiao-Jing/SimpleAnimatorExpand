//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.animation;

import ctn.simpleanimator.api.INavigatable;
import ctn.simpleanimator.api.animation.Interaction;
import ctn.simpleanimator.api.animation.RequestHolder;
import ctn.simpleanimator.core.PlayerUtils;
import ctn.simpleanimator.core.SimpleAnimator;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InteractionManager {
  protected final Map<UUID, RequestHolder> requests = new HashMap();

  public void reset() {
    for (RequestHolder holder : this.requests.values()) {
      holder.reset();
    }

  }

  public @Nullable RequestHolder get(UUID requester) {
    return this.requests.get(requester);
  }

  public boolean exist(UUID requester) {
    return this.requests.containsKey(requester);
  }

  public boolean invite(Player inviter, Player target, ResourceLocation interaction) {
    UUID inviterUUID = inviter.getUUID();
    if (inviterUUID.equals(target.getUUID())) {
      return false;
    } else {
      Animator animator = SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(inviterUUID);
      if (animator.isRunning() && !animator.getAnimation().isAbortable()) {
        return false;
      } else {
        RequestHolder holder = this.createIfAbsent(inviterUUID);
        if (PlayerUtils.inSameDimension(inviter, target) && !(inviter.distanceToSqr(target) > (double) SimpleAnimator.getProxy().getConfig().interactInviteDistanceSquare)) {
          Vec3 position = PlayerUtils.getRelativePositionWorldSpace(inviter, 1.0F, 0.0F);
          if (!PlayerUtils.isPositionSave(position, inviter.level())) {
            return false;
          } else {
            float rotY = PlayerUtils.getLookAtRotY(inviter, target.position());
            inviter.setYRot(rotY);
            inviter.yRotO = inviter.getYRot();
            inviter.yHeadRot = rotY;
            inviter.yHeadRotO = rotY;
            inviter.yBodyRot = rotY;
            inviter.yBodyRotO = rotY;
            this.cancel(inviterUUID);
            holder.invite(target.getUUID(), interaction);
            Interaction pInteraction = SimpleAnimator.getProxy().getAnimationManager().getInteraction(interaction);
            if (pInteraction != null) {
              animator.play(pInteraction.invite());
            }

            return true;
          }
        } else {
          return false;
        }
      }
    }
  }

  public boolean accept(Player requester, Player acceptor, boolean forced) {
    UUID acceptorUUID = acceptor.getUUID();
    UUID requesterUUID = requester.getUUID();
    RequestHolder holder = this.createIfAbsent(requesterUUID);
    if (holder.hasRequest() && acceptorUUID.equals(holder.getTarget())) {
      if (!PlayerUtils.inSameDimension(requester, requester)) {
        return false;
      } else {
        this.cancel(acceptorUUID);
        Vec3 position = PlayerUtils.getRelativePositionWorldSpace(requester, 1.0F, 0.0F);
        if (!forced && acceptor.distanceToSqr(position) > (double) 0.1F) {
          ((INavigatable) acceptor).simpleanimator$navigate(requester);
          return false;
        } else {
          Interaction interaction = SimpleAnimator.getProxy().getAnimationManager().getInteraction(holder.getInteraction());
          holder.success();
          acceptor.setPos(position);
          acceptor.lookAt(Anchor.EYES, requester.getEyePosition());
          if (interaction != null) {
            SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(requesterUUID).play(interaction.requester());
            SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(acceptorUUID).play(interaction.receiver());
          }

          return true;
        }
      }
    } else {
      return false;
    }
  }

  public void cancel(UUID requester) {
    RequestHolder request = this.get(requester);
    if (request != null) {
      request.reset();
      SimpleAnimator.getProxy().getAnimatorManager().createIfAbsent(requester).stop();
    }

  }

  public RequestHolder createIfAbsent(UUID uuid) {
    return this.requests.computeIfAbsent(uuid, RequestHolder::new);
  }

  public void remove(UUID uuid) {
    this.requests.remove(uuid);
  }

  public record Request(UUID target, ResourceLocation interaction) {
  }
}
