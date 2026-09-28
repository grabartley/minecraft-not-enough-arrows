package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowSound;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.BridgeArrowEntity;
import com.grahambartley.notenougharrows.entity.ScaffoldArrowEntity;
import com.grahambartley.notenougharrows.entity.TowArrowEntity;
import com.grahambartley.notenougharrows.entity.TrampolineArrowEntity;
import com.grahambartley.notenougharrows.entity.UpdraftArrowEntity;
import com.grahambartley.notenougharrows.entity.VineArrowEntity;
import com.grahambartley.notenougharrows.entity.ZiplineArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class TraversalArrows {

  public static final RegisteredArrow<ZiplineArrowEntity> ZIPLINE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "zipline_arrow",
                  ZiplineArrowEntity::new,
                  TraversalArrows::ziplineArrow,
                  ArrowSound.own(ModSounds.ZIPLINE_ARROW_STRING.getId())));

  public static final RegisteredArrow<TowArrowEntity> TOW_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of("tow_arrow", TowArrowEntity::new, TraversalArrows::towArrow));

  public static final RegisteredArrow<UpdraftArrowEntity> UPDRAFT_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "updraft_arrow",
                  UpdraftArrowEntity::new,
                  TraversalArrows::updraftArrow,
                  ArrowSound.own(ModSounds.UPDRAFT_ARROW_OPEN.getId())));

  public static final RegisteredArrow<VineArrowEntity> VINE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of("vine_arrow", VineArrowEntity::new, TraversalArrows::vineArrow));

  public static final RegisteredArrow<TrampolineArrowEntity> TRAMPOLINE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "trampoline_arrow",
                  TrampolineArrowEntity::new,
                  TraversalArrows::trampolineArrow,
                  ArrowSound.own(ModSounds.TRAMPOLINE_ARROW_LAUNCH.getId())));

  public static final RegisteredArrow<ScaffoldArrowEntity> SCAFFOLD_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "scaffold_arrow", ScaffoldArrowEntity::new, TraversalArrows::scaffoldArrow));

  public static final RegisteredArrow<BridgeArrowEntity> BRIDGE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "bridge_arrow", BridgeArrowEntity::new, TraversalArrows::bridgeArrow));

  private TraversalArrows() {}

  static void register() {
    NotEnoughArrows.LOGGER.debug("Registered the traversal arrows");
  }

  private static ZiplineArrowEntity ziplineArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new ZiplineArrowEntity(ZIPLINE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static TowArrowEntity towArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TowArrowEntity(TOW_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static UpdraftArrowEntity updraftArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new UpdraftArrowEntity(UPDRAFT_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static VineArrowEntity vineArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new VineArrowEntity(VINE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static TrampolineArrowEntity trampolineArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TrampolineArrowEntity(TRAMPOLINE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static ScaffoldArrowEntity scaffoldArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new ScaffoldArrowEntity(SCAFFOLD_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static BridgeArrowEntity bridgeArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new BridgeArrowEntity(BRIDGE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
