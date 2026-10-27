package com.grahambartley.volley;

import com.grahambartley.volley.arrow.ArrowDefinition;
import com.grahambartley.volley.arrow.ArrowEffect;
import com.grahambartley.volley.arrow.ArrowSound;
import com.grahambartley.volley.arrow.RegisteredArrow;
import com.grahambartley.volley.chaos.DiscPalette;
import com.grahambartley.volley.entity.BoomerangArrowEntity;
import com.grahambartley.volley.entity.ChickenArrowEntity;
import com.grahambartley.volley.entity.PartyArrowEntity;
import com.grahambartley.volley.entity.PolymorphArrowEntity;
import com.grahambartley.volley.entity.PufferArrowEntity;
import com.grahambartley.volley.entity.StinkArrowEntity;
import java.util.List;
import net.minecraft.entity.effect.StatusEffects;

public final class ChaosArrows {
  public static final int BOOMERANG_TRACKING_TICK_INTERVAL = 1;

  public static final RegisteredArrow<PartyArrowEntity> PARTY_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.tinted(
                  "party_arrow",
                  PartyArrowEntity::new,
                  PartyArrowEntity::new,
                  DiscPalette.create()));

  public static final RegisteredArrow<ChickenArrowEntity> CHICKEN_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "chicken_arrow",
                  ChickenArrowEntity::new,
                  ChickenArrowEntity::new,
                  ArrowSound.own(ModSounds.CHICKEN_ARROW_HATCH.getId())));

  public static final RegisteredArrow<PufferArrowEntity> PUFFER_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "puffer_arrow",
                  PufferArrowEntity::new,
                  PufferArrowEntity::new,
                  ArrowSound.own(ModSounds.PUFFER_ARROW_INFLATE.getId()),
                  ArrowSound.own(ModSounds.PUFFER_ARROW_DEFLATE.getId())));

  public static final RegisteredArrow<StinkArrowEntity> STINK_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.<StinkArrowEntity>of(
                      "stink_arrow",
                      StinkArrowEntity::new,
                      StinkArrowEntity::new,
                      ArrowSound.own(ModSounds.STINK_ARROW_RELEASE.getId()))
                  .applying(ArrowEffect.overArea(StatusEffects.NAUSEA)));

  public static final RegisteredArrow<BoomerangArrowEntity> BOOMERANG_ARROW =
      ModArrows.registrar()
          .register(
              new ArrowDefinition<>(
                  "boomerang_arrow",
                  BoomerangArrowEntity::new,
                  BoomerangArrowEntity::new,
                  ArrowDefinition.DEFAULT_SIZE,
                  ArrowDefinition.DEFAULT_SIZE,
                  ArrowDefinition.DEFAULT_MAX_TRACKING_RANGE,
                  BOOMERANG_TRACKING_TICK_INTERVAL,
                  List.of(ArrowSound.own(ModSounds.BOOMERANG_ARROW_RETURN.getId()))));

  public static final RegisteredArrow<PolymorphArrowEntity> POLYMORPH_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "polymorph_arrow",
                  PolymorphArrowEntity::new,
                  PolymorphArrowEntity::new,
                  ArrowSound.own(ModSounds.POLYMORPH_ARROW_CHANGE.getId()),
                  ArrowSound.own(ModSounds.POLYMORPH_ARROW_RESTORE.getId())));

  private ChaosArrows() {}

  static void register() {
    Volley.LOGGER.debug("Registered the chaos arrows");
  }
}
