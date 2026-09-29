package com.grahambartley.notenougharrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.ExplosiveArrowConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

class ModSoundsTest {
  private static final String VORBIS_MAGIC = "\u0001vorbis";
  private static final int OGG_PAGE_HEADER = 28;
  private static final int VORBIS_CHANNELS_OFFSET = OGG_PAGE_HEADER + VORBIS_MAGIC.length() + 4;
  private static final Map<String, String> BORROWED_BEFORE_REVIEW =
      Map.of(
          "repel_arrow_impact", "minecraft:block.soul_sand.break",
          "allegiance_arrow_impact", "minecraft:entity.player.levelup",
          "frost_arrow_freeze_crack", "minecraft:block.glass.break",
          "drill_arrow_bore", "minecraft:block.grindstone.use",
          "sonar_arrow_pulse", "minecraft:entity.warden.sonic_charge",
          "stink_arrow_release", "minecraft:entity.panda.sneeze",
          "polymorph_arrow_restore", "minecraft:block.sculk_catalyst.bloom",
          "magnet_arrow_pull", "minecraft:item.lodestone_compass.lock");
  private static final String COUNTDOWN_BEEP = "countdown_beep";
  private static final String WATCHER_CHIRP = "tripwire_arrow_alert";
  private static final int LOUDNESS_WINDOW = 2205;
  private static final double CODEC_TOLERANCE = 1.06;
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

  @Test
  void noSoundTheIdent8ReviewFlaggedStillBorrowsItsOldVanillaSound() throws IOException {
    final JsonObject json = soundsJson();

    final List<String> stillBorrowing =
        BORROWED_BEFORE_REVIEW.entrySet().stream()
            .filter(
                flagged ->
                    played(json.getAsJsonObject(flagged.getKey())).contains(flagged.getValue()))
            .map(Map.Entry::getKey)
            .sorted()
            .toList();

    assertTrue(
        stillBorrowing.isEmpty(),
        "Sounds still playing the vanilla sound the review said means something else: "
            + stillBorrowing);
  }

  @Test
  void theCountdownBeepReachesAsFarAsALandingSound() {
    assertEquals(
        ModSoundPlayer.LANDING_RANGE_BLOCKS,
        ModSounds.COUNTDOWN_BEEP.getDistanceToTravel(1.0f),
        "server range at ordinary volume");
    assertEquals(
        ModSounds.COUNTDOWN_BEEP.getDistanceToTravel(ExplosiveArrowConfig.BEEP_VOLUME_MIN),
        ModSounds.COUNTDOWN_BEEP.getDistanceToTravel(ExplosiveArrowConfig.BEEP_VOLUME_MAX),
        "the beep volume setting must change loudness, never reach");
  }

  @Test
  void theCountdownBeepFadesOverTheSameDistanceAsALandingSound() throws IOException {
    final JsonObject beep =
        soundsJson()
            .getAsJsonObject("countdown_beep")
            .getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();

    assertEquals(
        ModSoundPlayer.LANDING_RANGE_BLOCKS,
        beep.get("attenuation_distance").getAsInt(),
        "the client fades the beep over its attenuation distance");
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

  @Test
  void noAssetIsLouderThanTheCountdownBeep() throws IOException {
    final Loudness beep = loudnessOf(COUNTDOWN_BEEP);

    final List<String> louder =
        shippedAssets().stream()
            .filter(asset -> !asset.equals(COUNTDOWN_BEEP))
            .filter(asset -> loudnessOf(asset).exceeds(beep))
            .sorted()
            .toList();

    assertTrue(louder.isEmpty(), "Assets louder than the countdown beep (A11Y-3): " + louder);
  }

  @Test
  void theWatcherChirpIsTheQuietestArrowSound() throws IOException {
    final double chirp = loudnessOf(WATCHER_CHIRP).rmsPeak();

    final List<String> quieter =
        shippedAssets().stream()
            .filter(asset -> !asset.equals(WATCHER_CHIRP) && !asset.equals(COUNTDOWN_BEEP))
            .filter(asset -> loudnessOf(asset).rmsPeak() <= chirp)
            .sorted()
            .toList();

    assertTrue(quieter.isEmpty(), "Assets as quiet as the watcher chirp or quieter: " + quieter);
  }

  private record Loudness(double rmsPeak, double samplePeak) {
    boolean exceeds(final Loudness other) {
      return rmsPeak > other.rmsPeak * CODEC_TOLERANCE
          || samplePeak > other.samplePeak * CODEC_TOLERANCE;
    }
  }

  private static Loudness loudnessOf(final String asset) {
    final short[] pcm = decode(asset);
    final int window = pcm.length < LOUDNESS_WINDOW ? pcm.length : LOUDNESS_WINDOW;
    double rmsPeak = 0.0;
    double samplePeak = 0.0;
    for (int start = 0; start + window <= pcm.length; start += window / 2) {
      double sum = 0.0;
      for (int i = start; i < start + window; i++) {
        final double sample = pcm[i] / (double) Short.MAX_VALUE;
        sum += sample * sample;
        samplePeak = Math.max(samplePeak, Math.abs(sample));
      }
      rmsPeak = Math.max(rmsPeak, Math.sqrt(sum / window));
    }
    return new Loudness(rmsPeak, samplePeak);
  }

  private static short[] decode(final String asset) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      final byte[] bytes = Files.readAllBytes(Path.of(SOUNDS_DIR, asset + ".ogg"));
      final ByteBuffer ogg = BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();
      final long vorbis = STBVorbis.stb_vorbis_open_memory(ogg, stack.mallocInt(1), null);
      if (vorbis == 0L) {
        throw new IllegalStateException(asset + " did not decode as Ogg Vorbis");
      }
      try {
        final ShortBuffer pcm =
            BufferUtils.createShortBuffer(STBVorbis.stb_vorbis_stream_length_in_samples(vorbis));
        final int read = STBVorbis.stb_vorbis_get_samples_short_interleaved(vorbis, 1, pcm);
        final short[] samples = new short[read];
        pcm.get(samples);
        return samples;
      } finally {
        STBVorbis.stb_vorbis_close(vorbis);
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
