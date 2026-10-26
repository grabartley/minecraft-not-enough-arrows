package com.grahambartley.volley.control;

public enum TargetPolicy {
  AIM_AT_SUBJECT,
  DROP_UNLESS_CORNERED,
  DEFEND_SUBJECT;

  public boolean retargetsEveryTick() {
    return this != DROP_UNLESS_CORNERED;
  }
}
