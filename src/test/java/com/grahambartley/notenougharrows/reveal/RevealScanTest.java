package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.world.BlockOrder;
import java.util.List;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class RevealScanTest {
  private static final BlockPos CENTER = new BlockPos(100, 64, -40);

  @Test
  void findsOnlyWhatItIsAskedToReveal() {
    final Set<BlockPos> ore = Set.of(CENTER.east(2), CENTER.down(3));

    final List<BlockPos> found = RevealScan.matching(CENTER, 4, 100, pos -> true, ore::contains);

    assertEquals(Set.of(CENTER.east(2), CENTER.down(3)), Set.copyOf(found));
  }

  @Test
  void neverLooksBeyondTheRadius() {
    final List<BlockPos> found = RevealScan.matching(CENTER, 3, 1000, pos -> true, pos -> true);

    assertTrue(found.stream().allMatch(pos -> BlockOrder.squaredDistance(CENTER, pos) <= 9));
    assertTrue(found.contains(CENTER.up(3)));
  }

  @Test
  void anOreJustPastTheRadiusIsNotRevealed() {
    final List<BlockPos> found =
        RevealScan.matching(CENTER, 3, 100, pos -> true, pos -> pos.equals(CENTER.up(4)));

    assertTrue(found.isEmpty());
  }

  @Test
  void skipsPositionsWhoseGroundIsNotLoaded() {
    final List<BlockPos> found =
        RevealScan.matching(CENTER, 4, 100, pos -> pos.getX() <= CENTER.getX(), pos -> true);

    assertTrue(found.stream().noneMatch(pos -> pos.getX() > CENTER.getX()));
  }

  @Test
  void neverAsksAboutAnUnloadedPosition() {
    RevealScan.matching(
        CENTER,
        4,
        100,
        pos -> pos.getY() >= CENTER.getY(),
        pos -> {
          assertTrue(pos.getY() >= CENTER.getY(), "Read an unloaded position " + pos);
          return true;
        });
  }

  @Test
  void stopsAtTheLimitKeepingTheNearest() {
    final List<BlockPos> found = RevealScan.matching(CENTER, 5, 7, pos -> true, pos -> true);

    assertEquals(7, found.size());
    assertEquals(CENTER, found.getFirst());
    assertTrue(found.stream().allMatch(pos -> BlockOrder.squaredDistance(CENTER, pos) <= 1));
  }

  @Test
  void aRadiusOfZeroLooksAtTheCentreAlone() {
    assertEquals(List.of(CENTER), RevealScan.matching(CENTER, 0, 10, pos -> true, pos -> true));
  }

  @Test
  void refusesNonsense() {
    assertTrue(RevealScan.matching(null, 3, 10, pos -> true, pos -> true).isEmpty());
    assertTrue(RevealScan.matching(CENTER, -1, 10, pos -> true, pos -> true).isEmpty());
    assertTrue(RevealScan.matching(CENTER, 3, 0, pos -> true, pos -> true).isEmpty());
  }
}
