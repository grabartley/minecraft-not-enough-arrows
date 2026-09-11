package com.grahambartley.morearrows.compat.emi;

import dev.emi.emi.api.EmiRegistry;

@FunctionalInterface
public interface EmiStationRegistrar {
  void register(EmiRegistry registry);
}
