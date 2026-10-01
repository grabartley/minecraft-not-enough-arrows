package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class OpenAirCourseTest {
  private static final Vec3d FROM = new Vec3d(10.5, 6.5, 2.5);
  private static final Predicate<BlockPos> OPEN_SKY = pos -> true;
  private static final Predicate<BlockPos> WALL_BELOW_Z_TWO = pos -> pos.getZ() >= 2;

  @Test
  void flyStraightAtAnOpenDestination() {
    assertEquals(
        new BlockPos(22, 6, 0), OpenAirCourse.toward(FROM, new Vec3d(22.5, 6.5, 0.5), OPEN_SKY));
  }

  @Test
  void stopsAtTheLastOpenBlockBeforeAWall() {
    final Predicate<BlockPos> wallAtSixteen = pos -> pos.getX() < 16;
    assertEquals(
        new BlockPos(15, 6, 2),
        OpenAirCourse.toward(FROM, new Vec3d(22.5, 6.5, 2.5), wallAtSixteen));
  }

  @Test
  void slidesAlongAWallTheStraightLineRunsInto() {
    assertEquals(
        new BlockPos(22, 6, 2),
        OpenAirCourse.toward(FROM, new Vec3d(22.5, 6.5, -1.5), WALL_BELOW_Z_TWO));
  }

  @Test
  void slidesAcrossWhenTheWallRunsTheOtherWay() {
    final Predicate<BlockPos> wallPastXTen = pos -> pos.getX() <= 10;
    assertEquals(
        new BlockPos(10, 6, 14),
        OpenAirCourse.toward(FROM, new Vec3d(13.5, 6.5, 14.5), wallPastXTen));
  }

  @Test
  void keepsTheStraightLineWhenItReachesFurtherThanASlide() {
    final Predicate<BlockPos> slidesBlockedSoon =
        pos ->
            !(pos.getZ() == 2 && pos.getX() > 11)
                && !(pos.getX() == 10 && pos.getZ() < 2)
                && pos.getX() < 18;
    assertEquals(
        new BlockPos(17, 6, 0),
        OpenAirCourse.toward(FROM, new Vec3d(22.5, 6.5, -1.5), slidesBlockedSoon));
  }

  @Test
  void holdsItsOwnBlockWhenBoxedIn() {
    assertEquals(
        BlockPos.ofFloored(FROM),
        OpenAirCourse.toward(FROM, new Vec3d(22.5, 6.5, -1.5), pos -> false));
  }
}
