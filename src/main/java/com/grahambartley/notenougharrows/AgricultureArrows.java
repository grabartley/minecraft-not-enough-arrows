package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.agriculture.SaplingPalette;
import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowSound;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.BeeArrowEntity;
import com.grahambartley.notenougharrows.entity.BlossomArrowEntity;
import com.grahambartley.notenougharrows.entity.HarvestArrowEntity;
import com.grahambartley.notenougharrows.entity.SaplingArrowEntity;
import com.grahambartley.notenougharrows.entity.ShearArrowEntity;
import com.grahambartley.notenougharrows.entity.TillArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class AgricultureArrows {

  public static final RegisteredArrow<BlossomArrowEntity> BLOSSOM_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "blossom_arrow",
                  BlossomArrowEntity::new,
                  AgricultureArrows::blossomArrow,
                  ArrowSound.own(ModSounds.BLOSSOM_ARROW_BLOOM.getId())));

  public static final RegisteredArrow<HarvestArrowEntity> HARVEST_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "harvest_arrow",
                  HarvestArrowEntity::new,
                  AgricultureArrows::harvestArrow,
                  ArrowSound.own(ModSounds.HARVEST_ARROW_REAP.getId())));

  public static final RegisteredArrow<TillArrowEntity> TILL_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "till_arrow",
                  TillArrowEntity::new,
                  AgricultureArrows::tillArrow,
                  ArrowSound.own(ModSounds.TILL_ARROW_TILL.getId())));

  public static final RegisteredArrow<SaplingArrowEntity> SAPLING_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.tinted(
                  "sapling_arrow",
                  SaplingArrowEntity::new,
                  AgricultureArrows::saplingArrow,
                  SaplingPalette.create()));

  public static final RegisteredArrow<ShearArrowEntity> SHEAR_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "shear_arrow",
                  ShearArrowEntity::new,
                  AgricultureArrows::shearArrow,
                  ArrowSound.own(ModSounds.SHEAR_ARROW_CARVE.getId()),
                  ArrowSound.own(ModSounds.SHEAR_ARROW_HIVE.getId())));

  public static final RegisteredArrow<BeeArrowEntity> BEE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "bee_arrow",
                  BeeArrowEntity::new,
                  AgricultureArrows::beeArrow,
                  ArrowSound.own(ModSounds.BEE_ARROW_RELEASE.getId())));

  private AgricultureArrows() {}

  static void register() {
    NotEnoughArrows.LOGGER.debug("Registered the agriculture arrows");
  }

  private static BlossomArrowEntity blossomArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new BlossomArrowEntity(BLOSSOM_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static HarvestArrowEntity harvestArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new HarvestArrowEntity(HARVEST_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static TillArrowEntity tillArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TillArrowEntity(TILL_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static SaplingArrowEntity saplingArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new SaplingArrowEntity(SAPLING_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static ShearArrowEntity shearArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new ShearArrowEntity(SHEAR_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static BeeArrowEntity beeArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new BeeArrowEntity(BEE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
