package com.grahambartley.volley;

import com.grahambartley.volley.arrow.ArrowDefinition;
import com.grahambartley.volley.arrow.ArrowSound;
import com.grahambartley.volley.arrow.RegisteredArrow;
import com.grahambartley.volley.entity.CourierArrowEntity;
import com.grahambartley.volley.entity.MagnetArrowEntity;
import com.grahambartley.volley.entity.SnowGolemArrowEntity;
import com.grahambartley.volley.item.CourierArrowItem;

public final class SocialArrows {

  public static final RegisteredArrow<CourierArrowEntity> COURIER_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "courier_arrow",
                  CourierArrowEntity::new,
                  CourierArrowEntity::new,
                  ArrowSound.own(ModSounds.COURIER_ARROW_DELIVER.getId())),
              CourierArrowItem::new);

  public static final RegisteredArrow<SnowGolemArrowEntity> SNOW_GOLEM_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "snow_golem_arrow",
                  SnowGolemArrowEntity::new,
                  SnowGolemArrowEntity::new,
                  ArrowSound.own(ModSounds.SNOW_GOLEM_ARROW_MELT.getId())));

  public static final RegisteredArrow<MagnetArrowEntity> MAGNET_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "magnet_arrow",
                  MagnetArrowEntity::new,
                  MagnetArrowEntity::new,
                  ArrowSound.own(ModSounds.MAGNET_ARROW_PULL.getId())));

  private SocialArrows() {}

  static void register() {
    Volley.LOGGER.debug("Registered the social arrows");
  }
}
