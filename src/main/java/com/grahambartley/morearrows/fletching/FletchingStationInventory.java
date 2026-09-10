package com.grahambartley.morearrows.fletching;

import java.util.Objects;
import net.minecraft.inventory.SimpleInventory;

public final class FletchingStationInventory extends SimpleInventory {
  private final Runnable onChanged;

  public FletchingStationInventory(final int size, final Runnable onChanged) {
    super(size);
    this.onChanged = Objects.requireNonNull(onChanged, "onChanged");
  }

  @Override
  public void markDirty() {
    super.markDirty();
    onChanged.run();
  }
}
