//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ctn.simpleanimator.core.animation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import ctn.simpleanimator.api.animation.Animation;
import ctn.simpleanimator.api.animation.Animation.Type;
import ctn.simpleanimator.api.animation.Interaction;
import ctn.simpleanimator.core.SimpleAnimator;
import ctn.simpleanimator.core.network.packet.batch.ClientUpdateAnimationPacket;
import ctn.simpleanimator.core.network.packet.batch.ClientUpdateInteractionPacket;
import ctn.simpleanimator.core.network.packet.batch.PacketCache;
import net.minecraft.Util;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class AnimationManager implements PreparableReloadListener {
  public static final FileToIdConverter ANIMATION_LISTER = FileToIdConverter.json("animations");
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final Path EXTERMAL_PATH = Path.of("animations");
  private ImmutableMap<ResourceLocation, Animation> animations;
  private ImmutableMap<ResourceLocation, Interaction> interactions;
  private final PacketCache cacheAnimations = new PacketCache();
  private final PacketCache cacheInteractions = new PacketCache();

  public @Nullable Animation getAnimation(ResourceLocation location) {
    return this.animations.get(location);
  }

  public @Nullable Interaction getInteraction(ResourceLocation location) {
    return this.interactions.get(location);
  }

  public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
    CompletableFuture<List<Pair<ResourceLocation, Animation[]>>> animations = this.load(pResourceManager, pBackgroundExecutor);
    CompletableFuture<Void> var10000 = CompletableFuture.allOf(animations);
    Objects.requireNonNull(pPreparationBarrier);
    return var10000.thenCompose(pPreparationBarrier::wait).thenAcceptAsync((v) -> {
      ImmutableMap.Builder<ResourceLocation, Animation> animationBuilder = ImmutableMap.builder();
      ImmutableMap.Builder<ResourceLocation, Interaction> interactionBuilder = ImmutableMap.builder();
      List<Pair<ResourceLocation, Animation[]>> extern = this.loadExtern();
      List<Pair<ResourceLocation, Animation[]>> join = animations.join();
      this.collect(extern, animationBuilder, interactionBuilder);
      this.collect(join, animationBuilder, interactionBuilder);
      this.animations = animationBuilder.build();
      this.interactions = interactionBuilder.build();
      this.cacheAnimations.reset(new ClientUpdateAnimationPacket(this.animations));
      this.cacheInteractions.reset(new ClientUpdateInteractionPacket(this.interactions));
    });
  }

  private void collect(List<Pair<ResourceLocation, Animation[]>> list, ImmutableMap.Builder<ResourceLocation, Animation> animations, ImmutableMap.Builder<ResourceLocation, Interaction> interactions) {
    list.stream().flatMap((pair) -> {
      if (pair.getSecond().length > 1) {
        LOGGER.debug("Load Interaction: {}", pair.getFirst());
        interactions.put(pair.getFirst(), new Interaction(pair.getFirst().withPrefix(Type.INVITE.path), pair.getFirst().withPrefix(Type.REQUESTER.path), pair.getFirst().withPrefix(Type.RECEIVER.path)));
      }

      return Arrays.stream(pair.getSecond()).map((animation) -> new Pair<>(pair.getFirst().withPrefix(animation.getType().path), animation));
    }).forEach((pair) -> animations.put(pair.getFirst(), pair.getSecond()));
  }

  private CompletableFuture<List<Pair<ResourceLocation, Animation[]>>> load(ResourceManager pResourceManager, Executor pBackgroundExecutor) {
    return CompletableFuture.supplyAsync(() -> ANIMATION_LISTER.listMatchingResourceStacks(pResourceManager), pBackgroundExecutor).thenCompose((map) -> {
      List<CompletableFuture<Pair<ResourceLocation, Animation[]>>> list = new ArrayList<>(map.size());

      for (Map.Entry<ResourceLocation, List<Resource>> entry : map.entrySet()) {
        ResourceLocation location = entry.getKey();
        ResourceLocation resourceLocation = ANIMATION_LISTER.fileToId(location);

        for (Resource resource : entry.getValue()) {
          list.add(CompletableFuture.supplyAsync(() -> {
            try (Reader reader = resource.openAsReader()) {
              Animation[] animations = Animation.fromStream(reader);
              return Pair.of(resourceLocation, animations);
            } catch (IOException var8) {
              LOGGER.warn("Couldn't read animation {} from {} in data pack {}", resourceLocation, location, resource.sourcePackId());
              return null;
            }
          }));
        }
      }

      return Util.sequence(list).thenApply((result) -> result.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    });
  }

  public Set<ResourceLocation> getAnimationNames() {
    return this.animations.keySet();
  }

  public Set<ResourceLocation> getInteractionNames() {
    return this.interactions.keySet();
  }

  public List<Pair<ResourceLocation, Animation[]>> loadExtern() {
    final List<Pair<ResourceLocation, Animation[]>> animations = new ArrayList<>();
    if (!Files.exists(EXTERMAL_PATH)) {
      try {
        Files.createDirectories(EXTERMAL_PATH);
      } catch (IOException e) {
        LOGGER.warn("", e);
      }

      LOGGER.info("Nonexistent Animation Path!");
    }

    try {
      Files.walkFileTree(EXTERMAL_PATH, new SimpleFileVisitor<>() {
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          if (Files.isRegularFile(file) && file.toString().endsWith(".json")) {
            AnimationManager.load(file, animations);
          }

          return FileVisitResult.CONTINUE;
        }

        public FileVisitResult visitFileFailed(Path file, IOException exc) {
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      LOGGER.warn("Failed to load animations: {}", e.getMessage());
    }

    return animations;
  }

  public void handleUpdateAnimations(ClientUpdateAnimationPacket packet) {
    SimpleAnimator.getProxy().getAnimatorManager().reset();
    LOGGER.debug("Sync Animations From Server");
    Map<ResourceLocation, Animation> animations = packet.getAnimations();
    this.animations = ImmutableMap.copyOf(animations);
  }

  public void handleUpdateInteractions(ClientUpdateInteractionPacket packet) {
    LOGGER.debug("Sync Interactions From Server");
    Map<ResourceLocation, Interaction> interactions = packet.getInteractions();
    this.interactions = ImmutableMap.copyOf(interactions);
  }

  private static void load(Path path, List<Pair<ResourceLocation, Animation[]>> list) {
    try (Reader reader = Files.newBufferedReader(path)) {
      JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
      String name = path.getFileName().toString();
      name = name.substring(0, name.length() - 5);
      Animation[] animations = Animation.serialize(object);
      LOGGER.debug("Load External Animation: {}", name);
      list.add(Pair.of(ResourceLocation.fromNamespaceAndPath("external", name), animations));
    } catch (IOException | RuntimeException e) {
      LOGGER.warn("Failed to read resource {}", path, e);
    }

  }

  public void sync(ServerPlayer player) {
    LOGGER.info("Send Animations[{}] and Interactions[{}] to Client", this.animations.size(), this.interactions.size());
    if (this.cacheAnimations.ready()) {
      SimpleAnimator.getNetwork().sendToPlayer(this.cacheAnimations, player);
    }

    if (this.cacheInteractions.ready()) {
      SimpleAnimator.getNetwork().sendToPlayer(this.cacheInteractions, player);
    }

  }

  public void sync(PlayerList list) {
    LOGGER.info("Send Animations[{}] and Interactions[{}] to All Players", this.animations.size(), this.interactions.size());

    for (ServerPlayer player : list.getPlayers()) {
      if (this.cacheAnimations.ready()) {
        SimpleAnimator.getNetwork().sendToPlayer(this.cacheAnimations, player);
      }

      if (this.cacheInteractions.ready()) {
        SimpleAnimator.getNetwork().sendToPlayer(this.cacheInteractions, player);
      }
    }

  }
}
