package com.grahambartley.notenougharrows.traversal;

import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.world.BlockPlacement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public final class VineService {

  private VineService() {}

  public static List<BlockPos> grow(
      final ServerWorld world,
      final BlockPos struck,
      final Direction face,
      @Nullable final PlayerEntity shooter) {
    return grow(
        world, struck, face, shooter, ServerConfigService.get().traversal().vine().lengthBlocks());
  }

  public static List<BlockPos> grow(
      final ServerWorld world,
      final BlockPos struck,
      final Direction face,
      @Nullable final PlayerEntity shooter,
      final int lengthBlocks) {
    if (world == null || struck == null || face == null || !face.getAxis().isHorizontal()) {
      return List.of();
    }
    final BlockState vine = vineFacing(face.getOpposite());
    final List<BlockPos> grown = new ArrayList<>();
    for (final BlockPos pos : VerticalRun.upFrom(struck.offset(face), lengthBlocks)) {
      if (!BlockPlacement.canPlace(world, pos, vine, shooter)
          || !world.setBlockState(pos, vine, Block.NOTIFY_ALL)) {
        break;
      }
      grown.add(pos.toImmutable());
    }
    return List.copyOf(grown);
  }

  public static BlockState vineFacing(final Direction towardWall) {
    return Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(towardWall), true);
  }
}
