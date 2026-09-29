package com.grahambartley.notenougharrows.mixin.client;

import com.grahambartley.notenougharrows.client.state.ClientStateService;
import com.grahambartley.notenougharrows.config.ClientConfigHolder;
import com.grahambartley.notenougharrows.sound.CountdownMute;
import com.grahambartley.notenougharrows.sound.ModSoundVolume;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
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

  @ModifyExpressionValue(
      method = {
        "play(Lnet/minecraft/client/sound/SoundInstance;)V",
        "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F"
      },
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundInstance;getVolume()F"))
  private float notEnoughArrows$capModSound(
      final float volume, @Local(argsOnly = true) final SoundInstance sound) {
    return ModSoundVolume.cap(sound.getId(), volume);
  }

  @ModifyExpressionValue(
      method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F"))
  private float notEnoughArrows$scaleModSoundOnPlay(
      final float adjusted, @Local(argsOnly = true) final SoundInstance sound) {
    return notEnoughArrows$scale(sound, adjusted);
  }

  @ModifyReturnValue(
      method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F",
      at = @At("RETURN"))
  private float notEnoughArrows$scaleModSoundOnTick(
      final float adjusted, final SoundInstance sound) {
    return notEnoughArrows$scale(sound, adjusted);
  }

  private static float notEnoughArrows$scale(final SoundInstance sound, final float adjusted) {
    return ModSoundVolume.adjust(
        sound.getId(),
        adjusted,
        ClientConfigHolder.get().sound().volume(),
        ClientStateService.get().modSoundVolume());
  }
}
