//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.animation.keyframe;

import ctn.simpleanimator.api.animation.LerpMode;
import net.minecraft.network.FriendlyByteBuf;

import java.util.IdentityHashMap;
import java.util.function.Function;

public abstract class KeyFrame<T> {
  protected final float time;
  protected final LerpMode mode;
  protected final T pre;
  protected T post;

  public KeyFrame(FriendlyByteBuf byteBuf, Function<FriendlyByteBuf, T> decoder) {
    this.time = byteBuf.readFloat();
    this.mode = byteBuf.readEnum(LerpMode.class);
    this.pre = decoder.apply(byteBuf);
    this.post = decoder.apply(byteBuf);
  }

  public KeyFrame(float time, T pre, T post, LerpMode mode) {
    this.time = time;
    this.pre = pre;
    this.post = post;
    this.mode = mode;
  }

  public float getTime() {
    return this.time;
  }

  public LerpMode getMode() {
    return this.mode;
  }

  public T getPre() {
    return this.pre;
  }

  public T getPost() {
    return this.post;
  }

  public void toNetwork(FriendlyByteBuf byteBuf) {
    byteBuf.writeFloat(this.time);
    byteBuf.writeEnum(this.mode);
  }

  public abstract T linerInterpolation(T var1, T var2, float var3);

  public abstract T catmullRomInterpolation(T var1, T var2, T var3, T var4, float var5);

  public static class Decoder {
    private static final IdentityHashMap<Class<? extends KeyFrame<?>>, Function<FriendlyByteBuf, KeyFrame<?>>> DECODERS = new IdentityHashMap(3);

    public static <T extends KeyFrame<?>> T decode(FriendlyByteBuf byteBuf, Class<T> clazz) {
      Function<FriendlyByteBuf, KeyFrame<?>> decoder = DECODERS.get(clazz);
      if (decoder == null) {
        throw new IllegalArgumentException(clazz + "is not a legal type!");
      } else {
        return (T) (decoder.apply(byteBuf));
      }
    }

    static {
      DECODERS.put(VectorKeyFrame.class, VectorKeyFrame::new);
      DECODERS.put(QuaternionKeyFrame.class, QuaternionKeyFrame::new);
      DECODERS.put(VariableKeyFrame.class, VariableKeyFrame::new);
    }
  }
}
