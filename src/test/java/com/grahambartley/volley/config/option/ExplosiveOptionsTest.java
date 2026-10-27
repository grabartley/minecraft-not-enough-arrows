package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExplosiveOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "explosive.gunpowder.delayTicks",
            "explosive.gunpowder.power",
            "explosive.tnt.delayTicks",
            "explosive.tnt.power",
            "explosive.fireCharge.delayTicks",
            "explosive.fireCharge.power",
            "explosive.damageTerrain",
            "explosive.damageEntities",
            "explosive.firePatchRadius",
            "explosive.firePatchDurationTicks",
            "explosive.beepVolume",
            "explosive.incendiary.burnRadius",
            "explosive.incendiary.igniteSeconds",
            "explosive.incendiary.ignitesBlocks"),
        ExplosiveOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(ExplosiveOptions.options(), ExplosiveOptions.section().options());
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = {"explosive.beepVolume"})
  void stepsInTheSameIncrementAsEveryOtherSettingOfItsKind(final String id) {
    final FloatOption<?> option =
        (FloatOption<?>)
            ExplosiveOptions.options().stream()
                .filter(o -> o.id().equals(id))
                .findFirst()
                .orElseThrow();

    assertEquals(0.05f, option.step());
  }
}
