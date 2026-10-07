package com.grahambartley.notenougharrows.mixin.client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import net.minecraft.sound.SoundEvent;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.injection.ModifyArg;

class LightningEntityMixinTest {
  private static final String PLAY_SOUND =
      "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V";
  private static final int SOUND_ARGUMENT = 3;

  @Test
  void rewritesTheSoundOfEveryPlaySoundCallInTick() {
    final ModifyArg swap = onlySoundSwap();

    assertArrayEquals(new String[] {"tick()V"}, swap.method());
    assertEquals("INVOKE", swap.at().value());
    assertEquals(PLAY_SOUND, swap.at().target());
    assertEquals(SOUND_ARGUMENT, swap.index());
  }

  @Test
  void theSwapTakesAndReturnsASoundEvent() {
    final Method swap = soundSwaps().get(0);

    assertArrayEquals(new Class<?>[] {SoundEvent.class}, swap.getParameterTypes());
    assertEquals(SoundEvent.class, swap.getReturnType());
  }

  private static ModifyArg onlySoundSwap() {
    final List<Method> swaps = soundSwaps();
    assertEquals(1, swaps.size(), "LightningEntityMixin should declare one sound swap");
    return swaps.get(0).getAnnotation(ModifyArg.class);
  }

  private static List<Method> soundSwaps() {
    return Arrays.stream(LightningEntityMixin.class.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(ModifyArg.class))
        .toList();
  }
}
