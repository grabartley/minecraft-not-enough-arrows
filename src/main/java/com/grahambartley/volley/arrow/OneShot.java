package com.grahambartley.volley.arrow;

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
