package com.grahambartley.notenougharrows.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SmokeCloudTracker {
  private final List<SmokeCloud> clouds = new ArrayList<>();

  public void add(final SmokeCloud cloud) {
    if (cloud == null || cloud.radius() <= 0.0) {
      return;
    }
    clouds.add(cloud);
  }

  public List<SmokeCloud> takeExpired(final long tick) {
    final List<SmokeCloud> expired = new ArrayList<>();
    final Iterator<SmokeCloud> remaining = clouds.iterator();
    while (remaining.hasNext()) {
      final SmokeCloud cloud = remaining.next();
      if (cloud.hasExpired(tick)) {
        expired.add(cloud);
        remaining.remove();
      }
    }
    return List.copyOf(expired);
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
