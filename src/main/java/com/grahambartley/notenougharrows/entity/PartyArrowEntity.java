package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.chaos.PartyShow;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PartyArrowEntity extends TintedArrowEntity {
  public PartyArrowEntity(
      final EntityType<? extends PartyArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public PartyArrowEntity(
      final EntityType<? extends PartyArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    return partyAt(world, blockHitResult.getPos());
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    return partyAt(world, entityHitResult.getPos());
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !isPartying();
  }

  private ArrowImpact partyAt(final ServerWorld world, final Vec3d at) {
    if (!isPartying()) {
      return ArrowImpact.DEFAULT;
    }
    return PartyShow.throwAt(world, at, tint()) ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }

  private static boolean isPartying() {
    return ServerConfigService.get().chaos().party().enabled();
  }
}
