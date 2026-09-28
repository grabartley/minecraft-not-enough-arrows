package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.config.HarvestArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.terrain.SphereSweep;
import com.grahambartley.notenougharrows.world.DropGrant;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public final class HarvestService {
  private static final int HARVEST_PARTICLES = 12;
  private static final double PARTICLE_SPREAD = 0.3;

  private HarvestService() {}

  public static List<BlockPos> harvest(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return harvest(world, center, shooter, ServerConfigService.get().agriculture().harvest());
  }

  public static List<BlockPos> harvest(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final HarvestArrowConfig harvest) {
    if (harvest == null) {
      return List.of();
    }
    return SphereSweep.sweep(
        world,
        center,
        harvest.radius(),
        Integer.MAX_VALUE,
        shooter,
        (target, pos) -> reap(target, pos, shooter));
  }

  private static boolean reap(
      final ServerWorld world, final BlockPos pos, @Nullable final PlayerEntity shooter) {
    final BlockState state = world.getBlockState(pos);
    final Optional<BlockState> replanted = MatureCrop.replanted(state);
    if (replanted.isEmpty()) {
      return false;
    }
    final ItemStack tool = new ItemStack(Items.IRON_HOE);
    final List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, null, shooter, tool);
    final Optional<List<ItemStack>> kept =
        ReplantSeed.takenFrom(drops, state.getBlock().getPickStack(world, pos, state).getItem());
    if (kept.isEmpty() || !world.setBlockState(pos, replanted.get(), Block.NOTIFY_ALL)) {
      return false;
    }
    world.spawnParticles(
        new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        HARVEST_PARTICLES,
        PARTICLE_SPREAD,
        PARTICLE_SPREAD,
        PARTICLE_SPREAD,
        0.0);
    world.emitGameEvent(shooter, GameEvent.BLOCK_CHANGE, pos);
    DropGrant.grant(world, pos, kept.get(), shooter);
    return true;
  }
}
