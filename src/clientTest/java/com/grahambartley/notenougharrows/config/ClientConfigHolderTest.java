package com.grahambartley.notenougharrows.config;

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
    assertEquals(NotEnoughArrowsConfig.defaults(), ClientConfigHolder.get());
  }

  @Test
  void holdsWhateverTheServerSent() {
    final NotEnoughArrowsConfig fromServer =
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true));

    ClientConfigHolder.accept(fromServer);

    assertEquals(fromServer, ClientConfigHolder.get());
    assertTrue(ClientConfigHolder.isSynced());
  }

  @Test
  void replacesEarlierServerStateRatherThanMergingIt() {
    ClientConfigHolder.accept(
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true)));
    final NotEnoughArrowsConfig latest =
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(16, 0.5f, 0.2f, true, true, 4, false));

    ClientConfigHolder.accept(latest);

    assertEquals(latest, ClientConfigHolder.get());
  }

  @Test
  void ignoresANullSyncRatherThanDroppingKnownState() {
    final NotEnoughArrowsConfig fromServer =
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true));
    ClientConfigHolder.accept(fromServer);

    ClientConfigHolder.accept(null);

    assertEquals(fromServer, ClientConfigHolder.get());
    assertTrue(ClientConfigHolder.isSynced());
  }

  @Test
  void forgetsServerStateOnDisconnectSoTheNextServerStartsClean() {
    ClientConfigHolder.accept(
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, 0.2f, false, false, 8, true)));

    ClientConfigHolder.clear();

    assertEquals(NotEnoughArrowsConfig.defaults(), ClientConfigHolder.get());
    assertFalse(ClientConfigHolder.isSynced());
  }
}
