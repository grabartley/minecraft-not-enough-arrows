package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowSound;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.BeaconArrowEntity;
import com.grahambartley.notenougharrows.entity.ProspectorArrowEntity;
import com.grahambartley.notenougharrows.entity.SonarArrowEntity;
import com.grahambartley.notenougharrows.entity.TorchArrowEntity;
import com.grahambartley.notenougharrows.entity.TracerArrowEntity;
import com.grahambartley.notenougharrows.entity.TripwireArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class DiscoveryArrows {

  public static final RegisteredArrow<TorchArrowEntity> TORCH_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "torch_arrow", TorchArrowEntity::new, DiscoveryArrows::torchArrow));

  public static final RegisteredArrow<BeaconArrowEntity> BEACON_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "beacon_arrow",
                  BeaconArrowEntity::new,
                  DiscoveryArrows::beaconArrow,
                  ArrowSound.own(ModSounds.BEACON_ARROW_RAISE.getId())));

  public static final RegisteredArrow<ProspectorArrowEntity> PROSPECTOR_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "prospector_arrow",
                  ProspectorArrowEntity::new,
                  DiscoveryArrows::prospectorArrow,
                  ArrowSound.own(ModSounds.PROSPECTOR_ARROW_PULSE.getId())));

  public static final RegisteredArrow<SonarArrowEntity> SONAR_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "sonar_arrow",
                  SonarArrowEntity::new,
                  DiscoveryArrows::sonarArrow,
                  ArrowSound.own(ModSounds.SONAR_ARROW_PULSE.getId())));

  public static final RegisteredArrow<TracerArrowEntity> TRACER_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "tracer_arrow", TracerArrowEntity::new, DiscoveryArrows::tracerArrow));

  public static final RegisteredArrow<TripwireArrowEntity> TRIPWIRE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "tripwire_arrow",
                  TripwireArrowEntity::new,
                  DiscoveryArrows::tripwireArrow,
                  ArrowSound.own(ModSounds.TRIPWIRE_ARROW_SET.getId()),
                  ArrowSound.own(ModSounds.TRIPWIRE_ARROW_ALERT.getId())));

  private DiscoveryArrows() {}

  static void register() {
    NotEnoughArrows.LOGGER.debug("Registered the discovery arrows");
  }

  private static TorchArrowEntity torchArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TorchArrowEntity(TORCH_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static BeaconArrowEntity beaconArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new BeaconArrowEntity(BEACON_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static ProspectorArrowEntity prospectorArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new ProspectorArrowEntity(PROSPECTOR_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static SonarArrowEntity sonarArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new SonarArrowEntity(SONAR_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static TracerArrowEntity tracerArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TracerArrowEntity(TRACER_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static TripwireArrowEntity tripwireArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TripwireArrowEntity(TRIPWIRE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
