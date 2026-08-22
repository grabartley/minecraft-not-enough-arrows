package com.grahambartley.morearrows.client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ClientStateHolderTest {

  @AfterEach
  void tearDown() {
    ClientStateHolder.reset();
  }

  @Test
  void startsOnDefaults() {
    assertEquals(ClientState.defaults(), ClientStateHolder.get());
  }

  @Test
  void holdsWhateverItWasGiven() {
    final ClientState state = new ClientState(false, false, 1.5f);

    ClientStateHolder.set(state);

    assertEquals(state, ClientStateHolder.get());
  }

  @Test
  void replacesEarlierStateRatherThanMergingIt() {
    ClientStateHolder.set(new ClientState(false, false, 1.5f));
    final ClientState latest = new ClientState(true, false, 0.5f);

    ClientStateHolder.set(latest);

    assertEquals(latest, ClientStateHolder.get());
  }

  @Test
  void fallsBackToDefaultsRatherThanHoldingNothing() {
    ClientStateHolder.set(new ClientState(false, false, 1.5f));

    ClientStateHolder.set(null);

    assertEquals(ClientState.defaults(), ClientStateHolder.get());
  }

  @Test
  void resetsToDefaults() {
    ClientStateHolder.set(new ClientState(false, false, 1.5f));

    ClientStateHolder.reset();

    assertEquals(ClientState.defaults(), ClientStateHolder.get());
  }
}
