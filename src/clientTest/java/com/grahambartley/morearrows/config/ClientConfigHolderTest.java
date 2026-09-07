package com.grahambartley.morearrows.config;

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
    assertEquals(MoreArrowsConfig.defaults(), ClientConfigHolder.get());
  }

  @Test
  void holdsWhateverTheServerSent() {
    final MoreArrowsConfig fromServer =
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true));

    ClientConfigHolder.accept(fromServer);

    assertEquals(fromServer, ClientConfigHolder.get());
    assertTrue(ClientConfigHolder.isSynced());
  }

  @Test
  void replacesEarlierServerStateRatherThanMergingIt() {
    ClientConfigHolder.accept(
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true)));
    final MoreArrowsConfig latest =
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(16, 0.5f, 0.2f, true, true, 4, false));

    ClientConfigHolder.accept(latest);

    assertEquals(latest, ClientConfigHolder.get());
  }

  @Test
  void ignoresANullSyncRatherThanDroppingKnownState() {
    final MoreArrowsConfig fromServer =
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true));
    ClientConfigHolder.accept(fromServer);

    ClientConfigHolder.accept(null);

    assertEquals(fromServer, ClientConfigHolder.get());
    assertTrue(ClientConfigHolder.isSynced());
  }

  @Test
  void forgetsServerStateOnDisconnectSoTheNextServerStartsClean() {
    ClientConfigHolder.accept(
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true)));

    ClientConfigHolder.clear();

    assertEquals(MoreArrowsConfig.defaults(), ClientConfigHolder.get());
    assertFalse(ClientConfigHolder.isSynced());
  }
}
