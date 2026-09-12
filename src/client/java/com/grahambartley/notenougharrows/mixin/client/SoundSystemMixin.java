package com.grahambartley.notenougharrows.mixin.client;

import com.grahambartley.notenougharrows.client.state.ClientStateService;
import com.grahambartley.notenougharrows.sound.CountdownMute;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {

  @Inject(
      method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
      at = @At("HEAD"),
      cancellable = true)
  private void notEnoughArrows$muteCountdownBeep(
      final SoundInstance sound, final CallbackInfo info) {
    if (CountdownMute.silences(sound.getId(), ClientStateService.get().playCountdownSound())) {
      info.cancel();
    }
  }
}
