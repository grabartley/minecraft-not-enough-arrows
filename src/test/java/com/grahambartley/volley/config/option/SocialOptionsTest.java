package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SocialOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "social.courier.maxPayload",
            "social.courier.undeliverable",
            "social.snowGolem.lifetimeTicks",
            "social.magnet.radius"),
        SocialOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("social", SocialOptions.section().id());
    assertEquals(SocialOptions.options(), SocialOptions.section().options());
  }
}
