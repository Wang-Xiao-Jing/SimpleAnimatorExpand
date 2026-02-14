//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.network;

public interface ISync extends IPacket {
  default void handle(NetworkContext context) {
    this.sync();
  }

  void sync();
}
