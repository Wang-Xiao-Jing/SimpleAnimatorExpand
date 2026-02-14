//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ctn.simpleanimator.api.animation.keyframe.*;
import ctn.simpleanimator.api.animation.keyframe.KeyFrame.Decoder;
import ctn.simpleanimator.core.client.ClientAnimator;
import ctn.simpleanimator.core.client.state.IAnimationState.Impl;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

public class AnimationSection {
  private static final Vector3f ZERO_POS = new Vector3f(0.0F);
  private static final Quaternionf ZERO_ROT = new Quaternionf();
  private static final String PREFIX_VARIABLE = "var_";
  private static final Object2ByteMap<String> KEYWORDS;
  private static final float DEFAULT_FADE_IN = 0.05F;
  private static final float DEFAULT_FADE_OUT = 0.05F;
  private final boolean repeat;
  private final float length;
  private final float fadeIn;
  private final float fadeOut;
  private final EnumMap<ModelBone, BoneData> keyFrames;
  private final Object2ObjectMap<String, VariableKeyFrame.Group> varFrames;

  public static AnimationSection fromJsonObject(JsonObject json, Animation.Type type) {
    if (json == null) {
      return null;
    } else {
      boolean repeat = json.has("loop") && json.get("loop").getAsBoolean();
      float length = json.get("animation_length").getAsFloat();
      String var = json.has("anim_time_update") ? json.get("anim_time_update").getAsString() : "";
      Map<String, String> variables = getVariables(var);
      float fadeIn = tryParse(variables.get("fade_in"), 0.05F);
      float fadeOut = tryParse(variables.get("fade_out"), 0.05F);
      EnumMap<ModelBone, BoneData> keyFrames = new EnumMap(ModelBone.class);
      Object2ObjectMap<String, VariableKeyFrame.Group> varFrames = new Object2ObjectOpenHashMap();
      JsonObject bones = json.getAsJsonObject("bones");

      for (Map.Entry<String, JsonElement> entry : bones.entrySet()) {
        if (entry.getKey().startsWith(type.prefix)) {
          String key = entry.getKey().substring(type.prefix.length() + 1);
          if (key.startsWith("var_")) {
            getVarFramesFromBones(key.substring(4), entry.getValue().getAsJsonObject(), length, varFrames);
          } else {
            ModelBone bone = ModelBone.fromString(key);
            if (bone != null) {
              VectorKeyFrame[] rotation = getRotation(entry.getValue().getAsJsonObject().get("rotation"), bone, length);
              VectorKeyFrame[] position = getPosition(entry.getValue().getAsJsonObject().get("position"), bone, length);
              keyFrames.put(bone, new BoneData(rotation, position));
            }
          }
        }
      }

      return new AnimationSection(repeat, length, fadeIn, fadeOut, keyFrames, varFrames);
    }
  }

  public static AnimationSection fromJsonObject(JsonObject json) {
    if (json == null) {
      return null;
    } else {
      boolean repeat = json.has("loop") && json.get("loop").getAsBoolean();
      float length = json.get("animation_length").getAsFloat();
      String var = json.has("anim_time_update") ? json.get("anim_time_update").getAsString() : "";
      Map<String, String> variables = getVariables(var);
      float fadeIn = tryParse(variables.get("fade_in"), 0.05F);
      float fadeOut = tryParse(variables.get("fade_out"), 0.05F);
      EnumMap<ModelBone, BoneData> keyFrames = new EnumMap(ModelBone.class);
      Object2ObjectMap<String, VariableKeyFrame.Group> varFrames = new Object2ObjectOpenHashMap();
      JsonObject bones = json.getAsJsonObject("bones");

      for (Map.Entry<String, JsonElement> entry : bones.entrySet()) {
        String key = entry.getKey();
        if (key.startsWith("var_")) {
          getVarFramesFromBones(key.substring(4), entry.getValue().getAsJsonObject(), length, varFrames);
        } else {
          ModelBone bone = ModelBone.fromString(key);
          if (bone != null) {
            VectorKeyFrame[] rotation = getRotation(entry.getValue().getAsJsonObject().get("rotation"), bone, length);
            VectorKeyFrame[] position = getPosition(entry.getValue().getAsJsonObject().get("position"), bone, length);
            keyFrames.put(bone, new BoneData(rotation, position));
          }
        }
      }

      return new AnimationSection(repeat, length, fadeIn, fadeOut, keyFrames, varFrames);
    }
  }

  public void getVariables(Object2IntMap<String> set) {
    ObjectIterator var2 = this.varFrames.entrySet().iterator();

    while (var2.hasNext()) {
      Map.Entry<String, VariableKeyFrame.Group> entry = (Map.Entry) var2.next();
      set.put(entry.getKey(), entry.getValue().variableSize());
    }

  }

  private static float tryParse(String str, float def) {
    if (StringUtil.isNullOrEmpty(str)) {
      return def;
    } else {
      try {
        return Float.parseFloat(str);
      } catch (NumberFormatException var3) {
        return def;
      }
    }
  }

  private static void getVarFramesFromBones(String key, JsonObject object, float length, Object2ObjectMap<String, VariableKeyFrame.Group> map) {
    String[] split = key.split("_+");
    int i = 0;
    int[] sizes = new int[]{1, 1, 1};
    String[] keys = new String[3];

    for (String string : split) {
      byte b = KEYWORDS.getOrDefault(string, (byte) 0);
      if (b != 0) {
        sizes[i] = b;
      } else {
        keys[i++] = string;
        if (i == 3) {
          break;
        }
      }
    }

    if (i > 0) {
      VariableKeyFrame.Group array = getVarsFromBone(object.get("rotation"), sizes[0], length);
      if (array != null) {
        map.put(keys[0], array);
      }
    }

    if (i > 1) {
      VariableKeyFrame.Group array = getVarsFromBone(object.get("position"), sizes[1], length);
      if (array != null) {
        map.put(keys[1], array);
      }
    }

    if (i > 2) {
      VariableKeyFrame.Group array = getVarsFromBone(object.get("scale"), sizes[2], length);
      if (array != null) {
        map.put(keys[2], array);
      }
    }

  }

  private static VariableKeyFrame.Group getVarsFromBone(JsonElement element, int size, float length) {
    if (element == null) {
      return null;
    } else if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      VariableHolder pre = VariableHolder.fromJsonArray(array, size);
      return new VariableKeyFrame.Group(new VariableKeyFrame[]{new VariableKeyFrame(0.0F, pre, pre, LerpMode.LINEAR), new VariableKeyFrame(length, pre, pre, LerpMode.LINEAR)}, size);
    } else {
      JsonObject object = element.getAsJsonObject();
      VariableKeyFrame[] array = object.entrySet().stream().map((entry) -> {
        float time = Float.parseFloat(entry.getKey());
        LerpMode mode = LerpMode.LINEAR;
        JsonArray pre;
        JsonArray post;
        if (entry.getValue().isJsonArray()) {
          pre = entry.getValue().getAsJsonArray();
          post = null;
        } else {
          JsonObject obj = entry.getValue().getAsJsonObject();
          pre = obj.getAsJsonArray("pre");
          post = obj.getAsJsonArray("post");
          if (pre == null) {
            pre = post;
          }

          JsonElement lerpMode = obj.get("lerp_mode");
          mode = lerpMode == null ? LerpMode.LINEAR : LerpMode.valueOf(lerpMode.getAsString().toUpperCase());
        }

        VariableHolder preHolder = VariableHolder.fromJsonArray(pre, size);
        VariableHolder postHolder = post == null ? preHolder : VariableHolder.fromJsonArray(post, size);
        return new VariableKeyFrame(time, preHolder, postHolder, mode);
      }).toArray((x$0) -> new VariableKeyFrame[x$0]);
      return new VariableKeyFrame.Group(array, size);
    }
  }

  private static Map<String, String> getVariables(String input) {
    Map<String, String> result = new HashMap();
    if (StringUtil.isNullOrEmpty(input)) {
      return result;
    } else {
      String[] pairs = input.split(",\\s*");

      for (String pair : pairs) {
        String[] keyValue = pair.split("=\\s*");
        if (keyValue.length == 2) {
          String key = keyValue[0].trim();
          result.put(key, keyValue[1].trim());
        }
      }

      return result;
    }
  }

  private static VectorKeyFrame[] getRotation(JsonElement element, ModelBone bone, float length) {
    if (element == null) {
      return new VectorKeyFrame[0];
    } else if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      Vector3f vector3f = new Vector3f((float) Math.toRadians(array.get(0).getAsFloat()), (float) Math.toRadians(array.get(1).getAsFloat()), (float) Math.toRadians(array.get(2).getAsFloat()));
      return new VectorKeyFrame[]{new VectorKeyFrame(0.0F, vector3f, vector3f, LerpMode.LINEAR), new VectorKeyFrame(length, vector3f, vector3f, LerpMode.LINEAR)};
    } else {
      JsonObject object = element.getAsJsonObject();
      return object.entrySet().stream().map((entry) -> {
        float time = Float.parseFloat(entry.getKey());
        LerpMode mode = LerpMode.LINEAR;
        JsonArray pre;
        JsonArray post;
        if (entry.getValue().isJsonArray()) {
          pre = entry.getValue().getAsJsonArray();
          post = null;
        } else {
          JsonObject obj = entry.getValue().getAsJsonObject();
          pre = obj.getAsJsonArray("pre");
          post = obj.getAsJsonArray("post");
          if (pre == null) {
            pre = post;
          }

          JsonElement lerpMode = obj.get("lerp_mode");
          mode = lerpMode == null ? LerpMode.LINEAR : LerpMode.valueOf(lerpMode.getAsString().toUpperCase());
        }

        Vector3f preRotation = new Vector3f((float) Math.toRadians(pre.get(0).getAsFloat()), (float) Math.toRadians(pre.get(1).getAsFloat()), (float) Math.toRadians(pre.get(2).getAsFloat()));
        Vector3f postRotation = post == null ? preRotation : new Vector3f((float) Math.toRadians(post.get(0).getAsFloat()), (float) Math.toRadians(post.get(1).getAsFloat()), (float) Math.toRadians(post.get(2).getAsFloat()));
        return new VectorKeyFrame(time, preRotation, postRotation, mode);
      }).toArray((x$0) -> new VectorKeyFrame[x$0]);
    }
  }

  private static VectorKeyFrame[] getPosition(JsonElement element, ModelBone bone, float length) {
    if (element == null) {
      return new VectorKeyFrame[0];
    } else if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      Vector3f vector3f = new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
      return new VectorKeyFrame[]{new VectorKeyFrame(0.0F, vector3f, vector3f, LerpMode.LINEAR), new VectorKeyFrame(length, vector3f, vector3f, LerpMode.LINEAR)};
    } else {
      JsonObject object = element.getAsJsonObject();
      return object.entrySet().stream().map((entry) -> {
        float time = Float.parseFloat(entry.getKey());
        LerpMode mode = LerpMode.LINEAR;
        JsonArray pre;
        JsonArray post;
        if (entry.getValue().isJsonArray()) {
          pre = entry.getValue().getAsJsonArray();
          post = null;
        } else {
          JsonObject obj = entry.getValue().getAsJsonObject();
          pre = obj.getAsJsonArray("pre");
          post = obj.getAsJsonArray("post");
          if (pre == null) {
            pre = post;
          }

          JsonElement lerpMode = obj.get("lerp_mode");
          mode = lerpMode == null ? LerpMode.LINEAR : LerpMode.valueOf(lerpMode.getAsString().toUpperCase());
        }

        Vector3f prePosition = new Vector3f(pre.get(0).getAsFloat(), pre.get(1).getAsFloat(), pre.get(2).getAsFloat());
        Vector3f postPosition = post == null ? prePosition : new Vector3f(post.get(0).getAsFloat(), post.get(1).getAsFloat(), post.get(2).getAsFloat());
        if (bone == ModelBone.LEFT_ARM || bone == ModelBone.RIGHT_ARM) {
          prePosition.y += 0.5F;
        }

        return new VectorKeyFrame(time, prePosition, postPosition, mode);
      }).toArray((x$0) -> new VectorKeyFrame[x$0]);
    }
  }

  public AnimationSection(boolean repeat, float length, float fadeIn, float fadeOut, EnumMap<ModelBone, BoneData> keyFrames, Object2ObjectMap<String, VariableKeyFrame.Group> varFrames) {
    this.repeat = repeat;
    this.length = length;
    this.fadeIn = fadeIn;
    this.fadeOut = fadeOut;
    this.keyFrames = keyFrames;
    this.varFrames = varFrames;
  }

  public void update(ModelBone bone, ModelPart part, ClientAnimator animator, float fadeIn) {
    ClientAnimator.Cache cache = animator.getCache(bone);
    BoneData data = this.keyFrames.get(bone);
    Vector3f position = ZERO_POS;
    Vector3f rotation = ZERO_POS;
    if (animator.getAnimation().isOverride(bone)) {
      PartPose initialPose = part.getInitialPose();
      PartPose pose = PartPose.offsetAndRotation(part.x - initialPose.x, initialPose.y - part.y, part.z - initialPose.z, part.xRot - initialPose.xRot, part.yRot - initialPose.yRot, part.zRot - initialPose.zRot);
      position = new Vector3f(pose.x, pose.y, pose.z);
      rotation = new Vector3f(pose.xRot, pose.yRot, pose.zRot);
    }

    if (data == null) {
      cache.position().set(position);
      cache.rotation().set(rotation);
    } else {
      VectorKeyFrame[] posFrames = data.position();
      VectorKeyFrame[] rotFrames = data.rotation();
      if (animator.isTransferring()) {
        float time = fadeIn == 0.0F ? 1.0F : Mth.clamp(animator.getTimer() / fadeIn, 0.0F, 1.0F);
        if (posFrames.length != 0) {
          cache.position().set(Interpolation.linerInterpolation(Impl.get(animator.getCurState()).getSrc(cache.position(), position), Impl.get(animator.getNextState()).getDest(posFrames[0].getPre(), position), time));
        } else {
          cache.position().set(Interpolation.linerInterpolation(Impl.get(animator.getCurState()).getSrc(cache.position(), position), Impl.get(animator.getNextState()).getDest(position, position), time));
        }

        if (rotFrames.length != 0) {
          cache.rotation().set(Interpolation.linerInterpolation(Impl.get(animator.getCurState()).getSrc(cache.rotation(), rotation), Impl.get(animator.getNextState()).getDest(rotFrames[0].getPre(), rotation), time));
        } else {
          cache.rotation().set(Interpolation.linerInterpolation(Impl.get(animator.getCurState()).getSrc(cache.rotation(), rotation), Impl.get(animator.getNextState()).getDest(rotation, rotation), time));
        }
      } else {
        float time = Mth.clamp(animator.getTimer(), 0.0F, this.length);
        cache.position().set(Interpolation.interpolation(posFrames, cache.position(), time));
        cache.rotation().set(Interpolation.interpolation(rotFrames, cache.rotation(), time));
      }

    }
  }

  public void update(String variable, VariableHolder holder, ClientAnimator animator, float fadeIn) {
    VariableKeyFrame.Group group = this.varFrames.get(variable);
    if (group == null) {
      holder.setValue(0.0F);
    } else {
      VariableKeyFrame[] frames = group.keyFrames();
      VariableHolder target = new VariableHolder(0.0F);
      if (animator.isTransferring()) {
        float time = fadeIn == 0.0F ? 1.0F : Mth.clamp(animator.getTimer() / fadeIn, 0.0F, 1.0F);
        if (frames.length != 0) {
          holder.setValue(Interpolation.linerInterpolation(Impl.get(animator.getCurState()).getSrc(holder, target), Impl.get(animator.getNextState()).getDest(frames[0].getPre(), target), time));
        } else {
          holder.setValue(Interpolation.linerInterpolation(Impl.get(animator.getCurState()).getSrc(holder, target), Impl.get(animator.getNextState()).getDest(target, target), time));
        }
      } else {
        float time = Mth.clamp(animator.getTimer(), 0.0F, this.length);
        holder.setValue(Interpolation.interpolation(frames, holder, time));
      }

    }
  }

  public boolean repeatable() {
    return this.repeat;
  }

  public float getLength() {
    return this.length;
  }

  public float getFadeIn() {
    return this.fadeIn;
  }

  public float getFadeOut() {
    return this.fadeOut;
  }

  public static void toNetwork(FriendlyByteBuf byteBuf, AnimationSection animation) {
    byteBuf.writeBoolean(animation.repeat);
    byteBuf.writeFloat(animation.length);
    byteBuf.writeFloat(animation.fadeIn);
    byteBuf.writeFloat(animation.fadeOut);

    for (ModelBone value : ModelBone.values()) {
      byteBuf.writeOptional(Optional.ofNullable(animation.keyFrames.get(value)), BoneData::toNetwork);
    }

    byteBuf.writeMap(animation.varFrames, FriendlyByteBuf::writeUtf, VariableKeyFrame.Group::toNetwork);
  }

  public static AnimationSection fromNetwork(FriendlyByteBuf byteBuf) {
    boolean repeat = byteBuf.readBoolean();
    float length = byteBuf.readFloat();
    float fadeIn = byteBuf.readFloat();
    float fadeOut = byteBuf.readFloat();
    EnumMap<ModelBone, BoneData> map = new EnumMap(ModelBone.class);

    for (ModelBone value : ModelBone.values()) {
      Optional<BoneData> optional = byteBuf.readOptional(BoneData::fromNetwork);
      optional.ifPresent((boneData) -> map.put(value, boneData));
    }

    Object2ObjectMap<String, VariableKeyFrame.Group> varFrames = byteBuf.readMap(Object2ObjectOpenHashMap::new, FriendlyByteBuf::readUtf, VariableKeyFrame.Group::fromNetwork);
    return new AnimationSection(repeat, length, fadeIn, fadeOut, map, varFrames);
  }

  private static void writeVariableKeyFrames(FriendlyByteBuf byteBuf, VariableKeyFrame[] frames) {
    AnimationSection.BoneData.writeKeyFrames(byteBuf, frames);
  }

  private static VariableKeyFrame[] readVariableKeyFrames(FriendlyByteBuf byteBuf) {
    return BoneData.readKeyFrames(byteBuf, (x$0) -> new VariableKeyFrame[x$0], VariableKeyFrame.class);
  }

  static {
    Object2ByteOpenHashMap<String> keywords = new Object2ByteOpenHashMap(6);
    keywords.put("vec2", (byte) 2);
    keywords.put("vec3", (byte) 3);
    keywords.put("float", (byte) 1);
    keywords.put("float2", (byte) 2);
    keywords.put("float3", (byte) 3);
    keywords.put("int", (byte) 1);
    keywords.put("int2", (byte) 2);
    keywords.put("int3", (byte) 3);
    keywords.put("bool", (byte) 1);
    keywords.put("bool2", (byte) 2);
    keywords.put("bool3", (byte) 3);
    KEYWORDS = Object2ByteMaps.unmodifiable(keywords);
  }

  public record BoneData(VectorKeyFrame[] rotation, VectorKeyFrame[] position) {
    public static void toNetwork(FriendlyByteBuf byteBuf, BoneData data) {
      writeKeyFrames(byteBuf, data.rotation);
      writeKeyFrames(byteBuf, data.position);
    }

    public static BoneData fromNetwork(FriendlyByteBuf byteBuf) {
      VectorKeyFrame[] rotate = readKeyFrames(byteBuf, (x$0) -> new VectorKeyFrame[x$0], VectorKeyFrame.class);
      VectorKeyFrame[] position = readKeyFrames(byteBuf, (x$0) -> new VectorKeyFrame[x$0], VectorKeyFrame.class);
      return new BoneData(rotate, position);
    }

    public static void writeKeyFrames(FriendlyByteBuf byteBuf, KeyFrame<?>[] arr) {
      byteBuf.writeVarInt(arr.length);

      for (KeyFrame<?> keyFrame : arr) {
        keyFrame.toNetwork(byteBuf);
      }

    }

    public static <T extends KeyFrame<?>> T[] readKeyFrames(FriendlyByteBuf byteBuf, IntFunction<T[]> function, Class<T> clazz) {
      int i = byteBuf.readVarInt();
      T[] arr = function.apply(i);

      for (int i1 = 0; i1 < i; ++i1) {
        arr[i1] = Decoder.decode(byteBuf, clazz);
      }

      return arr;
    }
  }
}
