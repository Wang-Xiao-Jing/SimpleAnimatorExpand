//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.event;

import com.mojang.logging.LogUtils;
import ctn.simpleanimator.api.event.ISAEventBus;
import ctn.simpleanimator.api.event.SAEvent;
import org.slf4j.Logger;

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.function.Consumer;

public class SAEventBusImpl implements ISAEventBus {
  private final Logger logger = LogUtils.getLogger();
  private final IdentityHashMap<Class<?>, ListenerList> map = new IdentityHashMap<>();

  public <T extends SAEvent> void addListener(Class<T> clazz, Consumer<T> listener) {
    if (Modifier.isAbstract(clazz.getModifiers())) {
      throw new IllegalArgumentException(clazz + " is an abstract class, you cannot add listener for it!");
    } else {
      this.map.computeIfAbsent(clazz, ListenerList::new).addListener(listener);
    }
  }

  public <T extends SAEvent> T post(T event) {
    ListenerList list = this.map.get(event.getClass());
    if (list != null) {
      try {
        for (Consumer<SAEvent> listener : list.getListeners()) {
          listener.accept(event);
        }
      } catch (Exception e) {
        this.logger.warn("Simple Animator Event Bus Error", e);
      }

    }
    return event;
  }
}
