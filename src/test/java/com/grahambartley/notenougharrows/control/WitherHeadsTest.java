package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class WitherHeadsTest {
  private static final int DEFENDED = 42;
  private static final int STRANGER = 7;

  @Test
  void findsEverySideHeadAimedAtWhoItDefends() {
    assertEquals(List.of(1, 2), WitherHeads.aimedAt(head -> DEFENDED, DEFENDED));
  }

  @Test
  void leavesSideHeadsAimedAtAnyoneElse() {
    assertEquals(
        List.of(2), WitherHeads.aimedAt(head -> head == 2 ? DEFENDED : STRANGER, DEFENDED));
  }

  @Test
  void neverTouchesTheMainHead() {
    assertEquals(List.of(), WitherHeads.aimedAt(head -> head == 0 ? DEFENDED : 0, DEFENDED));
  }
}
