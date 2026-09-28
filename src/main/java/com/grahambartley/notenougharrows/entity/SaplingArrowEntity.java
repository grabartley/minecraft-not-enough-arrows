package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.agriculture.SaplingPlanting;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SaplingArrowEntity extends TintedArrowEntity {
  public SaplingArrowEntity(
      final EntityType<? extends SaplingArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public SaplingArrowEntity(
      final EntityType<? extends SaplingArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  public Item sapling() {
    return Registries.ITEM.get(tint().ingredient());
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    return SaplingPlanting.plant(world, blockHitResult, sapling(), shootingPlayer().orElse(null))
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }
}
