package com.grahambartley.notenougharrows.disguise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DisguiseSyncTest {
  private static final int MOB = 7;
  private static final Identifier SHEEP = Identifier.ofVanilla("sheep");

  @BeforeEach
  void setUp() {
    DisguiseSync.clear();
  }

  @Test
  void remembersTheFormTheServerSent() {
    DisguiseSync.accept(MOB, Optional.of(SHEEP));

    assertEquals(Optional.of(SHEEP), DisguiseSync.formOf(MOB));
  }

  @Test
  void aRestoreClearsTheForm() {
    DisguiseSync.accept(MOB, Optional.of(SHEEP));
    DisguiseSync.accept(MOB, Optional.empty());

    assertTrue(DisguiseSync.formOf(MOB).isEmpty());
  }

  @Test
  void refusesToDrawAnythingButAHarmlessForm() {
    DisguiseSync.accept(MOB, Optional.of(Identifier.ofVanilla("warden")));

    assertTrue(DisguiseSync.formOf(MOB).isEmpty());
  }

  @Test
  void anUnsupportedFormClearsAnEarlierOne() {
    DisguiseSync.accept(MOB, Optional.of(SHEEP));
    DisguiseSync.accept(MOB, Optional.of(Identifier.ofVanilla("warden")));

    assertTrue(DisguiseSync.formOf(MOB).isEmpty());
  }

  @Test
  void forgetsAnUnloadedMob() {
    DisguiseSync.accept(MOB, Optional.of(SHEEP));
    DisguiseSync.forget(MOB);

    assertTrue(DisguiseSync.formOf(MOB).isEmpty());
  }

  @Test
  void clearForgetsEveryMob() {
    DisguiseSync.accept(MOB, Optional.of(SHEEP));
    DisguiseSync.accept(MOB + 1, Optional.of(SHEEP));
    DisguiseSync.clear();

    assertTrue(DisguiseSync.formOf(MOB).isEmpty());
    assertTrue(DisguiseSync.formOf(MOB + 1).isEmpty());
  }
}
