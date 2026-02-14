//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.api.event;

public abstract class SAEvent {
  protected boolean canceled = false;

  protected SAEvent() {
  }

  public void setCancel(boolean canceled) {
    if (this instanceof ICancelable) {
      this.canceled = canceled;
    }

  }

  public boolean isCanceled() {
    return this.canceled;
  }
}
