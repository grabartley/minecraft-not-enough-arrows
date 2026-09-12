package com.grahambartley.notenougharrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.anchor.BlockAnchor;
import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class GrappleSessionTest {
  private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
  private static final UUID ARROW = UUID.fromString("00000000-0000-0000-0000-0000000000b1");
  private static final BlockPos ANCHOR = new BlockPos(4, 64, -2);
  private static final Identifier STONE = Identifier.ofVanilla("stone");
  private static final int LIFETIME_TICKS = 40;
  private static final double STARTING_DISTANCE = 20.0;

  @ParameterizedTest
  @CsvSource({"2, false", "1, false", "0, true"})
  void aSessionEndsOnceItHasRunOutOfTicks(final int remainingTicks, final boolean expired) {
    assertEquals(expired, session(remainingTicks).hasExpired());
  }

  @Test
  void eachPullSpendsOneTick() {
    assertEquals(
        LIFETIME_TICKS - 1, session(LIFETIME_TICKS).pulled(STARTING_DISTANCE).remainingTicks());
  }

  @Test
  void aSessionCountsHowLongItHasBeenPulling() {
    assertEquals(0, session(LIFETIME_TICKS).pulledTicks());
    assertEquals(
        3,
        session(LIFETIME_TICKS)
            .pulled(STARTING_DISTANCE)
            .pulled(STARTING_DISTANCE)
            .pulled(STARTING_DISTANCE)
            .pulledTicks());
  }

  @Test
  void aSessionOnItsLastTickCannotBePulledPastTheEnd() {
    assertTrue(session(1).pulled(STARTING_DISTANCE).hasExpired());
    assertEquals(0, session(0).pulled(STARTING_DISTANCE).remainingTicks());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -40})
  void aSessionThatCannotOutliveItsStartIsBornExpired(final int lifetimeTicks) {
    assertTrue(session(lifetimeTicks).hasExpired());
  }

  @Test
  void aSessionStartsOutHavingComeNoCloserThanWhereItBegan() {
    assertEquals(STARTING_DISTANCE, session(LIFETIME_TICKS).progress().closestApproach());
    assertFalse(session(LIFETIME_TICKS).hasStopped());
  }

  @Test
  void aSessionCarriesItsPullThroughToTheProgressItTracks() {
    assertEquals(18.0, session(LIFETIME_TICKS).pulled(18.0).progress().closestApproach());
  }

  @Test
  void aSessionThatStopsClosingForTheWholeWindowHasStopped() {
    GrappleSession session = session(LIFETIME_TICKS);
    for (int tick = 0; tick < GrappleProgress.IDLE_TICKS_LIMIT; tick++) {
      session = session.pulled(STARTING_DISTANCE);
    }

    assertTrue(session.hasStopped());
  }

  @Test
  void aSessionRemembersTheArrowItHangsFrom() {
    assertEquals(ARROW, session(LIFETIME_TICKS).pulled(STARTING_DISTANCE).arrowId());
  }

  @Test
  void aSessionWithNoArrowBehindItStillPulls() {
    final GrappleSession session =
        GrappleSession.beginning(PLAYER, null, ANCHOR, LIFETIME_TICKS, STARTING_DISTANCE);

    assertNull(session.arrowId());
    assertFalse(session.hasExpired());
  }

  @Test
  void aSessionPullsTowardTheCentreOfItsAnchorBlock() {
    assertEquals(new Vec3d(4.5, 64.5, -1.5), session(LIFETIME_TICKS).target());
  }

  @Test
  void aSessionHoldsOntoTheAnchorItWasStartedOn() {
    assertTrue(session(LIFETIME_TICKS).holdsOnto(anchorAt(ANCHOR)));
  }

  @Test
  void aSessionLosesItsGripWhenTheOwnerHoldsADifferentBlock() {
    assertFalse(session(LIFETIME_TICKS).holdsOnto(anchorAt(ANCHOR.up())));
  }

  @ParameterizedTest
  @NullSource
  void aSessionLosesItsGripWhenTheAnchorIsGone(final BlockAnchor held) {
    assertFalse(session(LIFETIME_TICKS).holdsOnto(held));
  }

  @Test
  void aSessionWithoutAPlayerIsRejected() {
    assertThrows(
        NullPointerException.class,
        () -> GrappleSession.beginning(null, ARROW, ANCHOR, LIFETIME_TICKS, STARTING_DISTANCE));
  }

  @Test
  void aSessionWithoutAnAnchorIsRejected() {
    assertThrows(
        NullPointerException.class,
        () -> GrappleSession.beginning(PLAYER, ARROW, null, LIFETIME_TICKS, STARTING_DISTANCE));
  }

  @Test
  void aSessionWithoutProgressBehindItIsRejected() {
    assertThrows(
        NullPointerException.class,
        () -> new GrappleSession(PLAYER, ARROW, ANCHOR, LIFETIME_TICKS, 0, null));
  }

  @Test
  void aSessionKeepsItsAnchorWhenTheCallersCursorMovesOn() {
    final BlockPos.Mutable cursor = new BlockPos.Mutable(4, 64, -2);
    final GrappleSession session =
        GrappleSession.beginning(PLAYER, ARROW, cursor, LIFETIME_TICKS, STARTING_DISTANCE);

    cursor.set(9, 9, 9);

    assertEquals(ANCHOR, session.anchor());
  }

  private static GrappleSession session(final int remainingTicks) {
    return GrappleSession.beginning(PLAYER, ARROW, ANCHOR, remainingTicks, STARTING_DISTANCE);
  }

  private static BlockAnchor anchorAt(final BlockPos pos) {
    return new BlockAnchor(PLAYER, pos, STONE, 100L);
  }
}
