package com.grahambartley.volley;

import com.grahambartley.volley.arrow.ArrowDefinition;
import com.grahambartley.volley.arrow.ArrowEffect;
import com.grahambartley.volley.arrow.ArrowSound;
import com.grahambartley.volley.arrow.RegisteredArrow;
import com.grahambartley.volley.entity.BeaconArrowEntity;
import com.grahambartley.volley.entity.ProspectorArrowEntity;
import com.grahambartley.volley.entity.SonarArrowEntity;
import com.grahambartley.volley.entity.TorchArrowEntity;
import com.grahambartley.volley.entity.TracerArrowEntity;
import com.grahambartley.volley.entity.TripwireArrowEntity;
import net.minecraft.entity.effect.StatusEffects;

public final class DiscoveryArrows {

  public static final RegisteredArrow<TorchArrowEntity> TORCH_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of("torch_arrow", TorchArrowEntity::new, TorchArrowEntity::new));

  public static final RegisteredArrow<BeaconArrowEntity> BEACON_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "beacon_arrow",
                  BeaconArrowEntity::new,
                  BeaconArrowEntity::new,
                  ArrowSound.own(ModSounds.BEACON_ARROW_RAISE.getId())));

  public static final RegisteredArrow<ProspectorArrowEntity> PROSPECTOR_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "prospector_arrow",
                  ProspectorArrowEntity::new,
                  ProspectorArrowEntity::new,
                  ArrowSound.own(ModSounds.PROSPECTOR_ARROW_PULSE.getId())));

  public static final RegisteredArrow<SonarArrowEntity> SONAR_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.<SonarArrowEntity>of(
                      "sonar_arrow",
                      SonarArrowEntity::new,
                      SonarArrowEntity::new,
                      ArrowSound.own(ModSounds.SONAR_ARROW_PULSE.getId()))
                  .applying(ArrowEffect.overArea(StatusEffects.GLOWING)));

  public static final RegisteredArrow<TracerArrowEntity> TRACER_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of("tracer_arrow", TracerArrowEntity::new, TracerArrowEntity::new));

  public static final RegisteredArrow<TripwireArrowEntity> TRIPWIRE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "tripwire_arrow",
                  TripwireArrowEntity::new,
                  TripwireArrowEntity::new,
                  ArrowSound.own(ModSounds.TRIPWIRE_ARROW_SET.getId()),
                  ArrowSound.own(ModSounds.TRIPWIRE_ARROW_ALERT.getId())));

  private DiscoveryArrows() {}

  static void register() {
    Volley.LOGGER.debug("Registered the discovery arrows");
  }
}
