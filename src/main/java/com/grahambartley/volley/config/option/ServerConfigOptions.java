package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;

public final class ServerConfigOptions {
  private static final List<ConfigSection<VolleyConfig>> SECTIONS =
      List.of(
          ExplosiveOptions.section(),
          GrappleOptions.section(),
          UtilityOptions.section(),
          PhysicsOptions.section(),
          EnderOptions.section(),
          CombatOptions.section(),
          ControlOptions.section(),
          TraversalOptions.section(),
          TerrainOptions.section(),
          AgricultureOptions.section(),
          DiscoveryOptions.section(),
          ChaosOptions.section(),
          SocialOptions.section(),
          FletchingOptions.section(),
          SoundOptions.section());
  private static final List<ConfigOption<VolleyConfig>> ALL =
      SECTIONS.stream().flatMap(section -> section.options().stream()).toList();

  private ServerConfigOptions() {}

  public static List<ConfigSection<VolleyConfig>> sections() {
    return SECTIONS;
  }

  public static List<ConfigOption<VolleyConfig>> all() {
    return ALL;
  }
}
