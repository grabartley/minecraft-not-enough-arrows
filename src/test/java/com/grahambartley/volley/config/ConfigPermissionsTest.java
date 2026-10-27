package com.grahambartley.volley.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConfigPermissionsTest {

  @ParameterizedTest(name = "permission level {0} may edit = {1}")
  @CsvSource({"0, false", "1, false", "2, true", "3, true", "4, true"})
  void onlyOperatorsAtLevelTwoOrAboveMayEdit(final int level, final boolean mayEdit) {
    assertEquals(mayEdit, ConfigPermissions.canEdit(required -> level >= required));
  }
}
