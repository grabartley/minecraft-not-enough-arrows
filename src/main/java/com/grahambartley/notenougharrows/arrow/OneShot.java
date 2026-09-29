package com.grahambartley.notenougharrows.arrow;

public final class OneShot {
  private boolean spent;

  public boolean claim() {
    if (spent) {
      return false;
    }
    spent = true;
    return true;
  }
}
