package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import org.junit.jupiter.api.Test;

class ConfigDraftTest {

  private static NotEnoughArrowsConfig changed() {
    return NotEnoughArrowsConfig.defaults()
        .withExplosive(NotEnoughArrowsConfig.defaults().explosive().withDamageTerrain(false));
  }

  @Test
  void startsOnTheValueItWasGiven() {
    final ConfigDraft<NotEnoughArrowsConfig> draft =
        new ConfigDraft<>(NotEnoughArrowsConfig.defaults());

    assertEquals(NotEnoughArrowsConfig.defaults(), draft.current());
    assertEquals(NotEnoughArrowsConfig.defaults(), draft.original());
    assertFalse(draft.isDirty());
  }

  @Test
  void tracksEditsWithoutTouchingTheOriginal() {
    final ConfigDraft<NotEnoughArrowsConfig> draft =
        new ConfigDraft<>(NotEnoughArrowsConfig.defaults());

    draft.set(changed());

    assertEquals(changed(), draft.current());
    assertEquals(NotEnoughArrowsConfig.defaults(), draft.original());
    assertTrue(draft.isDirty());
  }

  @Test
  void appliesAChangeToTheCurrentValue() {
    final ConfigDraft<NotEnoughArrowsConfig> draft =
        new ConfigDraft<>(NotEnoughArrowsConfig.defaults());

    draft.apply(config -> config.withExplosive(config.explosive().withDamageTerrain(false)));

    assertEquals(changed(), draft.current());
  }

  @Test
  void isCleanAgainWhenEditedBackToItsOriginalValue() {
    final ConfigDraft<NotEnoughArrowsConfig> draft =
        new ConfigDraft<>(NotEnoughArrowsConfig.defaults());

    draft.set(changed());
    draft.set(NotEnoughArrowsConfig.defaults());

    assertFalse(draft.isDirty());
  }

  @Test
  void revertsBackToItsOriginalValue() {
    final ConfigDraft<NotEnoughArrowsConfig> draft =
        new ConfigDraft<>(NotEnoughArrowsConfig.defaults());

    draft.set(changed());
    draft.revert();

    assertEquals(NotEnoughArrowsConfig.defaults(), draft.current());
    assertFalse(draft.isDirty());
  }

  @Test
  void refusesToHoldNothing() {
    assertThrows(NullPointerException.class, () -> new ConfigDraft<NotEnoughArrowsConfig>(null));
    assertThrows(
        NullPointerException.class,
        () -> new ConfigDraft<>(NotEnoughArrowsConfig.defaults()).set(null));
  }
}
