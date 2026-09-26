package com.grahambartley.notenougharrows.control;

import java.util.ArrayList;
import java.util.List;

public final class SmokeCloudTracker {
  private final List<SmokeCloud> clouds = new ArrayList<>();

  public void add(final SmokeCloud cloud) {
    clouds.add(cloud);
  }

  public void removeExpired(final long tick) {
    clouds.removeIf(cloud -> cloud.hasExpired(tick));
  }

  public List<SmokeCloud> live() {
    return List.copyOf(clouds);
  }

  public int size() {
    return clouds.size();
  }

  public boolean isEmpty() {
    return clouds.isEmpty();
  }
}
