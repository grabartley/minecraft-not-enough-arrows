package com.grahambartley.morearrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import org.junit.jupiter.api.Test;

class ConfigDraftTest {

  private static MoreArrowsConfig changed() {
    return MoreArrowsConfig.defaults()
        .withExplosive(MoreArrowsConfig.defaults().explosive().withDamageTerrain(true));
  }

  @Test
  void startsOnTheValueItWasGiven() {
    final ConfigDraft<MoreArrowsConfig> draft = new ConfigDraft<>(MoreArrowsConfig.defaults());

    assertEquals(MoreArrowsConfig.defaults(), draft.current());
    assertEquals(MoreArrowsConfig.defaults(), draft.original());
    assertFalse(draft.isDirty());
  }

  @Test
  void tracksEditsWithoutTouchingTheOriginal() {
    final ConfigDraft<MoreArrowsConfig> draft = new ConfigDraft<>(MoreArrowsConfig.defaults());

    draft.set(changed());

    assertEquals(changed(), draft.current());
    assertEquals(MoreArrowsConfig.defaults(), draft.original());
    assertTrue(draft.isDirty());
  }

  @Test
  void appliesAChangeToTheCurrentValue() {
    final ConfigDraft<MoreArrowsConfig> draft = new ConfigDraft<>(MoreArrowsConfig.defaults());

    draft.apply(config -> config.withExplosive(config.explosive().withDamageTerrain(true)));

    assertEquals(changed(), draft.current());
  }

  @Test
  void isCleanAgainWhenEditedBackToItsOriginalValue() {
    final ConfigDraft<MoreArrowsConfig> draft = new ConfigDraft<>(MoreArrowsConfig.defaults());

    draft.set(changed());
    draft.set(MoreArrowsConfig.defaults());

    assertFalse(draft.isDirty());
  }

  @Test
  void revertsBackToItsOriginalValue() {
    final ConfigDraft<MoreArrowsConfig> draft = new ConfigDraft<>(MoreArrowsConfig.defaults());

    draft.set(changed());
    draft.revert();

    assertEquals(MoreArrowsConfig.defaults(), draft.current());
    assertFalse(draft.isDirty());
  }

  @Test
  void refusesToHoldNothing() {
    assertThrows(NullPointerException.class, () -> new ConfigDraft<MoreArrowsConfig>(null));
    assertThrows(
        NullPointerException.class, () -> new ConfigDraft<>(MoreArrowsConfig.defaults()).set(null));
  }
}
