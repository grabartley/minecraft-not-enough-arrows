package com.grahambartley.morearrows;

import com.grahambartley.morearrows.arrow.ArrowCatalog;
import com.grahambartley.morearrows.arrow.ArrowDefinition;
import com.grahambartley.morearrows.arrow.ArrowRegistrar;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.entity.EnderPearlArrowEntity;
import com.grahambartley.morearrows.entity.FireChargeArrowEntity;
import com.grahambartley.morearrows.entity.GlowInkArrowEntity;
import com.grahambartley.morearrows.entity.GrappleArrowEntity;
import com.grahambartley.morearrows.entity.GravityArrowEntity;
import com.grahambartley.morearrows.entity.GunpowderArrowEntity;
import com.grahambartley.morearrows.entity.IncendiaryArrowEntity;
import com.grahambartley.morearrows.entity.RecallArrowEntity;
import com.grahambartley.morearrows.entity.RedstoneArrowEntity;
import com.grahambartley.morearrows.entity.RicochetArrowEntity;
import com.grahambartley.morearrows.entity.RopeArrowEntity;
import com.grahambartley.morearrows.entity.TntArrowEntity;
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

  public static final RegisteredArrow<GunpowderArrowEntity> GUNPOWDER_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "gunpowder_arrow", GunpowderArrowEntity::new, ModArrows::gunpowderArrow));

  public static final RegisteredArrow<TntArrowEntity> TNT_ARROW =
      REGISTRAR.register(ArrowDefinition.of("tnt_arrow", TntArrowEntity::new, ModArrows::tntArrow));

  public static final RegisteredArrow<FireChargeArrowEntity> FIRE_CHARGE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "fire_charge_arrow", FireChargeArrowEntity::new, ModArrows::fireChargeArrow));

  public static final RegisteredArrow<IncendiaryArrowEntity> INCENDIARY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "incendiary_arrow", IncendiaryArrowEntity::new, ModArrows::incendiaryArrow));

  public static final RegisteredArrow<GravityArrowEntity> GRAVITY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("gravity_arrow", GravityArrowEntity::new, ModArrows::gravityArrow));

  public static final RegisteredArrow<RicochetArrowEntity> RICOCHET_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("ricochet_arrow", RicochetArrowEntity::new, ModArrows::ricochetArrow));

  public static final RegisteredArrow<EnderPearlArrowEntity> ENDER_PEARL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "ender_pearl_arrow", EnderPearlArrowEntity::new, ModArrows::enderPearlArrow));

  public static final RegisteredArrow<RecallArrowEntity> RECALL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("recall_arrow", RecallArrowEntity::new, ModArrows::recallArrow));

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

  private static GunpowderArrowEntity gunpowderArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new GunpowderArrowEntity(GUNPOWDER_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static TntArrowEntity tntArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TntArrowEntity(TNT_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static FireChargeArrowEntity fireChargeArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new FireChargeArrowEntity(FIRE_CHARGE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static IncendiaryArrowEntity incendiaryArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new IncendiaryArrowEntity(INCENDIARY_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static GravityArrowEntity gravityArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new GravityArrowEntity(GRAVITY_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static RicochetArrowEntity ricochetArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new RicochetArrowEntity(RICOCHET_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static EnderPearlArrowEntity enderPearlArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new EnderPearlArrowEntity(ENDER_PEARL_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static RecallArrowEntity recallArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new RecallArrowEntity(RECALL_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
