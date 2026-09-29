package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.arrow.ArrowCatalog;
import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowRegistrar;
import com.grahambartley.notenougharrows.arrow.ArrowSound;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.AllegianceArrowEntity;
import com.grahambartley.notenougharrows.entity.DisarmArrowEntity;
import com.grahambartley.notenougharrows.entity.DrainArrowEntity;
import com.grahambartley.notenougharrows.entity.DrillArrowEntity;
import com.grahambartley.notenougharrows.entity.EnderPearlArrowEntity;
import com.grahambartley.notenougharrows.entity.FireChargeArrowEntity;
import com.grahambartley.notenougharrows.entity.FreezeArrowEntity;
import com.grahambartley.notenougharrows.entity.FrostArrowEntity;
import com.grahambartley.notenougharrows.entity.GlowInkArrowEntity;
import com.grahambartley.notenougharrows.entity.GrappleArrowEntity;
import com.grahambartley.notenougharrows.entity.GravityArrowEntity;
import com.grahambartley.notenougharrows.entity.GuardArrowEntity;
import com.grahambartley.notenougharrows.entity.GunpowderArrowEntity;
import com.grahambartley.notenougharrows.entity.HasteArrowEntity;
import com.grahambartley.notenougharrows.entity.HomingArrowEntity;
import com.grahambartley.notenougharrows.entity.IncendiaryArrowEntity;
import com.grahambartley.notenougharrows.entity.LevitationArrowEntity;
import com.grahambartley.notenougharrows.entity.LifestealArrowEntity;
import com.grahambartley.notenougharrows.entity.MilkArrowEntity;
import com.grahambartley.notenougharrows.entity.PaintArrowEntity;
import com.grahambartley.notenougharrows.entity.PillarArrowEntity;
import com.grahambartley.notenougharrows.entity.RailgunArrowEntity;
import com.grahambartley.notenougharrows.entity.RecallArrowEntity;
import com.grahambartley.notenougharrows.entity.RedstoneArrowEntity;
import com.grahambartley.notenougharrows.entity.RepelArrowEntity;
import com.grahambartley.notenougharrows.entity.RicochetArrowEntity;
import com.grahambartley.notenougharrows.entity.RopeArrowEntity;
import com.grahambartley.notenougharrows.entity.RustArrowEntity;
import com.grahambartley.notenougharrows.entity.ShockArrowEntity;
import com.grahambartley.notenougharrows.entity.SmokeArrowEntity;
import com.grahambartley.notenougharrows.entity.TauntArrowEntity;
import com.grahambartley.notenougharrows.entity.TntArrowEntity;
import com.grahambartley.notenougharrows.entity.VolleyArrowEntity;
import com.grahambartley.notenougharrows.entity.WebArrowEntity;
import com.grahambartley.notenougharrows.entity.WindArrowEntity;
import com.grahambartley.notenougharrows.tint.DyePalette;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class ModArrows {
  private static final ArrowRegistrar REGISTRAR = new ArrowRegistrar();
  private static final String EXPLOSIVE_FUSE = "explosive_fuse";
  private static final ArrowSound EXPLOSIVE_FUSE_BEEP =
      ArrowSound.sharedBy(EXPLOSIVE_FUSE, ModSounds.COUNTDOWN_BEEP.getId());
  private static final ArrowSound EXPLOSIVE_BLAST =
      ArrowSound.sharedBy(EXPLOSIVE_FUSE, ModSounds.EXPLOSIVE_ARROW_BLAST.getId());
  private static final String ENDER_TELEPORT = "ender_teleport";

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
          ArrowDefinition.of(
              "wind_arrow",
              WindArrowEntity::new,
              ModArrows::windArrow,
              ArrowSound.own(ModSounds.WIND_ARROW_BURST.getId())));

  public static final RegisteredArrow<GunpowderArrowEntity> GUNPOWDER_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "gunpowder_arrow",
              GunpowderArrowEntity::new,
              ModArrows::gunpowderArrow,
              EXPLOSIVE_FUSE_BEEP,
              EXPLOSIVE_BLAST));

  public static final RegisteredArrow<TntArrowEntity> TNT_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "tnt_arrow",
              TntArrowEntity::new,
              ModArrows::tntArrow,
              EXPLOSIVE_FUSE_BEEP,
              EXPLOSIVE_BLAST));

  public static final RegisteredArrow<FireChargeArrowEntity> FIRE_CHARGE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "fire_charge_arrow",
              FireChargeArrowEntity::new,
              ModArrows::fireChargeArrow,
              EXPLOSIVE_FUSE_BEEP,
              EXPLOSIVE_BLAST));

  public static final RegisteredArrow<IncendiaryArrowEntity> INCENDIARY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "incendiary_arrow", IncendiaryArrowEntity::new, ModArrows::incendiaryArrow));

  public static final RegisteredArrow<GravityArrowEntity> GRAVITY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("gravity_arrow", GravityArrowEntity::new, ModArrows::gravityArrow));

  public static final RegisteredArrow<RicochetArrowEntity> RICOCHET_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "ricochet_arrow",
              RicochetArrowEntity::new,
              ModArrows::ricochetArrow,
              ArrowSound.own(ModSounds.RICOCHET_ARROW_BOUNCE.getId())));

  public static final RegisteredArrow<EnderPearlArrowEntity> ENDER_PEARL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "ender_pearl_arrow",
              EnderPearlArrowEntity::new,
              ModArrows::enderPearlArrow,
              ArrowSound.sharedBy(ENDER_TELEPORT, ModSounds.ENDER_TELEPORT.getId())));

  public static final RegisteredArrow<RecallArrowEntity> RECALL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "recall_arrow",
              RecallArrowEntity::new,
              ModArrows::recallArrow,
              ArrowSound.sharedBy(ENDER_TELEPORT, ModSounds.ENDER_TELEPORT.getId())));

  public static final RegisteredArrow<ShockArrowEntity> SHOCK_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "shock_arrow",
              ShockArrowEntity::new,
              ModArrows::shockArrow,
              ArrowSound.own(ModSounds.SHOCK_ARROW_THUNDER.getId()),
              ArrowSound.own(ModSounds.SHOCK_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<LifestealArrowEntity> LIFESTEAL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "lifesteal_arrow", LifestealArrowEntity::new, ModArrows::lifestealArrow));

  public static final RegisteredArrow<RustArrowEntity> RUST_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("rust_arrow", RustArrowEntity::new, ModArrows::rustArrow));

  public static final RegisteredArrow<MilkArrowEntity> MILK_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("milk_arrow", MilkArrowEntity::new, ModArrows::milkArrow));

  public static final RegisteredArrow<HasteArrowEntity> HASTE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("haste_arrow", HasteArrowEntity::new, ModArrows::hasteArrow));

  public static final RegisteredArrow<GuardArrowEntity> GUARD_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("guard_arrow", GuardArrowEntity::new, ModArrows::guardArrow));

  public static final RegisteredArrow<HomingArrowEntity> HOMING_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("homing_arrow", HomingArrowEntity::new, ModArrows::homingArrow));

  public static final RegisteredArrow<VolleyArrowEntity> VOLLEY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("volley_arrow", VolleyArrowEntity::new, ModArrows::volleyArrow));

  public static final RegisteredArrow<RailgunArrowEntity> RAILGUN_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("railgun_arrow", RailgunArrowEntity::new, ModArrows::railgunArrow));

  public static final RegisteredArrow<FrostArrowEntity> FROST_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "frost_arrow",
              FrostArrowEntity::new,
              ModArrows::frostArrow,
              ArrowSound.own(ModSounds.FROST_ARROW_FREEZE_CRACK.getId()),
              ArrowSound.own(ModSounds.FROST_ARROW_FREEZE_SETTLE.getId()),
              ArrowSound.own(ModSounds.FROST_ARROW_THAW.getId())));

  public static final RegisteredArrow<LevitationArrowEntity> LEVITATION_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "levitation_arrow", LevitationArrowEntity::new, ModArrows::levitationArrow));

  public static final RegisteredArrow<TauntArrowEntity> TAUNT_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "taunt_arrow",
              TauntArrowEntity::new,
              ModArrows::tauntArrow,
              ArrowSound.own(ModSounds.TAUNT_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<RepelArrowEntity> REPEL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "repel_arrow",
              RepelArrowEntity::new,
              ModArrows::repelArrow,
              ArrowSound.own(ModSounds.REPEL_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<AllegianceArrowEntity> ALLEGIANCE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "allegiance_arrow",
              AllegianceArrowEntity::new,
              ModArrows::allegianceArrow,
              ArrowSound.own(ModSounds.ALLEGIANCE_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<SmokeArrowEntity> SMOKE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "smoke_arrow",
              SmokeArrowEntity::new,
              ModArrows::smokeArrow,
              ArrowSound.own(ModSounds.SMOKE_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<DisarmArrowEntity> DISARM_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "disarm_arrow",
              DisarmArrowEntity::new,
              ModArrows::disarmArrow,
              ArrowSound.own(ModSounds.DISARM_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<PaintArrowEntity> PAINT_ARROW =
      REGISTRAR.register(
          ArrowDefinition.tinted(
              "paint_arrow", PaintArrowEntity::new, ModArrows::paintArrow, DyePalette.create()));

  public static final RegisteredArrow<DrillArrowEntity> DRILL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "drill_arrow",
              DrillArrowEntity::new,
              ModArrows::drillArrow,
              ArrowSound.own(ModSounds.DRILL_ARROW_BORE.getId())));

  public static final RegisteredArrow<PillarArrowEntity> PILLAR_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "pillar_arrow",
              PillarArrowEntity::new,
              ModArrows::pillarArrow,
              ArrowSound.own(ModSounds.PILLAR_ARROW_RISE.getId())));

  public static final RegisteredArrow<DrainArrowEntity> DRAIN_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "drain_arrow",
              DrainArrowEntity::new,
              ModArrows::drainArrow,
              ArrowSound.own(ModSounds.DRAIN_ARROW_ABSORB.getId())));

  public static final RegisteredArrow<FreezeArrowEntity> FREEZE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "freeze_arrow",
              FreezeArrowEntity::new,
              ModArrows::freezeArrow,
              ArrowSound.own(ModSounds.FREEZE_ARROW_FREEZE.getId())));

  public static final RegisteredArrow<WebArrowEntity> WEB_ARROW =
      REGISTRAR.register(ArrowDefinition.of("web_arrow", WebArrowEntity::new, ModArrows::webArrow));

  static {
    registerLaterFamiliesInOrder();
  }

  private ModArrows() {}

  public static void register() {
    NotEnoughArrows.LOGGER.info("Registered {} arrow types", REGISTRAR.registrations().size());
  }

  private static void registerLaterFamiliesInOrder() {
    AgricultureArrows.register();
    TraversalArrows.register();
    DiscoveryArrows.register();
    ChaosArrows.register();
    SocialArrows.register();
  }

  static ArrowRegistrar registrar() {
    return REGISTRAR;
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

  private static ShockArrowEntity shockArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new ShockArrowEntity(SHOCK_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static LifestealArrowEntity lifestealArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new LifestealArrowEntity(LIFESTEAL_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static RustArrowEntity rustArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new RustArrowEntity(RUST_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static MilkArrowEntity milkArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new MilkArrowEntity(MILK_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static HasteArrowEntity hasteArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new HasteArrowEntity(HASTE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static GuardArrowEntity guardArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new GuardArrowEntity(GUARD_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static HomingArrowEntity homingArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new HomingArrowEntity(HOMING_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static VolleyArrowEntity volleyArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new VolleyArrowEntity(VOLLEY_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static RailgunArrowEntity railgunArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new RailgunArrowEntity(RAILGUN_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static FrostArrowEntity frostArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new FrostArrowEntity(FROST_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static LevitationArrowEntity levitationArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new LevitationArrowEntity(LEVITATION_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static TauntArrowEntity tauntArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new TauntArrowEntity(TAUNT_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static RepelArrowEntity repelArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new RepelArrowEntity(REPEL_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static SmokeArrowEntity smokeArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new SmokeArrowEntity(SMOKE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static DisarmArrowEntity disarmArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new DisarmArrowEntity(DISARM_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static AllegianceArrowEntity allegianceArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new AllegianceArrowEntity(ALLEGIANCE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static PaintArrowEntity paintArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new PaintArrowEntity(PAINT_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static DrillArrowEntity drillArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new DrillArrowEntity(DRILL_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static PillarArrowEntity pillarArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new PillarArrowEntity(PILLAR_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static DrainArrowEntity drainArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new DrainArrowEntity(DRAIN_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static FreezeArrowEntity freezeArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new FreezeArrowEntity(FREEZE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }

  private static WebArrowEntity webArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new WebArrowEntity(WEB_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
