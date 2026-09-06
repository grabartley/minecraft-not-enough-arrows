package com.grahambartley.morearrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.anchor.BlockAnchor;
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
  private static final BlockPos ANCHOR = new BlockPos(4, 64, -2);
  private static final Identifier STONE = Identifier.ofVanilla("stone");
  private static final int LIFETIME_TICKS = 40;

  @ParameterizedTest
  @CsvSource({"2, false", "1, false", "0, true"})
  void aSessionEndsOnceItHasRunOutOfTicks(final int remainingTicks, final boolean expired) {
    assertEquals(expired, session(remainingTicks).hasExpired());
  }

  @Test
  void eachPullSpendsOneTick() {
    assertEquals(LIFETIME_TICKS - 1, session(LIFETIME_TICKS).pulled().remainingTicks());
  }

  @Test
  void aSessionOnItsLastTickCannotBePulledPastTheEnd() {
    assertTrue(session(1).pulled().hasExpired());
    assertEquals(0, session(0).pulled().remainingTicks());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -40})
  void aSessionThatCannotOutliveItsStartIsBornExpired(final int lifetimeTicks) {
    assertTrue(session(lifetimeTicks).hasExpired());
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
        NullPointerException.class, () -> new GrappleSession(null, ANCHOR, LIFETIME_TICKS));
  }

  @Test
  void aSessionWithoutAnAnchorIsRejected() {
    assertThrows(
        NullPointerException.class, () -> new GrappleSession(PLAYER, null, LIFETIME_TICKS));
  }

  @Test
  void aSessionKeepsItsAnchorWhenTheCallersCursorMovesOn() {
    final BlockPos.Mutable cursor = new BlockPos.Mutable(4, 64, -2);
    final GrappleSession session = new GrappleSession(PLAYER, cursor, LIFETIME_TICKS);

    cursor.set(9, 9, 9);

    assertEquals(ANCHOR, session.anchor());
  }

  private static GrappleSession session(final int remainingTicks) {
    return new GrappleSession(PLAYER, ANCHOR, remainingTicks);
  }

  private static BlockAnchor anchorAt(final BlockPos pos) {
    return new BlockAnchor(PLAYER, pos, STONE, 100L);
  }
}
