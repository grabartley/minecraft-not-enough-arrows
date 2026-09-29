package com.grahambartley.notenougharrows.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.block.BlockAudit.BlockFacts;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class BlockAuditTest {
  private static final Identifier ROPE = Identifier.of(NotEnoughArrows.MOD_ID, "rope");
  private static final Identifier BEAM = Identifier.of(NotEnoughArrows.MOD_ID, "beacon_beam");

  private static BlockFacts expiring(final Identifier block) {
    return new BlockFacts(block, false, false, false, Set.of(BlockLifetime.EXPIRES), true);
  }

  @Test
  void passesABlockThatExpiresAndLeavesNothingBehind() {
    assertTrue(BlockAudit.violations(List.of(expiring(BEAM))).isEmpty());
  }

  @Test
  void passesABlockThatFallsWithoutSupport() {
    assertTrue(
        BlockAudit.violations(
                List.of(
                    new BlockFacts(
                        ROPE,
                        false,
                        false,
                        false,
                        Set.of(BlockLifetime.FALLS_WITHOUT_SUPPORT),
                        false)))
            .isEmpty());
  }

  @Test
  void failsABlockWithAnItemForm() {
    assertEquals(
        List.of(BEAM + " has an item form"),
        BlockAudit.violations(
            List.of(
                new BlockFacts(BEAM, true, false, false, Set.of(BlockLifetime.EXPIRES), true))));
  }

  @Test
  void failsABlockWithARecipe() {
    assertEquals(
        List.of(BEAM + " has a recipe"),
        BlockAudit.violations(
            List.of(
                new BlockFacts(BEAM, false, true, false, Set.of(BlockLifetime.EXPIRES), true))));
  }

  @Test
  void failsABlockThatDropsItems() {
    assertEquals(
        List.of(BEAM + " drops items when broken"),
        BlockAudit.violations(
            List.of(
                new BlockFacts(BEAM, false, false, true, Set.of(BlockLifetime.EXPIRES), true))));
  }

  @Test
  void failsABlockThatNeitherExpiresNorFalls() {
    assertEquals(
        List.of(BEAM + " neither expires nor answers for its own support"),
        BlockAudit.violations(List.of(new BlockFacts(BEAM, false, false, false, Set.of(), true))));
  }

  @Test
  void failsABlockThatClaimsToFallButStandsInOpenAir() {
    assertEquals(
        List.of(ROPE + " claims to fall without support but stands in open air"),
        BlockAudit.violations(
            List.of(
                new BlockFacts(
                    ROPE,
                    false,
                    false,
                    false,
                    Set.of(BlockLifetime.FALLS_WITHOUT_SUPPORT),
                    true))));
  }

  @Test
  void anExpiringBlockMayStandInOpenAir() {
    assertTrue(BlockAudit.violations(List.of(expiring(BEAM))).isEmpty());
  }

  @Test
  void reportsEveryRuleABlockBreaks() {
    assertEquals(
        4,
        BlockAudit.violations(List.of(new BlockFacts(BEAM, true, true, true, Set.of(), true)))
            .size());
  }

  @Test
  void rejectsNullInputs() {
    assertThrows(NullPointerException.class, () -> BlockAudit.violations(null));
    assertThrows(
        NullPointerException.class,
        () -> new BlockFacts(null, false, false, false, Set.of(), false));
    assertThrows(
        NullPointerException.class, () -> new BlockFacts(BEAM, false, false, false, null, false));
  }
}
