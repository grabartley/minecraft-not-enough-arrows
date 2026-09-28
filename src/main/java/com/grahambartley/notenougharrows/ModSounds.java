package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.audio.SoundRegistrar;
import java.util.List;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
  private static final SoundRegistrar REGISTRAR = new SoundRegistrar();

  public static final SoundEvent COUNTDOWN_BEEP = REGISTRAR.declare("countdown_beep");
  public static final Identifier COUNTDOWN_BEEP_ID = COUNTDOWN_BEEP.getId();

  public static final SoundEvent SMOKE_ARROW_IMPACT = REGISTRAR.declare("smoke_arrow_impact");
  public static final SoundEvent REPEL_ARROW_IMPACT = REGISTRAR.declare("repel_arrow_impact");
  public static final SoundEvent TAUNT_ARROW_IMPACT = REGISTRAR.declare("taunt_arrow_impact");
  public static final SoundEvent DISARM_ARROW_IMPACT = REGISTRAR.declare("disarm_arrow_impact");
  public static final SoundEvent ALLEGIANCE_ARROW_IMPACT =
      REGISTRAR.declare("allegiance_arrow_impact");
  public static final SoundEvent RICOCHET_ARROW_BOUNCE = REGISTRAR.declare("ricochet_arrow_bounce");
  public static final SoundEvent ENDER_TELEPORT = REGISTRAR.declare("ender_teleport");
  public static final SoundEvent FROST_ARROW_FREEZE_CRACK =
      REGISTRAR.declare("frost_arrow_freeze_crack");
  public static final SoundEvent FROST_ARROW_FREEZE_SETTLE =
      REGISTRAR.declare("frost_arrow_freeze_settle");
  public static final SoundEvent FROST_ARROW_THAW = REGISTRAR.declare("frost_arrow_thaw");
  public static final SoundEvent EXPLOSIVE_ARROW_BLAST = REGISTRAR.declare("explosive_arrow_blast");
  public static final SoundEvent WIND_ARROW_BURST = REGISTRAR.declare("wind_arrow_burst");
  public static final SoundEvent SHOCK_ARROW_THUNDER = REGISTRAR.declare("shock_arrow_thunder");
  public static final SoundEvent SHOCK_ARROW_IMPACT = REGISTRAR.declare("shock_arrow_impact");
  public static final SoundEvent DRILL_ARROW_BORE = REGISTRAR.declare("drill_arrow_bore");
  public static final SoundEvent PILLAR_ARROW_RISE = REGISTRAR.declare("pillar_arrow_rise");
  public static final SoundEvent DRAIN_ARROW_ABSORB = REGISTRAR.declare("drain_arrow_absorb");
  public static final SoundEvent FREEZE_ARROW_FREEZE = REGISTRAR.declare("freeze_arrow_freeze");
  public static final SoundEvent BLOSSOM_ARROW_BLOOM = REGISTRAR.declare("blossom_arrow_bloom");
  public static final SoundEvent HARVEST_ARROW_REAP = REGISTRAR.declare("harvest_arrow_reap");
  public static final SoundEvent TILL_ARROW_TILL = REGISTRAR.declare("till_arrow_till");
  public static final SoundEvent SHEAR_ARROW_CARVE = REGISTRAR.declare("shear_arrow_carve");
  public static final SoundEvent SHEAR_ARROW_HIVE = REGISTRAR.declare("shear_arrow_hive");
  public static final SoundEvent BEE_ARROW_RELEASE = REGISTRAR.declare("bee_arrow_release");
  public static final SoundEvent FLETCHING_STATION_SELECT =
      REGISTRAR.declare("fletching_station_select");

  private ModSounds() {}

  public static List<Identifier> declared() {
    return REGISTRAR.ids();
  }

  public static void register() {
    REGISTRAR.registerInto((id, event) -> Registry.register(Registries.SOUND_EVENT, id, event));
    NotEnoughArrows.LOGGER.info("Registered {} sound events", REGISTRAR.ids().size());
  }
}
