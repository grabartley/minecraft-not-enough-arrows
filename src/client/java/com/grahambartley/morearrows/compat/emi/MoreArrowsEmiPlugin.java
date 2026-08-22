package com.grahambartley.morearrows.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

/**
 * Loaded by EMI through the {@code emi} entrypoint in {@code fabric.mod.json}. Nothing else
 * references this class, so it stays unloaded when EMI is not installed.
 *
 * <p>EMI discovers the mod's items from the item groups on its own, so nothing extra is registered
 * until the shared info entries exist.
 */
public final class MoreArrowsEmiPlugin implements EmiPlugin {

  @Override
  public void register(final EmiRegistry registry) {}
}
