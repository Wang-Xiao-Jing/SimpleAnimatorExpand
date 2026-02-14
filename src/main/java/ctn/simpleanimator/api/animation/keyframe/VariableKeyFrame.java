//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.animation.keyframe;

import ctn.simpleanimator.api.animation.AnimationSection.BoneData;
import ctn.simpleanimator.api.animation.LerpMode;
import net.minecraft.network.FriendlyByteBuf;

public class VariableKeyFrame extends KeyFrame<VariableHolder> {
  public VariableKeyFrame(FriendlyByteBuf byteBuf) {
    super(byteBuf, VariableHolder::decode);
  }

  public VariableKeyFrame(float time, VariableHolder pre, VariableHolder post, LerpMode mode) {
    super(time, pre, post, mode);
  }

  public void toNetwork(FriendlyByteBuf byteBuf) {
    super.toNetwork(byteBuf);
    this.pre.toNetwork(byteBuf);
    this.post.toNetwork(byteBuf);
  }

  public VariableHolder linerInterpolation(VariableHolder p1, VariableHolder p2, float delta) {
    return p1.linerInterpolation(p1, p2, delta);
  }

  public VariableHolder catmullRomInterpolation(VariableHolder p0, VariableHolder p1, VariableHolder p2, VariableHolder p3, float delta) {
    return p0.catmullRomInterpolation(p0, p1, p2, p3, delta);
  }

  public int variableSize() {
    return this.post.size();
  }

  public record Group(VariableKeyFrame[] keyFrames, int variableSize) {
    public static void toNetwork(FriendlyByteBuf byteBuf, Group group) {
      BoneData.writeKeyFrames(byteBuf, group.keyFrames);
      byteBuf.writeVarInt(group.variableSize);
    }

    public static Group fromNetwork(FriendlyByteBuf byteBuf) {
      VariableKeyFrame[] frames = BoneData.readKeyFrames(byteBuf, (x$0) -> new VariableKeyFrame[x$0], VariableKeyFrame.class);
      int variableSize = byteBuf.readVarInt();
      return new Group(frames, variableSize);
    }
  }
}
