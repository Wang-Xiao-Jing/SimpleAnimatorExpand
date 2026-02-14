//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.event;

import ctn.simpleanimator.api.event.SAEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.function.Consumer;

public class ListenerList {
  private final List<Consumer<SAEvent>> listeners = new ObjectArrayList<>();

  public ListenerList() {
  }

  public ListenerList(Class<?> clazz) {
  }

  public List<Consumer<SAEvent>> getListeners() {
    return this.listeners;
  }

  public <T extends SAEvent> void addListener(Consumer<T> listener) {
    this.listeners.add((Consumer<SAEvent>) listener);
  }
}
