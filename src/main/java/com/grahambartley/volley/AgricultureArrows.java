package com.grahambartley.volley;

import com.grahambartley.volley.agriculture.SaplingPalette;
import com.grahambartley.volley.arrow.ArrowDefinition;
import com.grahambartley.volley.arrow.ArrowSound;
import com.grahambartley.volley.arrow.RegisteredArrow;
import com.grahambartley.volley.entity.BeeArrowEntity;
import com.grahambartley.volley.entity.BlossomArrowEntity;
import com.grahambartley.volley.entity.HarvestArrowEntity;
import com.grahambartley.volley.entity.SaplingArrowEntity;
import com.grahambartley.volley.entity.ShearArrowEntity;
import com.grahambartley.volley.entity.TillArrowEntity;

public final class AgricultureArrows {

  public static final RegisteredArrow<BlossomArrowEntity> BLOSSOM_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "blossom_arrow",
                  BlossomArrowEntity::new,
                  BlossomArrowEntity::new,
                  ArrowSound.own(ModSounds.BLOSSOM_ARROW_BLOOM.getId())));

  public static final RegisteredArrow<HarvestArrowEntity> HARVEST_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "harvest_arrow",
                  HarvestArrowEntity::new,
                  HarvestArrowEntity::new,
                  ArrowSound.own(ModSounds.HARVEST_ARROW_REAP.getId())));

  public static final RegisteredArrow<TillArrowEntity> TILL_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "till_arrow",
                  TillArrowEntity::new,
                  TillArrowEntity::new,
                  ArrowSound.own(ModSounds.TILL_ARROW_TILL.getId())));

  public static final RegisteredArrow<SaplingArrowEntity> SAPLING_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.tinted(
                  "sapling_arrow",
                  SaplingArrowEntity::new,
                  SaplingArrowEntity::new,
                  SaplingPalette.create()));

  public static final RegisteredArrow<ShearArrowEntity> SHEAR_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "shear_arrow",
                  ShearArrowEntity::new,
                  ShearArrowEntity::new,
                  ArrowSound.own(ModSounds.SHEAR_ARROW_CARVE.getId()),
                  ArrowSound.own(ModSounds.SHEAR_ARROW_HIVE.getId())));

  public static final RegisteredArrow<BeeArrowEntity> BEE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "bee_arrow",
                  BeeArrowEntity::new,
                  BeeArrowEntity::new,
                  ArrowSound.own(ModSounds.BEE_ARROW_RELEASE.getId())));

  private AgricultureArrows() {}

  static void register() {
    Volley.LOGGER.debug("Registered the agriculture arrows");
  }
}
