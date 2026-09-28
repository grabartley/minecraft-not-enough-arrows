package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.zipline.ZiplineFeedback;
import com.grahambartley.notenougharrows.zipline.ZiplineOutcome;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class ZiplineFeedbackGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-feedback";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theShooterIsToldWhatTheirShotDidOnTheActionBar(TestContext context) {
    final List<Text> overlay = new ArrayList<>();
    final PlayerEntity shooter = listening(context, overlay);

    ZiplineFeedback.tell(shooter, ZiplineOutcome.TOO_FAR);

    context.assertEquals(1, overlay.size(), "Messages");
    context.assertEquals(
        "message.not-enough-arrows.zipline.too_far",
        ((TranslatableTextContent) overlay.getFirst().getContent()).getKey(),
        "Message key");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aShotThatDidNothingSaysNothing(TestContext context) {
    final List<Text> overlay = new ArrayList<>();

    ZiplineFeedback.tell(listening(context, overlay), ZiplineOutcome.NOTHING);
    ZiplineFeedback.tell(null, ZiplineOutcome.TOO_FAR);

    context.assertTrue(overlay.isEmpty(), "Nothing to say");
    context.complete();
  }

  private static PlayerEntity listening(final TestContext context, final List<Text> overlay) {
    final PlayerEntity mock = context.createMockPlayer(GameMode.SURVIVAL);
    return new PlayerEntity(
        context.getWorld(), context.getAbsolutePos(BlockPos.ORIGIN), 0.0f, mock.getGameProfile()) {
      @Override
      public void sendMessage(final Text message, final boolean actionBar) {
        if (actionBar) {
          overlay.add(message);
        }
      }

      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return false;
      }
    };
  }
}
