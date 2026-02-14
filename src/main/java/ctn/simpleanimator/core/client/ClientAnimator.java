//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.client;

import ctn.simpleanimator.api.animation.AnimationState;
import ctn.simpleanimator.api.animation.IKBone;
import ctn.simpleanimator.api.animation.ModelBone;
import ctn.simpleanimator.api.animation.keyframe.VariableHolder;
import ctn.simpleanimator.api.animation.keyframe.VariableHolder.Immutable;
import ctn.simpleanimator.api.event.client.ClientAnimatorStateEvent;
import ctn.simpleanimator.core.PlayerUtils;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.animation.Animator;
import ctn.simpleanimator.core.client.state.IAnimationState;
import ctn.simpleanimator.core.client.state.IAnimationState.Impl;
import ctn.simpleanimator.core.network.packet.AnimatorDataPacket;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientAnimator extends Animator {
  private static final Object2IntMap<String> BUILTIN_VARIABLES;
  private static final ModelPart ROOT;
  private final EnumMap<ModelBone, Cache> cache = new EnumMap(ModelBone.class);
  private final EnumMap<IKBone, IKCache> ikCache = new EnumMap(IKBone.class);
  private final Object2ObjectMap<String, VariableHolder> variables = new Object2ObjectOpenHashMap();
  private boolean processed = false;
  private boolean shouldUpdate = false;

  public ClientAnimator(UUID uuid) {
    super(uuid);
    ObjectIterator var2 = BUILTIN_VARIABLES.object2IntEntrySet().iterator();

    while (var2.hasNext()) {
      Object2IntMap.Entry<String> variable = (Object2IntMap.Entry) var2.next();
      this.variables.put(variable.getKey(), VariableHolder.get(variable.getIntValue()));
    }

    for (ModelBone value : ModelBone.values()) {
      this.cache.put(value, new Cache(new Vector3f(), new Vector3f(), new Vector3f()));
    }

    for (IKBone value : IKBone.values()) {
      this.ikCache.put(value, new IKCache(new Vector3f(), new Vector3f()));
    }

  }

  public void update(AnimatorDataPacket packet) {
    if (this.isLocalPlayer()) {
      SimpleAnimator.getNetwork().update(packet);
    }
  }

  public void sync(AnimatorDataPacket packet) {
    ResourceLocation location = this.animationLocation;
    super.sync(packet);
    if (!location.equals(this.animationLocation) || this.animation != null) {
      Object2IntMap<String> animationVariables = this.animation.getVariables();
      ObjectIterator var4 = animationVariables.object2IntEntrySet().iterator();

      while (var4.hasNext()) {
        Object2IntMap.Entry<String> entry = (Object2IntMap.Entry) var4.next();
        this.variables.put(entry.getKey(), VariableHolder.get(entry.getIntValue()));
      }
    }

  }

  public boolean play(ResourceLocation location) {
    Player player = Minecraft.getInstance().level.getPlayerByUUID(this.uuid);
    if (this.animation == null) {
      this.processed = false;
    }

    if (!super.play(location)) {
      return false;
    } else if (this.animation == null) {
      return false;
    } else {
      ObjectIterator var3 = this.animation.getVariables().object2IntEntrySet().iterator();

      while (var3.hasNext()) {
        Object2IntMap.Entry<String> entry = (Object2IntMap.Entry) var3.next();
        this.variables.computeIfAbsent(entry.getKey(), (key) -> VariableHolder.get(entry.getIntValue()));
      }

      this.nextState = this.animation.hasEnterAnimation() ? AnimationState.ENTER : AnimationState.LOOP;
      this.procState = ProcessState.TRANSFER;
      this.shouldUpdate = true;
      return true;
    }
  }

  public boolean stop() {
    if (this.animation != null && (!this.canStop() || super.stop())) {
      this.timer = 0.0F;
      this.nextState = this.animation.hasExitAnimation() ? AnimationState.EXIT : AnimationState.IDLE;
      this.procState = ProcessState.TRANSFER;
      this.processed = true;
      return true;
    } else {
      return false;
    }
  }

  public void tick(float time) {
    if (this.animation != null) {
      this.timer += time * this.speed;
      this.shouldUpdate = true;
      switch (this.procState) {
        case TRANSFER:
          if (this.timer > this.animation.getFadeIn(this)) {
            this.timer = 0.0F;
            this.curState = this.nextState;
            Impl.get(this.curState).enter(this);
            this.procState = ProcessState.PROCESS;
            SimpleAnimator.EVENT_BUS.post(new ClientAnimatorStateEvent.Enter(this.uuid, this, this.animationLocation, this.animation, this.curState, this.nextState));
            this.update(new AnimatorDataPacket(this, false));
          }
          break;
        case PROCESS:
          if (this.timer > this.animation.get(this.curState).getLength()) {
            this.timer = 0.0F;
            IAnimationState impl = Impl.get(this.curState);
            if (impl.shouldEnd(this)) {
              this.nextState = impl.getNext(this);
              impl.exit(this);
              SimpleAnimator.EVENT_BUS.post(new ClientAnimatorStateEvent.Exit(this.uuid, this, this.animationLocation, this.animation, this.curState, this.nextState));
              this.procState = ProcessState.TRANSFER;
              this.shouldUpdate = false;
            } else {
              SimpleAnimator.EVENT_BUS.post(new ClientAnimatorStateEvent.Loop(this.uuid, this, this.animationLocation, this.animation, this.curState, this.nextState));
            }

            this.update(new AnimatorDataPacket(this, false));
          }
      }
    }

  }

  public void process(PlayerModel<AbstractClientPlayer> model, Player player) {
    this.update(model, player);
    if (this.animation.isModifiedRig()) {
      Matrix4f pos = this.processModifiedBody(model.body);
      Vector3f rotation = pos.getEulerAnglesXYZ(new Vector3f());
      this.processModified(ModelBone.HEAD, model.head, pos, rotation);
      this.processModified(ModelBone.LEFT_ARM, model.leftArm, pos, rotation);
      this.processModified(ModelBone.RIGHT_ARM, model.rightArm, pos, rotation);
    } else {
      this.process(ModelBone.HEAD, model.head);
      this.process(ModelBone.BODY, model.body);
      this.process(ModelBone.LEFT_ARM, model.leftArm);
      this.process(ModelBone.RIGHT_ARM, model.rightArm);
    }

    if (!PlayerUtils.isRiding(player)) {
      this.process(ModelBone.LEFT_LEG, model.leftLeg);
      this.process(ModelBone.RIGHT_LEG, model.rightLeg);
    }

  }

  private void update(PlayerModel<AbstractClientPlayer> model, Player player) {
    if (this.animation != null) {
      if (this.shouldUpdate) {
        if (!PlayerUtils.isRiding(player)) {
          this.animation.update(ModelBone.ROOT, ROOT, this);
          this.animation.update(ModelBone.LEFT_LEG, model.leftLeg, this);
          this.animation.update(ModelBone.RIGHT_LEG, model.rightLeg, this);
        }

        this.animation.update(ModelBone.BODY, model.body, this);
        this.animation.update(ModelBone.HEAD, model.head, this);
        this.animation.update(ModelBone.LEFT_ARM, model.leftArm, this);
        this.animation.update(ModelBone.RIGHT_ARM, model.rightArm, this);
        this.variables.forEach((variable, holder) -> this.animation.update(variable, holder, this));
        this.resolveIK(player);
        this.shouldUpdate = false;
        this.processed = true;
      }

    }
  }

  private void resolveIK(Player player) {
    if (this.isRunning()) {
      Cache root = this.cache.get(ModelBone.ROOT);
      Cache body = this.cache.get(ModelBone.BODY);
      Matrix4f mat = (new Matrix4f()).rotateXYZ(root.rotation()).translate(body.position().x / 16.0F, body.position().y / 16.0F + 0.75F, body.position().z / 16.0F).rotateXYZ(body.rotation()).translate(0.0F, 0.75F, 0.0F);
      this.resolveIK(IKBone.HEAD, mat, player, 0.0F);
      this.resolveIK(IKBone.LEFT_ARM, mat, player, 0.3125F);
      this.resolveIK(IKBone.RIGHT_ARM, mat, player, -0.3125F);
    }
  }

  private void resolveIK(IKBone bone, Matrix4f mat, Player player, float left) {
    IKCache cache = this.ikCache.get(bone);
    Matrix4f local = (new Matrix4f(mat)).translate(left, 0.0F, 0.0F);
    Vector3f w2l = this.positionW2L(this.ikCache.get(bone).target, player).mul(-1.0F, 1.0F, 1.0F);
    Vector3f current = new Vector3f();
    local.getTranslation(current);
    if (!(this.getIKWeight(bone) <= 0.0F)) {
      Vector3f forward = new Vector3f(0.0F, -1.0F, 0.0F);
      Vector3f dir = (new Vector3f(w2l)).sub(current);
      local.transformDirection(forward);
      Quaternionf quaternionf = (new Quaternionf()).rotateTo(forward, dir);
      if (bone == IKBone.HEAD) {
        quaternionf.rotateX(((float) Math.PI / 2F));
      }

      Vector3f vector3f = new Vector3f();
      quaternionf.getEulerAnglesXYZ(vector3f);
      cache.rotation().set(Mth.clamp(vector3f.x, -(float) Math.PI, (float) Math.PI), Mth.clamp(vector3f.y, -(float) Math.PI, (float) Math.PI), Mth.clamp(vector3f.z, -(float) Math.PI, (float) Math.PI));
    }
  }

  private Vector3f positionW2L(Vector3f world, Player player) {
    Matrix4f mat = (new Matrix4f()).rotateY(player.yBodyRot * ((float) Math.PI / 180F));
    Vector3f sub = (new Vector3f(world)).sub(player.position().toVector3f());
    return mat.transformPosition(sub);
  }

  public Cache getCache(ModelBone bone) {
    return this.cache.get(bone);
  }

  public boolean hasVariable(String variable) {
    return this.variables.containsKey(variable);
  }

  public VariableHolder getVariable(String variable) {
    return this.variables.getOrDefault(variable, Immutable.INSTANCE);
  }

  public float getIKWeight(IKBone bone) {
    return this.getVariable(bone.varName).getAsFloat();
  }

  public void setIkTarget(IKBone bone, Vector3f worldPosition) {
    this.ikCache.get(bone).target().set(worldPosition);
  }

  public Vector3f getIkTarget(IKBone bone) {
    return new Vector3f(this.ikCache.get(bone).target());
  }

  public void resetIK() {
    for (IKCache value : this.ikCache.values()) {
      value.reset();
    }

  }

  private void process(ModelBone bone, ModelPart part) {
    Cache cache = this.cache.get(bone);
    Vector3f position = cache.position;
    PartPose pose = this.animation.isOverride(bone) ? part.getInitialPose() : part.storePose();
    part.x = pose.x + position.x;
    part.y = pose.y - position.y;
    part.z = pose.z + position.z;
    Vector3f rotation = cache.rotation();
    if (bone.getIk() != null) {
      rotation.lerp(this.ikCache.get(bone.getIk()).rotation(), this.getIKWeight(bone.getIk()));
    }

    part.xRot = pose.xRot + rotation.x;
    part.yRot = pose.yRot + rotation.y;
    part.zRot = pose.zRot + rotation.z;
  }

  private Matrix4f processModifiedBody(ModelPart body) {
    Cache cache = this.cache.get(ModelBone.BODY);
    Vector3f rotation = cache.rotation();
    PartPose pose = this.animation.isOverride(ModelBone.BODY) ? body.getInitialPose() : body.storePose();
    Quaternionf rot = (new Quaternionf()).rotateXYZ(pose.xRot + rotation.x, pose.yRot + rotation.y, pose.zRot + rotation.z);
    Vector3f position = (new Vector3f(pose.x, pose.y, pose.z)).sub(0.0F, 12.0F, 0.0F).rotate(rot).add(0.0F, 12.0F, 0.0F).add(cache.position.x, -cache.position.y, cache.position.z);
    body.x = position.x;
    body.y = position.y;
    body.z = position.z;
    Vector3f anglesXYZ = rot.getEulerAnglesXYZ(new Vector3f());
    body.xRot = anglesXYZ.x;
    body.yRot = anglesXYZ.y;
    body.zRot = anglesXYZ.z;
    return (new Matrix4f()).translate(body.x, body.y, body.z).rotateXYZ(body.xRot, body.yRot, body.zRot);
  }

  private void processModified(ModelBone bone, ModelPart part, Matrix4f parentMat, Vector3f parentRot) {
    Cache cache = this.cache.get(bone);
    PartPose pose = this.animation.isOverride(bone) ? part.getInitialPose() : part.storePose();
    Vector3f position = parentMat.transformPosition((new Vector3f(pose.x, pose.y, pose.z)).add(cache.position.x, -cache.position.y, cache.position.z));
    part.x = position.x;
    part.y = position.y;
    part.z = position.z;
    Vector3f rotation = (new Vector3f(cache.rotation())).add(pose.xRot, pose.yRot, pose.zRot).add(parentRot);
    if (bone.getIk() != null) {
      rotation.lerp(this.ikCache.get(bone.getIk()).rotation(), this.getIKWeight(bone.getIk()));
    }

    part.xRot = rotation.x;
    part.yRot = rotation.y;
    part.zRot = rotation.z;
  }

  public boolean canStop() {
    return this.curState == AnimationState.LOOP && this.nextState == AnimationState.LOOP;
  }

  public boolean isTransferring() {
    return this.procState == ProcessState.TRANSFER;
  }

  public boolean isProcessed() {
    return this.processed;
  }

  public boolean isLocalPlayer() {
    return this.uuid.equals(Minecraft.getInstance().player.getUUID());
  }

  public boolean isLocal() {
    return this.isLocalPlayer();
  }

  public void reset(boolean update) {
    super.reset(update);
    this.processed = false;
    this.cache.forEach((bone, cache) -> {
      cache.position.set(0.0F);
      cache.rotation.set(0.0F);
    });
    Map<String, VariableHolder> temp = new Object2ObjectOpenHashMap(this.variables.size());
    ObjectIterator var3 = BUILTIN_VARIABLES.keySet().iterator();

    while (var3.hasNext()) {
      String variable = (String) var3.next();
      temp.put(variable, this.variables.get(variable));
    }

    this.variables.clear();
    this.variables.putAll(temp);
    this.resetIK();
    if (update) {
      this.update(new AnimatorDataPacket(this, false));
    }

  }

  public Vector3f getCameraPosition() {
    Cache head = this.cache.get(ModelBone.HEAD);
    Cache root = this.cache.get(ModelBone.ROOT);
    Matrix4f mat = (new Matrix4f()).rotateXYZ(root.rotation()).translate(root.position()).translate(0.0F, 12.0F, 0.0F);
    return mat.translate(0.0F, 12.0F, 0.0F).translate(head.position()).invert().transformPosition(new Vector3f(0.0F, 0.0F, 0.0F)).add(0.0F, 24.0F, 0.0F).div(16.0F, -16.0F, 16.0F);
  }

  public Vector3f getCameraRotation() {
    return this.isRunning() && this.animation.isModifiedRig() ? (new Vector3f(this.cache.get(ModelBone.HEAD).rotation())).add(this.cache.get(ModelBone.BODY).rotation()).add(this.cache.get(ModelBone.ROOT).rotation()) : (new Vector3f(this.cache.get(ModelBone.HEAD).rotation())).add(this.cache.get(ModelBone.ROOT).rotation());
  }

  static {
    ROOT = new ModelPart(Collections.EMPTY_LIST, Collections.EMPTY_MAP);
    Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap();
    map.put(IKBone.HEAD.varName, 1);
    map.put(IKBone.LEFT_ARM.varName, 1);
    map.put(IKBone.RIGHT_ARM.varName, 1);
    BUILTIN_VARIABLES = Object2IntMaps.unmodifiable(map);
  }

  public record Cache(Vector3f position, Vector3f rotation, Vector3f worldPosition) {
  }

  public record IKCache(Vector3f target, Vector3f rotation) {
    public void reset() {
      this.target.set(0.0F);
      this.rotation.set(0.0F);
    }
  }
}
