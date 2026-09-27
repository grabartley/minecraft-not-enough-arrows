package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TargetPolicyTest {

  @Test
  void everyPolicyThatPicksATargetReassertsItEveryTickSoVanillaCannotTakeItBack() {
    assertTrue(TargetPolicy.DEFEND_SUBJECT.retargetsEveryTick());
    assertTrue(TargetPolicy.AIM_AT_SUBJECT.retargetsEveryTick());
    assertFalse(TargetPolicy.DROP_UNLESS_CORNERED.retargetsEveryTick());
  }
}
