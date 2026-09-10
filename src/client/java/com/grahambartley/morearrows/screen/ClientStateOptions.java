package com.grahambartley.morearrows.screen;

import com.grahambartley.morearrows.client.state.ClientState;
import com.grahambartley.morearrows.config.option.BooleanOption;
import com.grahambartley.morearrows.config.option.ConfigOption;
import com.grahambartley.morearrows.config.option.ConfigSection;
import com.grahambartley.morearrows.config.option.FloatOption;
import java.util.List;

public final class ClientStateOptions {
  public static final String SECTION = "client";
  public static final String SHOW_COUNTDOWN_RING = SECTION + ".showCountdownRing";
  public static final String PLAY_COUNTDOWN_SOUND = SECTION + ".playCountdownSound";
  public static final String COUNTDOWN_RING_SCALE = SECTION + ".countdownRingScale";
  public static final float COUNTDOWN_RING_SCALE_STEP = 0.05f;

  private static final List<ConfigOption<ClientState>> OPTIONS = buildOptions();
  private static final ConfigSection<ClientState> CLIENT_SECTION =
      new ConfigSection<>(SECTION, OPTIONS);

  private ClientStateOptions() {}

  public static ConfigSection<ClientState> section() {
    return CLIENT_SECTION;
  }

  public static List<ConfigOption<ClientState>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<ClientState>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            SHOW_COUNTDOWN_RING,
            ClientState::showCountdownRing,
            ClientState::withShowCountdownRing),
        new BooleanOption<>(
            PLAY_COUNTDOWN_SOUND,
            ClientState::playCountdownSound,
            ClientState::withPlayCountdownSound),
        new FloatOption<>(
            COUNTDOWN_RING_SCALE,
            ClientState.COUNTDOWN_RING_SCALE_MIN,
            ClientState.COUNTDOWN_RING_SCALE_MAX,
            COUNTDOWN_RING_SCALE_STEP,
            ClientState::countdownRingScale,
            ClientState::withCountdownRingScale));
  }
}
