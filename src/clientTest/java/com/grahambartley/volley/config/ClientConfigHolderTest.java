package com.grahambartley.volley.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ClientConfigHolderTest {

  @AfterEach
  void tearDown() {
    ClientConfigHolder.clear();
  }

  @Test
  void startsUnsyncedOnDefaults() {
    assertFalse(ClientConfigHolder.isSynced());
    assertEquals(VolleyConfig.defaults(), ClientConfigHolder.get());
  }

  @Test
  void holdsWhateverTheServerSent() {
    final VolleyConfig fromServer =
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true));

    ClientConfigHolder.accept(fromServer);

    assertEquals(fromServer, ClientConfigHolder.get());
    assertTrue(ClientConfigHolder.isSynced());
  }

  @Test
  void replacesEarlierServerStateRatherThanMergingIt() {
    ClientConfigHolder.accept(
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true)));
    final VolleyConfig latest =
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(16, 0.5f, 0.2f, true, true, 4, false));

    ClientConfigHolder.accept(latest);

    assertEquals(latest, ClientConfigHolder.get());
  }

  @Test
  void ignoresANullSyncRatherThanDroppingKnownState() {
    final VolleyConfig fromServer =
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true));
    ClientConfigHolder.accept(fromServer);

    ClientConfigHolder.accept(null);

    assertEquals(fromServer, ClientConfigHolder.get());
    assertTrue(ClientConfigHolder.isSynced());
  }

  @Test
  void forgetsServerStateOnDisconnectSoTheNextServerStartsClean() {
    ClientConfigHolder.accept(
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true)));

    ClientConfigHolder.clear();

    assertEquals(VolleyConfig.defaults(), ClientConfigHolder.get());
    assertFalse(ClientConfigHolder.isSynced());
  }
}
