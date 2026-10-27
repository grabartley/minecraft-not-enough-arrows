package com.grahambartley.volley.entity;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.terrain.FreezeService;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FreezeArrowEntity extends BlockAreaArrowEntity {

  public FreezeArrowEntity(
      final EntityType<? extends FreezeArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public FreezeArrowEntity(
      final EntityType<? extends FreezeArrowEntity> entityType,
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
    return FreezeService.freeze(world, center, shooter);
  }

  @Override
  protected SoundEvent effectSound() {
    return ModSounds.FREEZE_ARROW_FREEZE;
  }

  @Override
  protected boolean alwaysDiscardsOnBlockHit() {
    return ServerConfigService.get().terrain().freeze().enabled();
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !ServerConfigService.get().terrain().freeze().enabled();
  }
}
