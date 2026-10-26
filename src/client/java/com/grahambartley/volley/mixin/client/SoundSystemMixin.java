package com.grahambartley.volley.mixin.client;

import com.grahambartley.volley.client.state.ClientStateService;
import com.grahambartley.volley.config.ClientConfigHolder;
import com.grahambartley.volley.sound.CountdownMute;
import com.grahambartley.volley.sound.ModSoundVolume;
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
  private void volley$muteCountdownBeep(final SoundInstance sound, final CallbackInfo info) {
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
  private float volley$capModSound(
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
  private float volley$scaleModSoundOnPlay(
      final float adjusted, @Local(argsOnly = true) final SoundInstance sound) {
    return volley$scale(sound, adjusted);
  }

  @ModifyReturnValue(
      method = "getAdjustedVolume(Lnet/minecraft/client/sound/SoundInstance;)F",
      at = @At("RETURN"))
  private float volley$scaleModSoundOnTick(final float adjusted, final SoundInstance sound) {
    return volley$scale(sound, adjusted);
  }

  private static float volley$scale(final SoundInstance sound, final float adjusted) {
    return ModSoundVolume.adjust(
        sound.getId(),
        adjusted,
        ClientConfigHolder.get().sound().volume(),
        ClientStateService.get().modSoundVolume());
  }
}
