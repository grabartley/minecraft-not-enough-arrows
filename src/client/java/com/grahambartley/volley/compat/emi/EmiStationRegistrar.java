package com.grahambartley.volley.compat.emi;

import dev.emi.emi.api.EmiRegistry;

@FunctionalInterface
public interface EmiStationRegistrar {
  void register(EmiRegistry registry);
}
