package com.grahambartley.notenougharrows.arrow;

public enum ArrowImpact {
  DEFAULT(true, false),
  CONSUME(true, true),
  DISCARD(false, true),
  RETAIN(false, false);

  private final boolean vanillaResolution;
  private final boolean removal;

  ArrowImpact(final boolean vanillaResolution, final boolean removal) {
    this.vanillaResolution = vanillaResolution;
    this.removal = removal;
  }

  public boolean runsVanillaResolution() {
    return vanillaResolution;
  }

  public boolean removesArrow() {
    return removal;
  }
}
