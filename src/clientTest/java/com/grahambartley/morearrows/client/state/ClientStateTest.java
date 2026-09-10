package com.grahambartley.morearrows.client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ClientStateTest {

  @Test
  void defaultsShowTheCountdownAndPlayItsSound() {
    final ClientState defaults = ClientState.defaults();

    assertTrue(defaults.showCountdownRing());
    assertTrue(defaults.playCountdownSound());
    assertEquals(1.0f, defaults.countdownRingScale());
  }

  @ParameterizedTest
  @CsvSource({"0.1, 0.5", "0.5, 0.5", "1.0, 1.0", "2.0, 2.0", "9.0, 2.0"})
  void clampsTheHudScaleRatherThanRejectingIt(final float given, final float expected) {
    assertEquals(expected, new ClientState(true, true, given).countdownRingScale());
  }

  @Test
  void appliesDefaultsToAnEmptyDocument() {
    assertEquals(ClientState.defaults(), ClientState.fromJson(new JsonObject()));
  }

  @ParameterizedTest
  @MethodSource("states")
  void survivesARoundTripThroughJson(final ClientState state) {
    assertEquals(state, ClientState.fromJson(state.toJson()));
  }

  static Stream<Arguments> states() {
    return Stream.of(
        Arguments.of(ClientState.defaults()),
        Arguments.of(new ClientState(false, false, 0.5f)),
        Arguments.of(new ClientState(true, false, 2.0f)),
        Arguments.of(new ClientState(false, true, 1.25f)));
  }

  @Test
  void readsEveryValueBackFromADocument() {
    final ClientState read =
        ClientState.fromJson(
            parse(
                "{\"showCountdownRing\":false,"
                    + "\"playCountdownSound\":false,\"countdownRingScale\":1.5}"));

    assertFalse(read.showCountdownRing());
    assertFalse(read.playCountdownSound());
    assertEquals(1.5f, read.countdownRingScale());
  }

  @Test
  void keepsKnownValuesWhenOneKeyIsMissing() {
    final ClientState read = ClientState.fromJson(parse("{\"showCountdownRing\":false}"));

    assertFalse(read.showCountdownRing());
    assertTrue(read.playCountdownSound());
    assertEquals(1.0f, read.countdownRingScale());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"showCountdownRing\":\"yes\"}",
        "{\"showCountdownRing\":7}",
        "{\"showCountdownRing\":[]}",
        "{\"showCountdownRing\":{}}"
      })
  void fallsBackToTheDefaultWhenABooleanIsNotABoolean(final String json) {
    assertTrue(ClientState.fromJson(parse(json)).showCountdownRing());
  }

  @ParameterizedTest
  @ValueSource(strings = {"{\"countdownRingScale\":\"big\"}", "{\"countdownRingScale\":[]}"})
  void fallsBackToTheDefaultWhenTheScaleIsNotANumber(final String json) {
    assertEquals(1.0f, ClientState.fromJson(parse(json)).countdownRingScale());
  }

  @Test
  void clampsAnOutOfRangeScaleReadFromADocument() {
    assertEquals(
        ClientState.COUNTDOWN_RING_SCALE_MAX,
        ClientState.fromJson(parse("{\"countdownRingScale\":99}")).countdownRingScale());
  }

  @Test
  void changesOneValueAtATime() {
    final ClientState defaults = ClientState.defaults();

    assertEquals(new ClientState(false, true, 1.0f), defaults.withShowCountdownRing(false));
    assertEquals(new ClientState(true, false, 1.0f), defaults.withPlayCountdownSound(false));
    assertEquals(new ClientState(true, true, 2.0f), defaults.withCountdownRingScale(2.0f));
  }

  @Test
  void writesEveryKeyOutSoTheFileIsSelfDescribing() {
    final JsonObject json = ClientState.defaults().toJson();

    assertTrue(json.has("showCountdownRing"));
    assertTrue(json.has("playCountdownSound"));
    assertTrue(json.has("countdownRingScale"));
  }

  private static JsonObject parse(final String json) {
    return JsonParser.parseString(json).getAsJsonObject();
  }
}
