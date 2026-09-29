package com.grahambartley.notenougharrows.cloud;

import java.util.ArrayList;
import java.util.List;

public final class TimedCloudTracker {
  private final List<TimedCloud> clouds = new ArrayList<>();

  public void add(final TimedCloud cloud) {
    clouds.add(cloud);
  }

  public void removeExpired(final long tick) {
    clouds.removeIf(cloud -> cloud.hasExpired(tick));
  }

  public List<TimedCloud> live() {
    return List.copyOf(clouds);
  }

  public int size() {
    return clouds.size();
  }

  public boolean isEmpty() {
    return clouds.isEmpty();
  }
}
