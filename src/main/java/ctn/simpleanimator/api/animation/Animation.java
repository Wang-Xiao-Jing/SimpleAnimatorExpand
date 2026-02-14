//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ctn.simpleanimator.api.animation.keyframe.VariableHolder;
import ctn.simpleanimator.core.JsonUtils;
import ctn.simpleanimator.core.PlayerUtils;
import ctn.simpleanimator.core.client.ClientAnimator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.FriendlyByteBuf;

import java.io.Reader;
import java.util.Optional;

public class Animation {
  private static final String KEY_REQUEST = "invite";
  private static final String KEY_WAITING = "waiting";
  private static final String KEY_CANCEL = "cancel";
  private static final String KEY_ENTER = "enter";
  private static final String KEY_LOOP = "main";
  private static final String KEY_EXIT = "exit";
  private final AnimationSection enter;
  private final AnimationSection loop;
  private final AnimationSection exit;
  private final boolean movable;
  private final boolean abortable;
  private final boolean useVanillaRig;
  private final Type type;
  private final byte unlockFlag;

  public static Animation[] fromStream(Reader reader) {
    JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
    return serialize(object);
  }

  public static Animation[] serialize(JsonObject json) {
    JsonObject object = json.getAsJsonObject("animations");
    if (!object.has("main")) {
      throw new RuntimeException("Cannot accept animation without \"main\"!");
    } else {
      boolean movable = JsonUtils.getBoolean("movable", json, false);
      boolean abortable = JsonUtils.getBoolean("abortable", json, true);
      boolean rig = JsonUtils.getBoolean("useVanillaRig", json, true);
      byte unlock = getUnlocks(json);
      Animation[] animations;
      if (isInteractiveAnimation(object)) {
        animations = new Animation[3];
        AnimationSection request = AnimationSection.fromJsonObject(object.getAsJsonObject("invite"), Animation.Type.INVITE);
        if (request == null) {
          throw new RuntimeException("Required animation: \"request\"!");
        }

        AnimationSection waiting = AnimationSection.fromJsonObject(object.getAsJsonObject("waiting"), Animation.Type.INVITE);
        if (waiting == null) {
          throw new RuntimeException("Required animation: \"waiting\"!");
        }

        if (!waiting.repeatable()) {
          throw new RuntimeException("\"waiting\" should be looped!");
        }

        AnimationSection cancel = AnimationSection.fromJsonObject(object.getAsJsonObject("cancel"), Animation.Type.INVITE);
        animations[0] = new Animation(request, waiting, cancel, movable, true, rig, unlock, Animation.Type.INVITE);
        animations[1] = new Animation(AnimationSection.fromJsonObject(object.getAsJsonObject("enter"), Animation.Type.REQUESTER), AnimationSection.fromJsonObject(object.getAsJsonObject("main"), Animation.Type.REQUESTER), AnimationSection.fromJsonObject(object.getAsJsonObject("exit"), Animation.Type.REQUESTER), movable, false, rig, unlock, Animation.Type.REQUESTER);
        animations[2] = new Animation(AnimationSection.fromJsonObject(object.getAsJsonObject("enter"), Animation.Type.RECEIVER), AnimationSection.fromJsonObject(object.getAsJsonObject("main"), Animation.Type.RECEIVER), AnimationSection.fromJsonObject(object.getAsJsonObject("exit"), Animation.Type.RECEIVER), movable, false, rig, unlock, Animation.Type.RECEIVER);
      } else {
        animations = new Animation[]{new Animation(AnimationSection.fromJsonObject(object.getAsJsonObject("enter")), AnimationSection.fromJsonObject(object.getAsJsonObject("main")), AnimationSection.fromJsonObject(object.getAsJsonObject("exit")), movable, abortable, rig, unlock, Animation.Type.SIMPLE)};
      }

      return animations;
    }
  }

  private static byte getUnlocks(JsonObject object) {
    JsonElement element = object.get("unlock");
    if (element != null && element.isJsonArray()) {
      byte flag = -1;

      for (JsonElement ele : element.getAsJsonArray()) {
        if (ele.isJsonPrimitive()) {
          ModelBone bone = ModelBone.fromString(ele.getAsString());
          if (bone != null) {
            flag = bone.remove(flag);
          }
        }
      }

      return flag;
    } else {
      return -1;
    }
  }

  private Animation(AnimationSection enter, AnimationSection loop, AnimationSection exit, boolean movable, boolean abortable, boolean useVanillaRig, byte lock, Type type) {
    this.enter = enter;
    this.loop = loop;
    this.exit = exit;
    this.movable = movable;
    this.abortable = abortable;
    this.useVanillaRig = useVanillaRig;
    this.unlockFlag = lock;
    this.type = type;
  }

  private static boolean isInteractiveAnimation(JsonObject json) {
    return json.has("invite");
  }

  private static boolean getBoolean(JsonObject json, String key, boolean def) {
    return json.has(key) ? json.get(key).getAsBoolean() : def;
  }

  public AnimationSection get(AnimationState state) {
    AnimationSection var10000;
    switch (state) {
      case ENTER -> var10000 = this.enter != null ? this.enter : this.loop;
      case EXIT -> var10000 = this.exit != null ? this.exit : this.loop;
      default -> var10000 = this.loop;
    }

    return var10000;
  }

  public float getFadeIn(ClientAnimator animator) {
    AnimationSection animation = this.get(animator.getCurState());
    if (animator.getNextState() == AnimationState.IDLE) {
      return animation.getFadeOut();
    } else {
      return animator.getCurState() == AnimationState.IDLE ? this.get(animator.getNextState()).getFadeIn() : animation.getFadeIn();
    }
  }

  public boolean repeatable() {
    return this.loop.repeatable();
  }

  public boolean hasEnterAnimation() {
    return this.enter != null;
  }

  public boolean hasExitAnimation() {
    return this.exit != null;
  }

  public void update(ModelBone bone, ModelPart part, ClientAnimator animator) {
    AnimationSection animation = animator.isTransferring() ? this.get(animator.getNextState()) : this.get(animator.getCurState());
    part.xRot = PlayerUtils.normalizeRadians(part.xRot);
    part.yRot = PlayerUtils.normalizeRadians(part.yRot);
    animation.update(bone, part, animator, this.getFadeIn(animator));
  }

  public void update(String variable, VariableHolder holder, ClientAnimator animator) {
    AnimationSection animation = animator.isTransferring() ? this.get(animator.getNextState()) : this.get(animator.getCurState());
    animation.update(variable, holder, animator, this.getFadeIn(animator));
  }

  public boolean isOverride(ModelBone bone) {
    return bone.in(this.unlockFlag);
  }

  public boolean isOverrideHead() {
    return ModelBone.HEAD.in(this.unlockFlag);
  }

  public boolean isOverrideHands() {
    return ModelBone.LEFT_ARM.in(this.unlockFlag) || ModelBone.RIGHT_ARM.in(this.unlockFlag);
  }

  public boolean isMovable() {
    return this.movable;
  }

  public boolean isAbortable() {
    return this.abortable;
  }

  public Type getType() {
    return this.type;
  }

  public Object2IntMap<String> getVariables() {
    Object2IntMap<String> set = new Object2IntOpenHashMap<>();
    if (this.enter != null) {
      this.enter.getVariables(set);
    }

    if (this.loop != null) {
      this.loop.getVariables(set);
    }

    if (this.exit != null) {
      this.exit.getVariables(set);
    }

    return set;
  }

  public static void toNetwork(FriendlyByteBuf byteBuf, Animation group) {
    byteBuf.writeBoolean(group.movable);
    byteBuf.writeBoolean(group.abortable);
    byteBuf.writeBoolean(group.useVanillaRig);
    byteBuf.writeByte(group.unlockFlag);
    byteBuf.writeEnum(group.type);
    byteBuf.writeOptional(Optional.ofNullable(group.enter), AnimationSection::toNetwork);
    byteBuf.writeOptional(Optional.ofNullable(group.loop), AnimationSection::toNetwork);
    byteBuf.writeOptional(Optional.ofNullable(group.exit), AnimationSection::toNetwork);
  }

  public static Animation fromNetwork(FriendlyByteBuf byteBuf) {
    boolean movable = byteBuf.readBoolean();
    boolean abortable = byteBuf.readBoolean();
    boolean rigModified = byteBuf.readBoolean();
    byte unlock = byteBuf.readByte();
    Type type = byteBuf.readEnum(Type.class);
    Optional<AnimationSection> enter = byteBuf.readOptional(AnimationSection::fromNetwork);
    Optional<AnimationSection> loop = byteBuf.readOptional(AnimationSection::fromNetwork);
    Optional<AnimationSection> exit = byteBuf.readOptional(AnimationSection::fromNetwork);
    return new Animation(enter.orElse(null), loop.orElse(null), exit.orElse(null), movable, abortable, rigModified, unlock, type);
  }

  public boolean isModifiedRig() {
    return !this.useVanillaRig;
  }

  public boolean isVanillaRig() {
    return this.useVanillaRig;
  }

  public enum Type {
    SIMPLE("", ""),
    INVITE("invite/", "requester"),
    REQUESTER("requester/", "requester"),
    RECEIVER("receiver/", "receiver");

    public final String path;
    public final String prefix;

    Type(String path, String prefix) {
      this.path = path;
      this.prefix = prefix;
    }
  }
}
