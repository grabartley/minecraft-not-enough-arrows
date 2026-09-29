package com.grahambartley.notenougharrows.discovery;

import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.world.BlockPlacement;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class TorchPlacement {
  private static final float PLACE_VOLUME_SHARE = 0.5f;
  private static final float PLACE_PITCH_SHARE = 0.8f;

  private TorchPlacement() {}

  public static Optional<BlockPos> place(
      final ServerWorld world,
      final BlockPos struck,
      final Direction face,
      @Nullable final PlayerEntity shooter) {
    if (world == null || struck == null || face == null) {
      return Optional.empty();
    }
    final Optional<BlockState> torch = torchFor(face);
    final BlockPos pos = struck.offset(face);
    if (torch.isEmpty()
        || !mayBuildByHand(shooter)
        || !BlockPlacement.canPlace(world, pos, torch.get(), shooter)
        || !world.setBlockState(pos, torch.get(), Block.NOTIFY_ALL)) {
      return Optional.empty();
    }
    final BlockSoundGroup sounds = torch.get().getSoundGroup();
    ModSoundPlayer.play(
        world,
        Vec3d.ofCenter(pos),
        sounds.getPlaceSound(),
        SoundCategory.BLOCKS,
        (sounds.getVolume() + 1.0f) * PLACE_VOLUME_SHARE,
        sounds.getPitch() * PLACE_PITCH_SHARE);
    return Optional.of(pos.toImmutable());
  }

  public static Optional<BlockState> torchFor(final Direction face) {
    if (face == Direction.UP) {
      return Optional.of(Blocks.TORCH.getDefaultState());
    }
    if (face.getAxis().isHorizontal()) {
      return Optional.of(Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, face));
    }
    return Optional.empty();
  }

  private static boolean mayBuildByHand(@Nullable final PlayerEntity shooter) {
    return shooter == null || shooter.canModifyBlocks();
  }
}
