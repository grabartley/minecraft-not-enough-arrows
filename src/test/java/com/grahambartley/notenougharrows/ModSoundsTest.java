package com.grahambartley.notenougharrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ModSoundsTest {

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
}
