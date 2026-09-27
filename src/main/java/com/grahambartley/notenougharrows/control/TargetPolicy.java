package com.grahambartley.notenougharrows.control;

public enum TargetPolicy {
  AIM_AT_SUBJECT,
  DROP_UNLESS_CORNERED,
  DEFEND_SUBJECT;

  public boolean retargetsEveryTick() {
    return this == DEFEND_SUBJECT;
  }
}
