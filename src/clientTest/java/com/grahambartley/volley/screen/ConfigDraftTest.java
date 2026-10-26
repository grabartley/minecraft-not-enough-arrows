package com.grahambartley.volley.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.volley.config.VolleyConfig;
import org.junit.jupiter.api.Test;

class ConfigDraftTest {

  private static VolleyConfig changed() {
    return VolleyConfig.defaults()
        .withExplosive(VolleyConfig.defaults().explosive().withDamageTerrain(false));
  }

  @Test
  void startsOnTheValueItWasGiven() {
    final ConfigDraft<VolleyConfig> draft = new ConfigDraft<>(VolleyConfig.defaults());

    assertEquals(VolleyConfig.defaults(), draft.current());
    assertEquals(VolleyConfig.defaults(), draft.original());
    assertFalse(draft.isDirty());
  }

  @Test
  void tracksEditsWithoutTouchingTheOriginal() {
    final ConfigDraft<VolleyConfig> draft = new ConfigDraft<>(VolleyConfig.defaults());

    draft.set(changed());

    assertEquals(changed(), draft.current());
    assertEquals(VolleyConfig.defaults(), draft.original());
    assertTrue(draft.isDirty());
  }

  @Test
  void appliesAChangeToTheCurrentValue() {
    final ConfigDraft<VolleyConfig> draft = new ConfigDraft<>(VolleyConfig.defaults());

    draft.apply(config -> config.withExplosive(config.explosive().withDamageTerrain(false)));

    assertEquals(changed(), draft.current());
  }

  @Test
  void isCleanAgainWhenEditedBackToItsOriginalValue() {
    final ConfigDraft<VolleyConfig> draft = new ConfigDraft<>(VolleyConfig.defaults());

    draft.set(changed());
    draft.set(VolleyConfig.defaults());

    assertFalse(draft.isDirty());
  }

  @Test
  void revertsBackToItsOriginalValue() {
    final ConfigDraft<VolleyConfig> draft = new ConfigDraft<>(VolleyConfig.defaults());

    draft.set(changed());
    draft.revert();

    assertEquals(VolleyConfig.defaults(), draft.current());
    assertFalse(draft.isDirty());
  }

  @Test
  void refusesToHoldNothing() {
    assertThrows(NullPointerException.class, () -> new ConfigDraft<VolleyConfig>(null));
    assertThrows(
        NullPointerException.class, () -> new ConfigDraft<>(VolleyConfig.defaults()).set(null));
  }
}
