//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.animation.keyframe;

import ctn.simpleanimator.api.animation.LerpMode;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3f;

public class VectorKeyFrame extends KeyFrame<Vector3f> {
  public VectorKeyFrame(FriendlyByteBuf byteBuf) {
    super(byteBuf, VectorKeyFrame::readValue);
  }

  private static Vector3f readValue(FriendlyByteBuf byteBuf) {
    return byteBuf.readVector3f();
  }

  public VectorKeyFrame(float time, Vector3f pre, Vector3f post, LerpMode mode) {
    super(time, pre, post, mode);
  }

  public void toNetwork(FriendlyByteBuf byteBuf) {
    super.toNetwork(byteBuf);
    byteBuf.writeVector3f(this.pre);
    byteBuf.writeVector3f(this.post);
  }

  public Vector3f linerInterpolation(Vector3f p1, Vector3f p2, float delta) {
    return Interpolation.linerInterpolation(p1, p2, delta);
  }

  public Vector3f catmullRomInterpolation(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float delta) {
    return Interpolation.catmullRomInterpolation(p0, p1, p2, p3, delta);
  }
}
