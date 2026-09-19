package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;

public final class ServerConfigOptions {
  private static final List<ConfigSection<NotEnoughArrowsConfig>> SECTIONS =
      List.of(
          ExplosiveOptions.section(),
          GrappleOptions.section(),
          UtilityOptions.section(),
          PhysicsOptions.section(),
          EnderOptions.section(),
          CombatOptions.section(),
          FletchingOptions.section());
  private static final List<ConfigOption<NotEnoughArrowsConfig>> ALL =
      SECTIONS.stream().flatMap(section -> section.options().stream()).toList();

  private ServerConfigOptions() {}

  public static List<ConfigSection<NotEnoughArrowsConfig>> sections() {
    return SECTIONS;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> all() {
    return ALL;
  }
}
