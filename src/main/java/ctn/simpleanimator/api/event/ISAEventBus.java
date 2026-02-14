//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event;

import java.util.function.Consumer;

public interface ISAEventBus {
  <T extends SAEvent> void addListener(Class<T> var1, Consumer<T> var2);

  <T extends SAEvent> T post(T var1);
}
