package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.block.BlockAudit;
import com.grahambartley.notenougharrows.block.BlockAudit.BlockFacts;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class BlockAuditGameTest implements FabricGameTest {
  private static final String BATCH = "release-audit-blocks";
  private static final BlockPos OPEN_AIR = new BlockPos(0, 0, 0);

  @GameTest(
      templateName = FabricGameTest.EMPTY_STRUCTURE,
      batchId = BATCH,
      tickLimit = 10,
      skyAccess = true)
  public void everyModBlockLeavesNothingBehindAndCannotOutstayItsPurpose(TestContext context) {
    final List<String> violations = BlockAudit.violations(modBlockFacts(context));

    context.assertTrue(violations.isEmpty(), "Blocks breaking REL-15: " + violations);
    context.complete();
  }

  @GameTest(
      templateName = FabricGameTest.EMPTY_STRUCTURE,
      batchId = BATCH,
      tickLimit = 10,
      skyAccess = true)
  public void everyBlockTheModRegistersDeclaresHowItEnds(TestContext context) {
    final Set<Identifier> inspected =
        modBlockFacts(context).stream().map(BlockFacts::block).collect(Collectors.toSet());

    context.assertTrue(
        !inspected.isEmpty() && inspected.equals(ModBlocks.declaredIds()),
        "The REL-15 audit inspected "
            + inspected
            + " but ModBlocks declares lifetimes for "
            + ModBlocks.declaredIds());
    context.complete();
  }

  private static List<BlockFacts> modBlockFacts(final TestContext context) {
    return Registries.BLOCK.getIds().stream()
        .filter(id -> NotEnoughArrows.MOD_ID.equals(id.getNamespace()))
        .sorted()
        .map(id -> factsOf(context, id))
        .toList();
  }

  private static BlockFacts factsOf(final TestContext context, final Identifier id) {
    final Block block = Registries.BLOCK.get(id);
    final BlockState state = block.getDefaultState();
    final BlockPos pos = context.getAbsolutePos(OPEN_AIR);
    return new BlockFacts(
        id,
        block.asItem() != Items.AIR || Registries.ITEM.containsId(id),
        isCraftable(context, id),
        !Block.getDroppedStacks(state, context.getWorld(), pos, null).isEmpty(),
        ModBlocks.lifetimesOf(id),
        state.canPlaceAt(context.getWorld(), pos));
  }

  private static boolean isCraftable(final TestContext context, final Identifier id) {
    return context.getWorld().getRecipeManager().values().stream()
        .map(entry -> entry.value().getResult(context.getWorld().getRegistryManager()))
        .anyMatch(
            result -> !result.isEmpty() && Registries.ITEM.getId(result.getItem()).equals(id));
  }
}
