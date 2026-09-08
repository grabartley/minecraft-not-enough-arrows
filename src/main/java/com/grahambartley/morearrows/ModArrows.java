package com.grahambartley.morearrows;

import com.grahambartley.morearrows.arrow.ArrowCatalog;
import com.grahambartley.morearrows.arrow.ArrowDefinition;
import com.grahambartley.morearrows.arrow.ArrowRegistrar;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.entity.GlowInkArrowEntity;
import com.grahambartley.morearrows.entity.GrappleArrowEntity;
import com.grahambartley.morearrows.entity.RedstoneArrowEntity;
import com.grahambartley.morearrows.entity.RopeArrowEntity;
import com.grahambartley.morearrows.entity.WindArrowEntity;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class ModArrows {
  private static final ArrowRegistrar REGISTRAR = new ArrowRegistrar();

  public static final RegisteredArrow<GrappleArrowEntity> GRAPPLE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("grapple_arrow", GrappleArrowEntity::new, ModArrows::grappleArrow));

  public static final RegisteredArrow<RopeArrowEntity> ROPE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("rope_arrow", RopeArrowEntity::new, ModArrows::ropeArrow));

  public static final RegisteredArrow<GlowInkArrowEntity> GLOW_INK_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("glow_ink_arrow", GlowInkArrowEntity::new, ModArrows::glowInkArrow));

  public static final RegisteredArrow<RedstoneArrowEntity> REDSTONE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("redstone_arrow", RedstoneArrowEntity::new, ModArrows::redstoneArrow));

  public static final RegisteredArrow<WindArrowEntity> WIND_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("wind_arrow", WindArrowEntity::new, ModArrows::windArrow));

  private ModArrows() {}

  public static void register() {
    MoreArrows.LOGGER.info("Registered {} arrow types", REGISTRAR.registrations().size());
  }

  public static List<RegisteredArrow<?>> registered() {
    return REGISTRAR.registrations();
  }

  public static ArrowCatalog catalog() {
    return REGISTRAR.catalog();
  }

  private static GrappleArrowEntity grappleArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new GrappleArrowEntity(GRAPPLE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static RopeArrowEntity ropeArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new RopeArrowEntity(ROPE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static GlowInkArrowEntity glowInkArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new GlowInkArrowEntity(GLOW_INK_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static RedstoneArrowEntity redstoneArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new RedstoneArrowEntity(REDSTONE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static WindArrowEntity windArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new WindArrowEntity(WIND_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
