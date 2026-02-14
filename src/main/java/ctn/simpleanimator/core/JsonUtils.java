//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
  public static String getString(String key, JsonObject jsonObject, String str) {
    JsonElement element = jsonObject.get(key);
    return element != null ? element.getAsString() : str;
  }

  public static boolean getBoolean(String key, JsonObject jsonObject, boolean bl) {
    JsonElement element = jsonObject.get(key);
    return element != null ? element.getAsBoolean() : bl;
  }

  public static int getInt(String key, JsonObject jsonObject, int i) {
    JsonElement element = jsonObject.get(key);
    return element != null ? element.getAsInt() : i;
  }
}
