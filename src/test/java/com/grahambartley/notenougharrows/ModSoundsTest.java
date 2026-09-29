package com.grahambartley.notenougharrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ModSoundsTest {
  private static final String VORBIS_MAGIC = "\u0001vorbis";
  private static final int OGG_PAGE_HEADER = 28;
  private static final int VORBIS_CHANNELS_OFFSET = OGG_PAGE_HEADER + VORBIS_MAGIC.length() + 4;
  private static final Set<String> FLAGGED_VANILLA =
      Set.of(
          "minecraft:block.soul_sand.break",
          "minecraft:entity.player.levelup",
          "minecraft:block.glass.break",
          "minecraft:block.grindstone.use",
          "minecraft:entity.warden.sonic_charge",
          "minecraft:entity.panda.sneeze",
          "minecraft:block.sculk_catalyst.bloom",
          "minecraft:item.lodestone_compass.lock");
  private static final String SOUNDS_DIR =
      "src/main/resources/assets/" + NotEnoughArrows.MOD_ID + "/sounds";

  private static JsonObject soundsJson() throws IOException {
    try (InputStream in =
        ModSoundsTest.class.getResourceAsStream(
            "/assets/" + NotEnoughArrows.MOD_ID + "/sounds.json")) {
      return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
          .getAsJsonObject();
    }
  }

  @Test
  void theCountdownBeepKeepsTheIdentifierItsAssetIsKeyedBy() {
    assertEquals(
        Identifier.of(NotEnoughArrows.MOD_ID, "countdown_beep"), ModSounds.COUNTDOWN_BEEP_ID);
    assertEquals(ModSounds.COUNTDOWN_BEEP_ID, ModSounds.COUNTDOWN_BEEP.getId());
  }

  @Test
  void everyDeclaredSoundHasAnEntryInSoundsJson() throws IOException {
    final Set<String> entries = soundsJson().keySet();

    final List<Identifier> missing =
        ModSounds.declared().stream().filter(id -> !entries.contains(id.getPath())).toList();

    assertTrue(missing.isEmpty(), "Declared sounds with no sounds.json entry: " + missing);
  }

  @Test
  void everySoundsJsonEntryIsDeclared() throws IOException {
    final Set<String> declared =
        ModSounds.declared().stream().map(Identifier::getPath).collect(Collectors.toSet());

    final List<String> undeclared =
        soundsJson().keySet().stream().filter(path -> !declared.contains(path)).toList();

    assertTrue(undeclared.isEmpty(), "sounds.json entries no code can play: " + undeclared);
  }

  @Test
  void everySoundOutsideTheInterfaceCarriesASubtitle() throws IOException {
    final JsonObject json = soundsJson();

    final List<String> silentToSubtitles =
        json.keySet().stream()
            .filter(path -> !json.getAsJsonObject(path).has("subtitle"))
            .filter(path -> !playsAnInterfaceSound(json.getAsJsonObject(path)))
            .toList();

    assertTrue(silentToSubtitles.isEmpty(), "Sounds with no subtitle: " + silentToSubtitles);
  }

  private static boolean playsAnInterfaceSound(final JsonObject entry) {
    return entry.getAsJsonArray("sounds").asList().stream()
        .allMatch(
            sound ->
                sound.isJsonObject()
                    && sound
                        .getAsJsonObject()
                        .get("name")
                        .getAsString()
                        .startsWith("minecraft:ui."));
  }

  @Test
  void everyModAssetASoundPlaysIsShipped() throws IOException {
    final Set<String> shipped = shippedAssets();

    final List<String> missing =
        modAssetsPlayed().stream().filter(asset -> !shipped.contains(asset)).toList();

    assertTrue(missing.isEmpty(), "sounds.json plays assets that are not shipped: " + missing);
  }

  @Test
  void everyShippedAssetIsPlayedBySomeSound() throws IOException {
    final List<String> played = modAssetsPlayed();

    final List<String> orphans =
        shippedAssets().stream().filter(asset -> !played.contains(asset)).sorted().toList();

    assertTrue(orphans.isEmpty(), "Shipped assets no sound plays: " + orphans);
  }

  @Test
  void noTwoSoundsPlayTheSameAsset() throws IOException {
    final List<String> played = modAssetsPlayed();

    final List<String> shared =
        played.stream()
            .filter(asset -> played.indexOf(asset) != played.lastIndexOf(asset))
            .distinct()
            .toList();

    assertTrue(shared.isEmpty(), "Assets played by more than one sound (IDENT-9): " + shared);
  }

  @Test
  void everyShippedAssetIsMonoVorbisSoItCanBePlacedInTheWorld() throws IOException {
    final List<String> notMono = new ArrayList<>();
    for (final String asset : shippedAssets()) {
      final byte[] header;
      try (InputStream in = assetStream(asset)) {
        header = in.readNBytes(VORBIS_CHANNELS_OFFSET + 1);
      }
      final boolean vorbis =
          new String(header, OGG_PAGE_HEADER, VORBIS_MAGIC.length(), StandardCharsets.US_ASCII)
              .equals(VORBIS_MAGIC);
      if (!vorbis || header[VORBIS_CHANNELS_OFFSET] != 1) {
        notMono.add(asset);
      }
    }

    assertTrue(notMono.isEmpty(), "Assets that are not mono Ogg Vorbis: " + notMono);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "repel_arrow_impact",
        "allegiance_arrow_impact",
        "frost_arrow_freeze_crack",
        "drill_arrow_bore",
        "sonar_arrow_pulse",
        "stink_arrow_release",
        "polymorph_arrow_restore",
        "magnet_arrow_pull"
      })
  void noSoundTheIdent8ReviewFlaggedStillBorrowsItsOldVanillaSound(final String path)
      throws IOException {
    final List<String> plays = played(soundsJson().getAsJsonObject(path));

    assertTrue(
        plays.stream().noneMatch(FLAGGED_VANILLA::contains),
        path + " still plays a vanilla sound the review said means something else: " + plays);
  }

  private static List<String> played(final JsonObject entry) {
    return entry.getAsJsonArray("sounds").asList().stream()
        .map(
            sound ->
                sound.isJsonObject()
                    ? sound.getAsJsonObject().get("name").getAsString()
                    : sound.getAsString())
        .toList();
  }

  private static List<String> modAssetsPlayed() throws IOException {
    final JsonObject json = soundsJson();
    final String prefix = NotEnoughArrows.MOD_ID + ":";
    final List<String> assets = new ArrayList<>();
    for (final String path : json.keySet()) {
      final JsonObject entry = json.getAsJsonObject(path);
      for (final var sound : entry.getAsJsonArray("sounds").asList()) {
        final boolean isEvent =
            sound.isJsonObject()
                && sound.getAsJsonObject().has("type")
                && sound.getAsJsonObject().get("type").getAsString().equals("event");
        final String name =
            sound.isJsonObject()
                ? sound.getAsJsonObject().get("name").getAsString()
                : sound.getAsString();
        if (!isEvent && name.startsWith(prefix)) {
          assets.add(name.substring(prefix.length()));
        }
      }
    }
    return assets;
  }

  private static Set<String> shippedAssets() throws IOException {
    try (Stream<Path> files = Files.list(Path.of(SOUNDS_DIR))) {
      return files
          .map(file -> file.getFileName().toString())
          .filter(name -> name.endsWith(".ogg"))
          .map(name -> name.substring(0, name.length() - ".ogg".length()))
          .collect(Collectors.toSet());
    }
  }

  private static InputStream assetStream(final String asset) throws IOException {
    return Files.newInputStream(Path.of(SOUNDS_DIR, asset + ".ogg"));
  }
}
