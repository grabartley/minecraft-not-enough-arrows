package com.grahambartley.morearrows.config.option;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import java.util.List;

public final class ServerConfigOptions {
  private static final List<ConfigSection<MoreArrowsConfig>> SECTIONS =
      List.of(
          ExplosiveOptions.section(),
          GrappleOptions.section(),
          UtilityOptions.section(),
          PhysicsOptions.section());
  private static final List<ConfigOption<MoreArrowsConfig>> ALL =
      SECTIONS.stream().flatMap(section -> section.options().stream()).toList();

  private ServerConfigOptions() {}

  public static List<ConfigSection<MoreArrowsConfig>> sections() {
    return SECTIONS;
  }

  public static List<ConfigOption<MoreArrowsConfig>> all() {
    return ALL;
  }
}
