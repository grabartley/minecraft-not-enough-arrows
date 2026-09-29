package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.audio.SoundRegistrar;
import java.util.List;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
  public static final int LANDING_RANGE_BLOCKS = 48;

  private static final SoundRegistrar REGISTRAR = new SoundRegistrar();

  public static final SoundEvent COUNTDOWN_BEEP =
      REGISTRAR.declareReaching("countdown_beep", LANDING_RANGE_BLOCKS);
  public static final Identifier COUNTDOWN_BEEP_ID = COUNTDOWN_BEEP.getId();

  public static final SoundEvent SMOKE_ARROW_IMPACT =
      REGISTRAR.declareReaching("smoke_arrow_impact", LANDING_RANGE_BLOCKS);
  public static final SoundEvent REPEL_ARROW_IMPACT =
      REGISTRAR.declareReaching("repel_arrow_impact", LANDING_RANGE_BLOCKS);
  public static final SoundEvent TAUNT_ARROW_IMPACT =
      REGISTRAR.declareReaching("taunt_arrow_impact", LANDING_RANGE_BLOCKS);
  public static final SoundEvent DISARM_ARROW_IMPACT =
      REGISTRAR.declareReaching("disarm_arrow_impact", LANDING_RANGE_BLOCKS);
  public static final SoundEvent ALLEGIANCE_ARROW_IMPACT =
      REGISTRAR.declareReaching("allegiance_arrow_impact", LANDING_RANGE_BLOCKS);
  public static final SoundEvent RICOCHET_ARROW_BOUNCE = REGISTRAR.declare("ricochet_arrow_bounce");
  public static final SoundEvent ENDER_TELEPORT = REGISTRAR.declare("ender_teleport");
  public static final SoundEvent FROST_ARROW_FREEZE_CRACK =
      REGISTRAR.declareReaching("frost_arrow_freeze_crack", LANDING_RANGE_BLOCKS);
  public static final SoundEvent FROST_ARROW_FREEZE_SETTLE =
      REGISTRAR.declareReaching("frost_arrow_freeze_settle", LANDING_RANGE_BLOCKS);
  public static final SoundEvent FROST_ARROW_THAW =
      REGISTRAR.declareReaching("frost_arrow_thaw", LANDING_RANGE_BLOCKS);
  public static final SoundEvent EXPLOSIVE_ARROW_BLAST = REGISTRAR.declare("explosive_arrow_blast");
  public static final SoundEvent WIND_ARROW_BURST = REGISTRAR.declare("wind_arrow_burst");
  public static final SoundEvent SHOCK_ARROW_THUNDER = REGISTRAR.declare("shock_arrow_thunder");
  public static final SoundEvent SHOCK_ARROW_IMPACT = REGISTRAR.declare("shock_arrow_impact");
  public static final SoundEvent LIFESTEAL_ARROW_DRAIN =
      REGISTRAR.declareReaching("lifesteal_arrow_drain", LANDING_RANGE_BLOCKS);
  public static final SoundEvent MILK_ARROW_WASH =
      REGISTRAR.declareReaching("milk_arrow_wash", LANDING_RANGE_BLOCKS);
  public static final SoundEvent VOLLEY_ARROW_SPLIT = REGISTRAR.declare("volley_arrow_split");
  public static final SoundEvent DRILL_ARROW_BORE =
      REGISTRAR.declareReaching("drill_arrow_bore", LANDING_RANGE_BLOCKS);
  public static final SoundEvent PILLAR_ARROW_RISE =
      REGISTRAR.declareReaching("pillar_arrow_rise", LANDING_RANGE_BLOCKS);
  public static final SoundEvent DRAIN_ARROW_ABSORB =
      REGISTRAR.declareReaching("drain_arrow_absorb", LANDING_RANGE_BLOCKS);
  public static final SoundEvent FREEZE_ARROW_FREEZE =
      REGISTRAR.declareReaching("freeze_arrow_freeze", LANDING_RANGE_BLOCKS);
  public static final SoundEvent BLOSSOM_ARROW_BLOOM =
      REGISTRAR.declareReaching("blossom_arrow_bloom", LANDING_RANGE_BLOCKS);
  public static final SoundEvent HARVEST_ARROW_REAP =
      REGISTRAR.declareReaching("harvest_arrow_reap", LANDING_RANGE_BLOCKS);
  public static final SoundEvent TILL_ARROW_TILL =
      REGISTRAR.declareReaching("till_arrow_till", LANDING_RANGE_BLOCKS);
  public static final SoundEvent SHEAR_ARROW_CARVE =
      REGISTRAR.declareReaching("shear_arrow_carve", LANDING_RANGE_BLOCKS);
  public static final SoundEvent SHEAR_ARROW_HIVE =
      REGISTRAR.declareReaching("shear_arrow_hive", LANDING_RANGE_BLOCKS);
  public static final SoundEvent BEE_ARROW_RELEASE =
      REGISTRAR.declareReaching("bee_arrow_release", LANDING_RANGE_BLOCKS);
  public static final SoundEvent ZIPLINE_ARROW_STRING =
      REGISTRAR.declareReaching("zipline_arrow_string", LANDING_RANGE_BLOCKS);
  public static final SoundEvent UPDRAFT_ARROW_OPEN =
      REGISTRAR.declareReaching("updraft_arrow_open", LANDING_RANGE_BLOCKS);
  public static final SoundEvent TRAMPOLINE_ARROW_LAUNCH =
      REGISTRAR.declare("trampoline_arrow_launch");
  public static final SoundEvent BEACON_ARROW_RAISE =
      REGISTRAR.declareReaching("beacon_arrow_raise", LANDING_RANGE_BLOCKS);
  public static final SoundEvent PROSPECTOR_ARROW_PULSE =
      REGISTRAR.declareReaching("prospector_arrow_pulse", LANDING_RANGE_BLOCKS);
  public static final SoundEvent SONAR_ARROW_PULSE =
      REGISTRAR.declareReaching("sonar_arrow_pulse", LANDING_RANGE_BLOCKS);
  public static final SoundEvent TRIPWIRE_ARROW_SET =
      REGISTRAR.declareReaching("tripwire_arrow_set", LANDING_RANGE_BLOCKS);
  public static final SoundEvent TRIPWIRE_ARROW_ALERT = REGISTRAR.declare("tripwire_arrow_alert");
  public static final SoundEvent CHICKEN_ARROW_HATCH =
      REGISTRAR.declareReaching("chicken_arrow_hatch", LANDING_RANGE_BLOCKS);
  public static final SoundEvent PUFFER_ARROW_INFLATE =
      REGISTRAR.declareReaching("puffer_arrow_inflate", LANDING_RANGE_BLOCKS);
  public static final SoundEvent PUFFER_ARROW_DEFLATE =
      REGISTRAR.declareReaching("puffer_arrow_deflate", LANDING_RANGE_BLOCKS);
  public static final SoundEvent STINK_ARROW_RELEASE =
      REGISTRAR.declareReaching("stink_arrow_release", LANDING_RANGE_BLOCKS);
  public static final SoundEvent BOOMERANG_ARROW_RETURN =
      REGISTRAR.declare("boomerang_arrow_return");
  public static final SoundEvent POLYMORPH_ARROW_CHANGE =
      REGISTRAR.declareReaching("polymorph_arrow_change", LANDING_RANGE_BLOCKS);
  public static final SoundEvent POLYMORPH_ARROW_RESTORE =
      REGISTRAR.declareReaching("polymorph_arrow_restore", LANDING_RANGE_BLOCKS);
  public static final SoundEvent COURIER_ARROW_DELIVER =
      REGISTRAR.declareReaching("courier_arrow_deliver", LANDING_RANGE_BLOCKS);
  public static final SoundEvent SNOW_GOLEM_ARROW_MELT =
      REGISTRAR.declareReaching("snow_golem_arrow_melt", LANDING_RANGE_BLOCKS);
  public static final SoundEvent MAGNET_ARROW_PULL =
      REGISTRAR.declareReaching("magnet_arrow_pull", LANDING_RANGE_BLOCKS);
  public static final SoundEvent FLETCHING_STATION_SELECT =
      REGISTRAR.declare("fletching_station_select");

  private ModSounds() {}

  public static List<Identifier> declared() {
    return REGISTRAR.ids();
  }

  public static List<SoundEvent> declaredEvents() {
    return REGISTRAR.events();
  }

  public static void register() {
    REGISTRAR.registerInto((id, event) -> Registry.register(Registries.SOUND_EVENT, id, event));
    NotEnoughArrows.LOGGER.info("Registered {} sound events", REGISTRAR.ids().size());
  }
}
