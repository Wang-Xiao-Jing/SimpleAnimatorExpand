//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.config;

import com.google.common.io.Files;
import ctn.simpleanimator.core.SimpleAnimator;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class CommonConfiguration {
  private static final File FILE = new File("config", "simpleanimator-common.json");
  public final int interactInviteDistanceSquare = 1024;

  CommonConfiguration() {
  }

  public static CommonConfiguration load() {
    if (FILE.exists()) {
      try (Reader reader = Files.newReader(FILE, StandardCharsets.UTF_8)) {
        return SimpleAnimator.GSON.fromJson(reader, CommonConfiguration.class);
      } catch (IOException var5) {
        SimpleAnimator.LOGGER.warn("Error occurred during loading common configuration!");
      }
    }

    CommonConfiguration configuration = new CommonConfiguration();
    configuration.write();
    return configuration;
  }

  public void write() {
    FILE.deleteOnExit();

    try (Writer writer = Files.newWriter(FILE, StandardCharsets.UTF_8)) {
      writer.write(SimpleAnimator.GSON.toJson(this));
    } catch (IOException var6) {
      SimpleAnimator.LOGGER.warn("Error occurred during writing common configuration!");
    }

  }
}
