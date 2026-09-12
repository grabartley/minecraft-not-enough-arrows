package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModSounds;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
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
      context
          .getWorld()
          .playSound(
              null,
              pos.getX(),
              pos.getY(),
              pos.getZ(),
              ModSounds.COUNTDOWN_BEEP,
              SoundCategory.NEUTRAL,
              1.0f,
              1.0f);
    }
    context.complete();
  }
}
