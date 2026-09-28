package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.config.BlossomArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.terrain.SphereSweep;
import com.grahambartley.notenougharrows.world.BlockSphere;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class BlossomService {
  private static final int BONE_MEAL_PARTICLES = 15;
  private static final double PARTICLE_SPREAD = 0.4;

  private BlossomService() {}

  public static List<BlockPos> bloom(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return bloom(world, center, shooter, ServerConfigService.get().agriculture().blossom());
  }

  public static List<BlockPos> bloom(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final BlossomArrowConfig blossom) {
    if (world == null || center == null || blossom == null) {
      return List.of();
    }
    final List<BlockPos> fertilizable =
        BlockSphere.blocks(center, blossom.radius()).stream()
            .filter(SphereSweep.editableBy(world, shooter))
            .filter(pos -> isFertilizable(world, pos))
            .toList();
    final List<BlockPos> bloomed = new ArrayList<>();
    for (final BlockPos pos : fertilizable) {
      if (boneMeal(world, pos)) {
        bloomed.add(pos);
      }
    }
    return List.copyOf(bloomed);
  }

  private static boolean isFertilizable(final ServerWorld world, final BlockPos pos) {
    final BlockState state = world.getBlockState(pos);
    return state.getBlock() instanceof Fertilizable fertilizable
        && fertilizable.isFertilizable(world, pos, state);
  }

  private static boolean boneMeal(final ServerWorld world, final BlockPos pos) {
    if (!BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), world, pos)) {
      return false;
    }
    world.spawnParticles(
        ParticleTypes.HAPPY_VILLAGER,
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        BONE_MEAL_PARTICLES,
        PARTICLE_SPREAD,
        PARTICLE_SPREAD,
        PARTICLE_SPREAD,
        0.0);
    return true;
  }
}
