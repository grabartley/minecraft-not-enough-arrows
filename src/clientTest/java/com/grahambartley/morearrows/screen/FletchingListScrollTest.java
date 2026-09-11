package com.grahambartley.morearrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class FletchingListScrollTest {
  private static final List<String> NINE = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i");
  private static final List<String> THREE = List.of("a", "b", "c");

  private static final int NINE_COUNT = 9;
  private static final int THREE_COUNT = 3;
  private static final int TRACK_TOP = 0;
  private static final double BOTTOM_OF_TRACK = 100.0;
  private static final double MIDDLE_OF_TRACK = 27.0;

  @Test
  void aFreshListStartsAtTheTopAndIsNotBeingDragged() {
    final FletchingListScroll scroll = new FletchingListScroll();

    assertEquals(0f, scroll.amount(NINE_COUNT));
    assertFalse(scroll.dragging());
  }

  @Test
  void aListThatCannotScrollReportsNoScrollHoweverFarItWasPushed() {
    final FletchingListScroll scroll = scrolledToTheBottom();

    assertEquals(0f, scroll.amount(THREE_COUNT));
  }

  @Test
  void theSameRecipesArrivingAgainLeaveTheScrollWhereThePlayerPutIt() {
    final FletchingListScroll scroll = scrolledToTheBottom();

    assertFalse(scroll.follow(List.copyOf(NINE)));
    assertEquals(1f, scroll.amount(NINE_COUNT));
  }

  @Test
  void aDifferentSetOfRecipesSendsTheListBackToItsFirstRow() {
    final FletchingListScroll scroll = scrolledToTheBottom();

    assertTrue(scroll.follow(THREE));
    assertEquals(0f, scroll.amount(NINE_COUNT));
  }

  @Test
  void recipesChangingUnderADragEndsThatDragRatherThanLettingItCarryOn() {
    final FletchingListScroll scroll = new FletchingListScroll();
    scroll.follow(NINE);
    scroll.startDrag(NINE_COUNT, MIDDLE_OF_TRACK, TRACK_TOP);
    assertTrue(scroll.dragging());

    scroll.follow(THREE);

    assertFalse(scroll.dragging());
    assertFalse(scroll.drag(NINE_COUNT, BOTTOM_OF_TRACK, TRACK_TOP));
    assertEquals(0f, scroll.amount(NINE_COUNT));
  }

  @Test
  void draggingIsIgnoredUntilADragHasActuallyStarted() {
    final FletchingListScroll scroll = new FletchingListScroll();
    scroll.follow(NINE);

    assertFalse(scroll.drag(NINE_COUNT, BOTTOM_OF_TRACK, TRACK_TOP));
    assertEquals(0f, scroll.amount(NINE_COUNT));
  }

  @Test
  void aDragThatHasStartedMovesTheListAndReleasingEndsIt() {
    final FletchingListScroll scroll = new FletchingListScroll();
    scroll.follow(NINE);

    assertTrue(scroll.startDrag(NINE_COUNT, TRACK_TOP, TRACK_TOP));
    assertTrue(scroll.drag(NINE_COUNT, BOTTOM_OF_TRACK, TRACK_TOP));
    assertEquals(1f, scroll.amount(NINE_COUNT));

    scroll.endDrag();

    assertFalse(scroll.dragging());
    assertFalse(scroll.drag(NINE_COUNT, TRACK_TOP, TRACK_TOP));
  }

  @Test
  void aListWithNowhereToScrollRefusesBothTheWheelAndTheScroller() {
    final FletchingListScroll scroll = new FletchingListScroll();
    scroll.follow(THREE);

    assertFalse(scroll.wheel(THREE_COUNT, -1.0));
    assertFalse(scroll.startDrag(THREE_COUNT, BOTTOM_OF_TRACK, TRACK_TOP));
    assertFalse(scroll.dragging());
  }

  @Test
  void theWheelWalksTheListOneRowAtATimeAndStopsAtTheEnd() {
    final FletchingListScroll scroll = new FletchingListScroll();
    scroll.follow(NINE);

    assertTrue(scroll.wheel(NINE_COUNT, -1.0));
    assertEquals(1, FletchingListGeometry.topRow(NINE_COUNT, scroll.amount(NINE_COUNT)));

    for (int notch = 0; notch < NINE_COUNT; notch++) {
      scroll.wheel(NINE_COUNT, -1.0);
    }

    assertEquals(1f, scroll.amount(NINE_COUNT));
    assertEquals(
        FletchingListGeometry.hiddenRows(NINE_COUNT),
        FletchingListGeometry.topRow(NINE_COUNT, scroll.amount(NINE_COUNT)));
  }

  @Test
  void aScrollHeldPastTheTopComesBackToTheTopRatherThanGoingNegative() {
    final FletchingListScroll scroll = new FletchingListScroll();
    scroll.follow(NINE);

    scroll.wheel(NINE_COUNT, 5.0);

    assertEquals(0f, scroll.amount(NINE_COUNT));
  }

  private static FletchingListScroll scrolledToTheBottom() {
    final FletchingListScroll scroll = new FletchingListScroll();
    scroll.follow(NINE);
    scroll.startDrag(NINE_COUNT, BOTTOM_OF_TRACK, TRACK_TOP);
    scroll.endDrag();
    return scroll;
  }
}
