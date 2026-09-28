package com.grahambartley.notenougharrows.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BridgeLineTest {
  private static final double EPSILON = 1.0E-9;
  private static final BlockPos STRUCK = new BlockPos(10, 70, 10);

  @Test
  void aStraightWalkwayRunsBackTowardTheShooterStartingBesideTheStruckBlock() {
    assertEquals(
        List.of(new BlockPos(9, 70, 10), new BlockPos(8, 70, 10), new BlockPos(7, 70, 10)),
        BridgeLine.toward(STRUCK, 0.5, 10.5, 3));
  }

  @Test
  void theWalkwayStaysLevelWithTheStruckBlock() {
    for (final BlockPos pos : BridgeLine.toward(STRUCK, -20.0, 30.0, 16)) {
      assertEquals(STRUCK.getY(), pos.getY());
    }
  }

  @Test
  void theWalkwayEndsBeneathTheShooterRatherThanRunningPastThem() {
    final List<BlockPos> walkway = BridgeLine.toward(STRUCK, 6.5, 10.5, 16);

    assertEquals(4, walkway.size());
    assertEquals(new BlockPos(6, 70, 10), walkway.getLast());
  }

  @Test
  void theWalkwayIsCutAtItsLength() {
    assertEquals(5, BridgeLine.toward(STRUCK, -40.0, 10.5, 5).size());
  }

  @ParameterizedTest
  @CsvSource({"-3.2, 17.9", "25.0, -4.0", "10.6, 30.0", "0.0, 0.0"})
  void everyPlankSharesAnEdgeWithTheLastSoTheWalkwayCanBeWalked(final double x, final double z) {
    final List<BlockPos> walk = new ArrayList<>();
    walk.add(STRUCK);
    walk.addAll(BridgeLine.toward(STRUCK, x, z, 32));

    for (int i = 1; i < walk.size(); i++) {
      assertEquals(1, walk.get(i).getManhattanDistance(walk.get(i - 1)), "plank " + i);
    }
  }

  @Test
  void aShooterStandingOnTheStruckBlockGetsNoWalkway() {
    assertTrue(BridgeLine.toward(STRUCK, 10.5, 10.5, 16).isEmpty());
  }

  @Test
  void noLengthOrNoStartMeansNoWalkway() {
    assertTrue(BridgeLine.toward(STRUCK, 0.0, 0.0, 0).isEmpty());
    assertTrue(BridgeLine.toward(null, 0.0, 0.0, 4).isEmpty());
  }

  @Test
  void anArrowWithNoShooterPointsBackTheWayItFlew() {
    final Vec3d impact = new Vec3d(10.0, 70.0, 10.0);

    final Vec3d back = BridgeLine.backAlong(impact, new Vec3d(2.0, -1.0, 0.0), 4);

    assertEquals(5.0, back.getX(), EPSILON);
    assertEquals(10.0, back.getZ(), EPSILON);
  }

  @Test
  void anArrowFallingStraightDownHasNoWayBack() {
    final Vec3d impact = new Vec3d(10.0, 70.0, 10.0);

    assertEquals(impact, BridgeLine.backAlong(impact, new Vec3d(0.0, -3.0, 0.0), 4));
  }
}
