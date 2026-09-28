package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowSound;
import com.grahambartley.notenougharrows.arrow.ArrowSoundAudit;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class ModSoundsGameTest implements FabricGameTest {

  private static final Vec3d BEEP_POS = new Vec3d(0.5, 2.0, 0.5);

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theCountdownBeepResolvesInTheSoundEventRegistry(TestContext context) {
    context.assertTrue(
        Registries.SOUND_EVENT.containsId(ModSounds.COUNTDOWN_BEEP_ID),
        "Sound event "
            + ModSounds.COUNTDOWN_BEEP_ID
            + " should resolve in the sound event registry");
    context.assertTrue(
        Registries.SOUND_EVENT.get(ModSounds.COUNTDOWN_BEEP_ID) == ModSounds.COUNTDOWN_BEEP,
        "Sound event "
            + ModSounds.COUNTDOWN_BEEP_ID
            + " should resolve to the registered instance");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theCountdownBeepIsNamedAfterTheAssetItPlays(TestContext context) {
    context.assertTrue(
        ModSounds.COUNTDOWN_BEEP.getId().equals(ModSounds.COUNTDOWN_BEEP_ID),
        "The countdown beep should carry the identifier its sounds.json entry is keyed by");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theServerDispatchesRepeatedBeepsWithoutHoldingTheClientAsset(TestContext context) {
    final Vec3d pos = context.getAbsolute(BEEP_POS);

    for (int beep = 0; beep < 8; beep++) {
      ModSoundPlayer.play(
          context.getWorld(), pos, ModSounds.COUNTDOWN_BEEP, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void everyDeclaredSoundResolvesInTheSoundEventRegistry(TestContext context) {
    final List<Identifier> missing =
        ModSounds.declared().stream().filter(id -> !Registries.SOUND_EVENT.containsId(id)).toList();

    context.assertTrue(missing.isEmpty(), "Declared sounds never registered: " + missing);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void everyArrowThatDeclaresASoundHasItRegistered(TestContext context) {
    final List<Identifier> unvoiced =
        ArrowSoundAudit.unregistered(declaredArrowSounds(), Registries.SOUND_EVENT.getIds());

    context.assertTrue(
        unvoiced.isEmpty(), "Arrows declaring a sound that is not registered: " + unvoiced);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void noTwoUnrelatedArrowsShareASound(TestContext context) {
    final List<Identifier> shared = ArrowSoundAudit.sharedWithoutASystem(declaredArrowSounds());

    context.assertTrue(
        shared.isEmpty(), "Sounds shared by arrows outside one system (IDENT-9): " + shared);
    context.complete();
  }

  private static Map<Identifier, ArrowSound> declaredArrowSounds() {
    final Map<Identifier, ArrowSound> declared = new LinkedHashMap<>();
    for (final ArrowDefinition<?> definition : ModArrows.catalog().definitions()) {
      definition.impactSound().ifPresent(sound -> declared.put(definition.id(), sound));
    }
    return declared;
  }
}
