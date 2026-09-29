package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowSound;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.CourierArrowEntity;
import com.grahambartley.notenougharrows.entity.MagnetArrowEntity;
import com.grahambartley.notenougharrows.entity.SnowGolemArrowEntity;
import com.grahambartley.notenougharrows.item.CourierArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class SocialArrows {

  public static final RegisteredArrow<CourierArrowEntity> COURIER_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "courier_arrow",
                  CourierArrowEntity::new,
                  SocialArrows::courierArrow,
                  ArrowSound.own(ModSounds.COURIER_ARROW_DELIVER.getId())),
              CourierArrowItem::new);

  public static final RegisteredArrow<SnowGolemArrowEntity> SNOW_GOLEM_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "snow_golem_arrow",
                  SnowGolemArrowEntity::new,
                  SocialArrows::snowGolemArrow,
                  ArrowSound.own(ModSounds.SNOW_GOLEM_ARROW_MELT.getId())));

  public static final RegisteredArrow<MagnetArrowEntity> MAGNET_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "magnet_arrow",
                  MagnetArrowEntity::new,
                  SocialArrows::magnetArrow,
                  ArrowSound.own(ModSounds.MAGNET_ARROW_PULL.getId())));

  private SocialArrows() {}

  static void register() {
    NotEnoughArrows.LOGGER.debug("Registered the social arrows");
  }

  private static CourierArrowEntity courierArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new CourierArrowEntity(COURIER_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static SnowGolemArrowEntity snowGolemArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new SnowGolemArrowEntity(SNOW_GOLEM_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static MagnetArrowEntity magnetArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new MagnetArrowEntity(MAGNET_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
