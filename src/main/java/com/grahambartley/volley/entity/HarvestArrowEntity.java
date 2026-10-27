package com.grahambartley.volley.entity;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.agriculture.HarvestService;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HarvestArrowEntity extends BlockAreaArrowEntity {

  public HarvestArrowEntity(
      final EntityType<? extends HarvestArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public HarvestArrowEntity(
      final EntityType<? extends HarvestArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected List<BlockPos> affectAround(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return HarvestService.harvest(world, center, shooter);
  }

  @Override
  protected SoundEvent effectSound() {
    return ModSounds.HARVEST_ARROW_REAP;
  }
}
