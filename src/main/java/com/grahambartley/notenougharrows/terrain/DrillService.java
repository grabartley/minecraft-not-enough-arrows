package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.config.DrillArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.world.DropGrant;
import com.grahambartley.notenougharrows.world.HandBreakable;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class DrillService {

  private DrillService() {}

  public static boolean bore(
      final ServerWorld world, final BlockPos pos, @Nullable final PlayerEntity shooter) {
    return bore(world, pos, shooter, ServerConfigService.get().terrain().drill());
  }

  public static boolean bore(
      final ServerWorld world,
      final BlockPos pos,
      @Nullable final PlayerEntity shooter,
      final DrillArrowConfig drill) {
    if (world == null || pos == null || drill == null || !drill.enabled()) {
      return false;
    }
    final BlockState state = world.getBlockState(pos);
    final ItemStack tool = DrillTool.forTier(drill.toolTier());
    if (!HandBreakable.allows(world, pos, state, shooter)
        || !DrillTool.canHarvest(tool, state)
        || state.getBlock() instanceof IceBlock) {
      return false;
    }

    final List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, null, shooter, tool);
    if (!world.breakBlock(pos, false, shooter)) {
      return false;
    }
    state.onStacksDropped(world, pos, tool, true);
    DropGrant.grant(world, pos, drops, shooter);
    return true;
  }
}
