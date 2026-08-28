package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.anchor.AnchorService;
import com.grahambartley.morearrows.anchor.BlockAnchor;
import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class AnchorServiceGameTest implements FabricGameTest {
  private static final String BATCH = "anchor-lifecycle";
  private static final String FORGET_BATCH = "anchor-forget";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final BlockPos TARGET = new BlockPos(3, 3, 3);
  private static final int LONG_LIFETIME_TICKS = 400;
  private static final int SHORT_LIFETIME_TICKS = 10;

  @BeforeBatch(batchId = BATCH)
  public void forgetAnchorsBeforeBatch(ServerWorld world) {
    AnchorService.forget();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anAnchorOnAValidTargetIsHeldForItsOwner(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);

    final BlockAnchor anchor = anchorFor(context, owner, LONG_LIFETIME_TICKS);

    context.assertTrue(anchor != null, "Anchoring to stone should produce an anchor");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), owner) == anchor,
        "The owner should hold the anchor that was just made");
    release(context, owner);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anInvalidTargetLeavesNoAnchorBehind(TestContext context) {
    final UUID owner = UUID.randomUUID();

    final BlockAnchor anchor = anchorFor(context, owner, LONG_LIFETIME_TICKS);

    context.assertTrue(anchor == null, "Anchoring to open air should produce no anchor");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), owner) == null,
        "A rejected target should leave nothing tracked");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aLifetimeOfZeroLeavesNoAnchorBehind(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);

    context.assertTrue(anchorFor(context, owner, 0) == null, "A zero lifetime should not anchor");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), owner) == null,
        "A zero lifetime should leave nothing tracked");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void breakingTheAnchoredBlockReleasesTheAnchor(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);
    anchorFor(context, owner, LONG_LIFETIME_TICKS);

    context.runAtTick(5, () -> context.setBlockState(TARGET, Blocks.AIR));
    context.runAtTick(
        10,
        () -> {
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), owner) == null,
              "Breaking the anchored block should release the anchor");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void replacingTheAnchoredBlockReleasesTheAnchor(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);
    anchorFor(context, owner, LONG_LIFETIME_TICKS);

    context.runAtTick(5, () -> context.setBlockState(TARGET, Blocks.DIRT));
    context.runAtTick(
        10,
        () -> {
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), owner) == null,
              "Replacing the anchored block should release the anchor");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anAnchorHoldsWhileItsBlockStandsAndItsLifetimeLasts(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);
    anchorFor(context, owner, LONG_LIFETIME_TICKS);

    context.runAtTick(
        30,
        () -> {
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), owner) != null,
              "An anchor on a standing block should still be held");
          release(context, owner);
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anAnchorIsReleasedOnceItsLifetimeElapses(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);
    anchorFor(context, owner, SHORT_LIFETIME_TICKS);

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 5,
        () -> {
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), owner) == null,
              "An anchor past its lifetime should be released");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void twoOwnersAnchoredToTheSameBlockStayIndependent(TestContext context) {
    final UUID first = UUID.randomUUID();
    final UUID second = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);

    final BlockAnchor firstAnchor = anchorFor(context, first, LONG_LIFETIME_TICKS);
    final BlockAnchor secondAnchor = anchorFor(context, second, LONG_LIFETIME_TICKS);

    context.assertTrue(
        firstAnchor != null && secondAnchor != null && firstAnchor != secondAnchor,
        "Two owners on one block should hold two anchors");

    release(context, first);

    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), first) == null,
        "Releasing one owner should release only that owner");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), second) == secondAnchor,
        "The other owner should still hold their anchor");
    release(context, second);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anOwnerAnchoringAgainKeepsOnlyTheirNewestAnchor(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);
    anchorFor(context, owner, LONG_LIFETIME_TICKS);

    final BlockAnchor replacement = anchorFor(context, owner, LONG_LIFETIME_TICKS);

    context.assertEquals(anchorsHeldBy(context, owner), 1, "Anchors held by the owner");
    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), owner) == replacement,
        "The newest anchor should be the one that is held");
    release(context, owner);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anOwnerLeavingReleasesEveryAnchorTheyHeld(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);
    anchorFor(context, owner, LONG_LIFETIME_TICKS);

    AnchorService.releaseEverywhere(owner);

    context.assertTrue(
        AnchorService.anchorOf(context.getWorld(), owner) == null,
        "An owner who leaves should hold no anchor anywhere");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = FORGET_BATCH, tickLimit = 20)
  public void forgettingEverythingLeavesNoAnchorState(TestContext context) {
    final UUID owner = UUID.randomUUID();
    context.setBlockState(TARGET, Blocks.STONE);
    anchorFor(context, owner, LONG_LIFETIME_TICKS);

    AnchorService.forget();

    context.assertTrue(
        AnchorService.anchorsIn(context.getWorld()).isEmpty(),
        "A stopped server should leave no anchor state behind");
    context.complete();
  }

  private static BlockAnchor anchorFor(
      final TestContext context, final UUID owner, final int lifetimeTicks) {
    return AnchorService.anchor(
        context.getWorld(), owner, context.getAbsolutePos(TARGET), lifetimeTicks);
  }

  private static int anchorsHeldBy(final TestContext context, final UUID owner) {
    return (int)
        AnchorService.anchorsIn(context.getWorld()).stream()
            .filter(anchor -> anchor.ownerId().equals(owner))
            .count();
  }

  private static void release(final TestContext context, final UUID owner) {
    AnchorService.release(context.getWorld(), owner);
  }
}
