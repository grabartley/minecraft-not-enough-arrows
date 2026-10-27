package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

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
}
