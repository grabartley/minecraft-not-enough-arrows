package com.grahambartley.notenougharrows.gametest;

import java.lang.reflect.Field;
import java.util.Arrays;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestRunContext;

final class LeavesWhenTestEnds implements TestListener {
  private final ServerPlayerEntity player;

  LeavesWhenTestEnds(final ServerPlayerEntity player) {
    this.player = player;
  }

  static ServerPlayerEntity register(final TestContext context, final ServerPlayerEntity player) {
    stateOf(context).addListener(new LeavesWhenTestEnds(player));
    return player;
  }

  @Override
  public void onStarted(final GameTestState test) {}

  @Override
  public void onPassed(final GameTestState test, final TestRunContext context) {
    leave();
  }

  @Override
  public void onFailed(final GameTestState test, final TestRunContext context) {
    leave();
  }

  @Override
  public void onRetry(
      final GameTestState prevState, final GameTestState nextState, final TestRunContext context) {
    leave();
  }

  void leave() {
    final PlayerManager players = player.getServer().getPlayerManager();
    if (players.getPlayer(player.getUuid()) == player) {
      players.remove(player);
    }
  }

  private static GameTestState stateOf(final TestContext context) {
    final Field state =
        Arrays.stream(TestContext.class.getDeclaredFields())
            .filter(field -> field.getType() == GameTestState.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("TestContext has no GameTestState"));
    try {
      state.setAccessible(true);
      return (GameTestState) state.get(context);
    } catch (final IllegalAccessException e) {
      throw new IllegalStateException("Cannot reach the running test", e);
    }
  }
}
