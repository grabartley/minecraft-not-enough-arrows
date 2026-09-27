package com.grahambartley.notenougharrows.gametest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class MobRosterGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = "mob-roster")
  public void theRosterNamesEveryMobInTheGame(TestContext context) {
    final Set<EntityType<?>> listed =
        MobRoster.EVERY_MOB.stream().map(MobRoster.Mob::type).collect(Collectors.toSet());
    final List<String> missing = new ArrayList<>();
    for (final EntityType<?> type : Registries.ENTITY_TYPE) {
      final Entity probe = type.create(context.getWorld());
      if (probe instanceof MobEntity && !listed.contains(type)) {
        missing.add(EntityType.getId(type).toString());
      }
      if (probe != null) {
        probe.discard();
      }
    }
    context.assertTrue(
        missing.isEmpty(),
        "Every mob must be in the roster the per-arrow tests run over, but these are missing: "
            + missing);
    context.complete();
  }
}
