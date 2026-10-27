package com.grahambartley.volley.gametest;

import com.grahambartley.volley.config.ChaosArrowConfig;
import com.grahambartley.volley.config.DiscoveryArrowConfig;
import com.grahambartley.volley.config.ServerConfigHolder;
import com.grahambartley.volley.config.SocialArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

final class ConfigOverrides {
  private ConfigOverrides() {}

  static void during(final VolleyConfig config, final Runnable body) {
    during(
        config,
        () -> {
          body.run();
          return null;
        });
  }

  static <T> T during(final VolleyConfig config, final Supplier<T> body) {
    final VolleyConfig previous = ServerConfigHolder.get();
    try {
      ServerConfigHolder.set(config);
      return body.get();
    } finally {
      ServerConfigHolder.set(previous);
    }
  }

  static void useDefaultsWith(final UnaryOperator<VolleyConfig> change) {
    ServerConfigHolder.set(change.apply(VolleyConfig.defaults()));
  }

  static void useChaos(final UnaryOperator<ChaosArrowConfig> change) {
    useDefaultsWith(config -> config.withChaos(change.apply(config.chaos())));
  }

  static void useDiscovery(final UnaryOperator<DiscoveryArrowConfig> change) {
    useDefaultsWith(config -> config.withDiscovery(change.apply(config.discovery())));
  }

  static void useSocial(final UnaryOperator<SocialArrowConfig> change) {
    useDefaultsWith(config -> config.withSocial(change.apply(config.social())));
  }
}
