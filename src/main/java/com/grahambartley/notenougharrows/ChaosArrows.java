package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowSound;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.chaos.DiscPalette;
import com.grahambartley.notenougharrows.entity.BoomerangArrowEntity;
import com.grahambartley.notenougharrows.entity.ChickenArrowEntity;
import com.grahambartley.notenougharrows.entity.PartyArrowEntity;
import com.grahambartley.notenougharrows.entity.PolymorphArrowEntity;
import com.grahambartley.notenougharrows.entity.PufferArrowEntity;
import com.grahambartley.notenougharrows.entity.StinkArrowEntity;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class ChaosArrows {
  public static final int BOOMERANG_TRACKING_TICK_INTERVAL = 1;

  public static final RegisteredArrow<PartyArrowEntity> PARTY_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.tinted(
                  "party_arrow",
                  PartyArrowEntity::new,
                  ChaosArrows::partyArrow,
                  DiscPalette.create()));

  public static final RegisteredArrow<ChickenArrowEntity> CHICKEN_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "chicken_arrow",
                  ChickenArrowEntity::new,
                  ChaosArrows::chickenArrow,
                  ArrowSound.own(ModSounds.CHICKEN_ARROW_HATCH.getId())));

  public static final RegisteredArrow<PufferArrowEntity> PUFFER_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "puffer_arrow",
                  PufferArrowEntity::new,
                  ChaosArrows::pufferArrow,
                  ArrowSound.own(ModSounds.PUFFER_ARROW_INFLATE.getId()),
                  ArrowSound.own(ModSounds.PUFFER_ARROW_DEFLATE.getId())));

  public static final RegisteredArrow<StinkArrowEntity> STINK_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "stink_arrow",
                  StinkArrowEntity::new,
                  ChaosArrows::stinkArrow,
                  ArrowSound.own(ModSounds.STINK_ARROW_RELEASE.getId())));

  public static final RegisteredArrow<BoomerangArrowEntity> BOOMERANG_ARROW =
      ModArrows.registrar()
          .register(
              new ArrowDefinition<>(
                  "boomerang_arrow",
                  BoomerangArrowEntity::new,
                  ChaosArrows::boomerangArrow,
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
                  ChaosArrows::polymorphArrow,
                  ArrowSound.own(ModSounds.POLYMORPH_ARROW_CHANGE.getId()),
                  ArrowSound.own(ModSounds.POLYMORPH_ARROW_RESTORE.getId())));

  private ChaosArrows() {}

  static void register() {
    NotEnoughArrows.LOGGER.debug("Registered the chaos arrows");
  }

  private static PartyArrowEntity partyArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new PartyArrowEntity(PARTY_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static ChickenArrowEntity chickenArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new ChickenArrowEntity(CHICKEN_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static PufferArrowEntity pufferArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new PufferArrowEntity(PUFFER_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static StinkArrowEntity stinkArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new StinkArrowEntity(STINK_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static BoomerangArrowEntity boomerangArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new BoomerangArrowEntity(BOOMERANG_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static PolymorphArrowEntity polymorphArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new PolymorphArrowEntity(POLYMORPH_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
