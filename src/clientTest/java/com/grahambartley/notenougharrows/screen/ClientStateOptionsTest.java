package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.notenougharrows.client.state.ClientState;
import com.grahambartley.notenougharrows.config.option.BooleanOption;
import com.grahambartley.notenougharrows.config.option.ConfigOption;
import com.grahambartley.notenougharrows.config.option.FloatOption;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ClientStateOptionsTest {

  private static Stream<ConfigOption<ClientState>> options() {
    return ClientStateOptions.options().stream();
  }

  private static ClientState changed(final ConfigOption<ClientState> option) {
    return switch (option) {
      case BooleanOption<ClientState> booleanOption ->
          booleanOption.write(ClientState.defaults(), !booleanOption.read(ClientState.defaults()));
      case FloatOption<ClientState> floatOption ->
          floatOption.write(ClientState.defaults(), floatOption.max());
      default -> throw new AssertionError("unexpected option type for " + option.id());
    };
  }

  @Test
  void listsEveryPerInstallationSetting() {
    assertEquals(
        List.of(
            "client.showCountdownRing", "client.playCountdownSound", "client.countdownRingScale"),
        ClientStateOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderTheClientSection() {
    assertEquals("client", ClientStateOptions.section().id());
    assertEquals(ClientStateOptions.options(), ClientStateOptions.section().options());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionChangesThatOptionsValue(final ConfigOption<ClientState> option) {
    assertNotEquals(
        option.displayValue(ClientState.defaults()), option.displayValue(changed(option)));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionLeavesEveryOtherSettingAlone(final ConfigOption<ClientState> option) {
    final ClientState after = changed(option);

    for (final ConfigOption<ClientState> other : ClientStateOptions.options()) {
      if (!other.id().equals(option.id())) {
        assertEquals(
            other.displayValue(ClientState.defaults()), other.displayValue(after), other.id());
      }
    }
  }
}
