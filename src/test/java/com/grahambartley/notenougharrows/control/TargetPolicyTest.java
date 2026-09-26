package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TargetPolicyTest {

  @Test
  void onlyDefendingNeedsItsTargetPickedEveryTick() {
    assertTrue(TargetPolicy.DEFEND_SUBJECT.retargetsEveryTick());
    assertFalse(TargetPolicy.AIM_AT_SUBJECT.retargetsEveryTick());
    assertFalse(TargetPolicy.LEAVE_ALONE.retargetsEveryTick());
  }
}
