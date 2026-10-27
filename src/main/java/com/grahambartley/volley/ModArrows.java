package com.grahambartley.volley;

import com.grahambartley.volley.arrow.ArrowCatalog;
import com.grahambartley.volley.arrow.ArrowDefinition;
import com.grahambartley.volley.arrow.ArrowEffect;
import com.grahambartley.volley.arrow.ArrowRegistrar;
import com.grahambartley.volley.arrow.ArrowSound;
import com.grahambartley.volley.arrow.RegisteredArrow;
import com.grahambartley.volley.entity.AllegianceArrowEntity;
import com.grahambartley.volley.entity.DisarmArrowEntity;
import com.grahambartley.volley.entity.DrainArrowEntity;
import com.grahambartley.volley.entity.DrillArrowEntity;
import com.grahambartley.volley.entity.EnderPearlArrowEntity;
import com.grahambartley.volley.entity.FireChargeArrowEntity;
import com.grahambartley.volley.entity.FreezeArrowEntity;
import com.grahambartley.volley.entity.FrostArrowEntity;
import com.grahambartley.volley.entity.GlowInkArrowEntity;
import com.grahambartley.volley.entity.GrappleArrowEntity;
import com.grahambartley.volley.entity.GravityArrowEntity;
import com.grahambartley.volley.entity.GuardArrowEntity;
import com.grahambartley.volley.entity.GunpowderArrowEntity;
import com.grahambartley.volley.entity.HasteArrowEntity;
import com.grahambartley.volley.entity.HomingArrowEntity;
import com.grahambartley.volley.entity.IncendiaryArrowEntity;
import com.grahambartley.volley.entity.LevitationArrowEntity;
import com.grahambartley.volley.entity.LifestealArrowEntity;
import com.grahambartley.volley.entity.MilkArrowEntity;
import com.grahambartley.volley.entity.PaintArrowEntity;
import com.grahambartley.volley.entity.PillarArrowEntity;
import com.grahambartley.volley.entity.RailgunArrowEntity;
import com.grahambartley.volley.entity.RecallArrowEntity;
import com.grahambartley.volley.entity.RedstoneArrowEntity;
import com.grahambartley.volley.entity.RepelArrowEntity;
import com.grahambartley.volley.entity.RicochetArrowEntity;
import com.grahambartley.volley.entity.RopeArrowEntity;
import com.grahambartley.volley.entity.RustArrowEntity;
import com.grahambartley.volley.entity.ShockArrowEntity;
import com.grahambartley.volley.entity.SmokeArrowEntity;
import com.grahambartley.volley.entity.TauntArrowEntity;
import com.grahambartley.volley.entity.TntArrowEntity;
import com.grahambartley.volley.entity.VolleyArrowEntity;
import com.grahambartley.volley.entity.WebArrowEntity;
import com.grahambartley.volley.entity.WindArrowEntity;
import com.grahambartley.volley.tint.DyePalette;
import java.util.List;
import net.minecraft.entity.effect.StatusEffects;

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
          ArrowDefinition.of("grapple_arrow", GrappleArrowEntity::new, GrappleArrowEntity::new));

  public static final RegisteredArrow<RopeArrowEntity> ROPE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("rope_arrow", RopeArrowEntity::new, RopeArrowEntity::new));

  public static final RegisteredArrow<GlowInkArrowEntity> GLOW_INK_ARROW =
      REGISTRAR.register(
          ArrowDefinition.<GlowInkArrowEntity>of(
                  "glow_ink_arrow", GlowInkArrowEntity::new, GlowInkArrowEntity::new)
              .applying(ArrowEffect.onStruckTarget(StatusEffects.GLOWING)));

  public static final RegisteredArrow<RedstoneArrowEntity> REDSTONE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("redstone_arrow", RedstoneArrowEntity::new, RedstoneArrowEntity::new));

  public static final RegisteredArrow<WindArrowEntity> WIND_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "wind_arrow",
              WindArrowEntity::new,
              WindArrowEntity::new,
              ArrowSound.own(ModSounds.WIND_ARROW_BURST.getId())));

  public static final RegisteredArrow<GunpowderArrowEntity> GUNPOWDER_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "gunpowder_arrow",
              GunpowderArrowEntity::new,
              GunpowderArrowEntity::new,
              EXPLOSIVE_FUSE_BEEP,
              EXPLOSIVE_BLAST));

  public static final RegisteredArrow<TntArrowEntity> TNT_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "tnt_arrow",
              TntArrowEntity::new,
              TntArrowEntity::new,
              EXPLOSIVE_FUSE_BEEP,
              EXPLOSIVE_BLAST));

  public static final RegisteredArrow<FireChargeArrowEntity> FIRE_CHARGE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "fire_charge_arrow",
              FireChargeArrowEntity::new,
              FireChargeArrowEntity::new,
              EXPLOSIVE_FUSE_BEEP,
              EXPLOSIVE_BLAST));

  public static final RegisteredArrow<IncendiaryArrowEntity> INCENDIARY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "incendiary_arrow", IncendiaryArrowEntity::new, IncendiaryArrowEntity::new));

  public static final RegisteredArrow<GravityArrowEntity> GRAVITY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("gravity_arrow", GravityArrowEntity::new, GravityArrowEntity::new));

  public static final RegisteredArrow<RicochetArrowEntity> RICOCHET_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "ricochet_arrow",
              RicochetArrowEntity::new,
              RicochetArrowEntity::new,
              ArrowSound.own(ModSounds.RICOCHET_ARROW_BOUNCE.getId())));

  public static final RegisteredArrow<EnderPearlArrowEntity> ENDER_PEARL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "ender_pearl_arrow",
              EnderPearlArrowEntity::new,
              EnderPearlArrowEntity::new,
              ArrowSound.sharedBy(ENDER_TELEPORT, ModSounds.ENDER_TELEPORT.getId())));

  public static final RegisteredArrow<RecallArrowEntity> RECALL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "recall_arrow",
              RecallArrowEntity::new,
              RecallArrowEntity::new,
              ArrowSound.sharedBy(ENDER_TELEPORT, ModSounds.ENDER_TELEPORT.getId())));

  public static final RegisteredArrow<ShockArrowEntity> SHOCK_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "shock_arrow",
              ShockArrowEntity::new,
              ShockArrowEntity::new,
              ArrowSound.own(ModSounds.SHOCK_ARROW_THUNDER.getId()),
              ArrowSound.own(ModSounds.SHOCK_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<LifestealArrowEntity> LIFESTEAL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "lifesteal_arrow",
              LifestealArrowEntity::new,
              LifestealArrowEntity::new,
              ArrowSound.own(ModSounds.LIFESTEAL_ARROW_DRAIN.getId())));

  public static final RegisteredArrow<RustArrowEntity> RUST_ARROW =
      REGISTRAR.register(
          ArrowDefinition.<RustArrowEntity>of(
                  "rust_arrow", RustArrowEntity::new, RustArrowEntity::new)
              .applying(ArrowEffect.onStruckTarget(StatusEffects.MINING_FATIGUE)));

  public static final RegisteredArrow<MilkArrowEntity> MILK_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "milk_arrow",
              MilkArrowEntity::new,
              MilkArrowEntity::new,
              ArrowSound.own(ModSounds.MILK_ARROW_WASH.getId())));

  public static final RegisteredArrow<HasteArrowEntity> HASTE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.<HasteArrowEntity>of(
                  "haste_arrow", HasteArrowEntity::new, HasteArrowEntity::new)
              .applying(ArrowEffect.onStruckTarget(StatusEffects.HASTE)));

  public static final RegisteredArrow<GuardArrowEntity> GUARD_ARROW =
      REGISTRAR.register(
          ArrowDefinition.<GuardArrowEntity>of(
                  "guard_arrow", GuardArrowEntity::new, GuardArrowEntity::new)
              .applying(ArrowEffect.onStruckTarget(StatusEffects.ABSORPTION)));

  public static final RegisteredArrow<HomingArrowEntity> HOMING_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("homing_arrow", HomingArrowEntity::new, HomingArrowEntity::new));

  public static final RegisteredArrow<VolleyArrowEntity> VOLLEY_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "volley_arrow",
              VolleyArrowEntity::new,
              VolleyArrowEntity::new,
              ArrowSound.own(ModSounds.VOLLEY_ARROW_SPLIT.getId())));

  public static final RegisteredArrow<RailgunArrowEntity> RAILGUN_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("railgun_arrow", RailgunArrowEntity::new, RailgunArrowEntity::new));

  public static final RegisteredArrow<FrostArrowEntity> FROST_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "frost_arrow",
              FrostArrowEntity::new,
              FrostArrowEntity::new,
              ArrowSound.own(ModSounds.FROST_ARROW_FREEZE_CRACK.getId()),
              ArrowSound.own(ModSounds.FROST_ARROW_FREEZE_SETTLE.getId()),
              ArrowSound.own(ModSounds.FROST_ARROW_THAW.getId())));

  public static final RegisteredArrow<LevitationArrowEntity> LEVITATION_ARROW =
      REGISTRAR.register(
          ArrowDefinition.<LevitationArrowEntity>of(
                  "levitation_arrow", LevitationArrowEntity::new, LevitationArrowEntity::new)
              .applying(ArrowEffect.onStruckTarget(StatusEffects.LEVITATION)));

  public static final RegisteredArrow<TauntArrowEntity> TAUNT_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "taunt_arrow",
              TauntArrowEntity::new,
              TauntArrowEntity::new,
              ArrowSound.own(ModSounds.TAUNT_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<RepelArrowEntity> REPEL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "repel_arrow",
              RepelArrowEntity::new,
              RepelArrowEntity::new,
              ArrowSound.own(ModSounds.REPEL_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<AllegianceArrowEntity> ALLEGIANCE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "allegiance_arrow",
              AllegianceArrowEntity::new,
              AllegianceArrowEntity::new,
              ArrowSound.own(ModSounds.ALLEGIANCE_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<SmokeArrowEntity> SMOKE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.<SmokeArrowEntity>of(
                  "smoke_arrow",
                  SmokeArrowEntity::new,
                  SmokeArrowEntity::new,
                  ArrowSound.own(ModSounds.SMOKE_ARROW_IMPACT.getId()))
              .applying(ArrowEffect.overArea(StatusEffects.BLINDNESS)));

  public static final RegisteredArrow<DisarmArrowEntity> DISARM_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "disarm_arrow",
              DisarmArrowEntity::new,
              DisarmArrowEntity::new,
              ArrowSound.own(ModSounds.DISARM_ARROW_IMPACT.getId())));

  public static final RegisteredArrow<PaintArrowEntity> PAINT_ARROW =
      REGISTRAR.register(
          ArrowDefinition.tinted(
              "paint_arrow", PaintArrowEntity::new, PaintArrowEntity::new, DyePalette.create()));

  public static final RegisteredArrow<DrillArrowEntity> DRILL_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "drill_arrow",
              DrillArrowEntity::new,
              DrillArrowEntity::new,
              ArrowSound.own(ModSounds.DRILL_ARROW_BORE.getId())));

  public static final RegisteredArrow<PillarArrowEntity> PILLAR_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "pillar_arrow",
              PillarArrowEntity::new,
              PillarArrowEntity::new,
              ArrowSound.own(ModSounds.PILLAR_ARROW_RISE.getId())));

  public static final RegisteredArrow<DrainArrowEntity> DRAIN_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "drain_arrow",
              DrainArrowEntity::new,
              DrainArrowEntity::new,
              ArrowSound.own(ModSounds.DRAIN_ARROW_ABSORB.getId())));

  public static final RegisteredArrow<FreezeArrowEntity> FREEZE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of(
              "freeze_arrow",
              FreezeArrowEntity::new,
              FreezeArrowEntity::new,
              ArrowSound.own(ModSounds.FREEZE_ARROW_FREEZE.getId())));

  public static final RegisteredArrow<WebArrowEntity> WEB_ARROW =
      REGISTRAR.register(ArrowDefinition.of("web_arrow", WebArrowEntity::new, WebArrowEntity::new));

  static {
    registerLaterFamiliesInOrder();
  }

  private ModArrows() {}

  public static void register() {
    Volley.LOGGER.info("Registered {} arrow types", REGISTRAR.registrations().size());
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
}
