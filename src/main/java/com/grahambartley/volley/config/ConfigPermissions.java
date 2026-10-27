package com.grahambartley.volley.config;

import java.util.function.IntPredicate;

public final class ConfigPermissions {
  public static final int OPERATOR_LEVEL = 2;

  private ConfigPermissions() {}

  public static boolean canEdit(final IntPredicate hasPermissionLevel) {
    return hasPermissionLevel.test(OPERATOR_LEVEL);
  }
}
