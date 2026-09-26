package com.grahambartley.notenougharrows.control;

public enum TargetPolicy {
  AIM_AT_SUBJECT,
  LEAVE_ALONE,
  DEFEND_SUBJECT;

  public boolean retargetsEveryTick() {
    return this == DEFEND_SUBJECT;
  }
}
