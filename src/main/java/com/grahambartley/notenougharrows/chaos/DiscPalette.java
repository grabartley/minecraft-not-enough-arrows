package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.tint.TintChoice;
import com.grahambartley.notenougharrows.tint.TintPalette;
import java.util.List;
import net.minecraft.util.Identifier;

public final class DiscPalette {
  public static final String FALLBACK_KEY = "cat";
  public static final String LABEL_PREFIX = "tint." + NotEnoughArrows.MOD_ID + ".party.";
  public static final String DISC_PREFIX = "music_disc_";

  private static final List<TintChoice> CHOICES =
      List.of(
          choice("13", 0xE8E142),
          choice("cat", 0x6BD03A),
          choice("blocks", 0xE3632B),
          choice("chirp", 0xD8332F),
          choice("far", 0x9ACD4F),
          choice("mall", 0x7C59C9),
          choice("mellohi", 0xC87BD8),
          choice("stal", 0x3A3A3A),
          choice("strad", 0xF1F1F1),
          choice("ward", 0x2E8B57),
          choice("11", 0x5A4A3A),
          choice("wait", 0x3F86D1),
          choice("otherside", 0x4DC7C7),
          choice("5", 0x6A7DA0),
          choice("pigstep", 0xB0453A),
          choice("relic", 0x2FA3A3),
          choice("creator", 0xF2B63A),
          choice("creator_music_box", 0xE9D39A),
          choice("precipice", 0xA35A2A));

  private DiscPalette() {}

  public static List<String> songs() {
    return CHOICES.stream().map(TintChoice::key).toList();
  }

  public static TintPalette create() {
    return new TintPalette(CHOICES, FALLBACK_KEY);
  }

  private static TintChoice choice(final String song, final int color) {
    return new TintChoice(
        song, Identifier.ofVanilla(DISC_PREFIX + song), color, LABEL_PREFIX + song);
  }
}
