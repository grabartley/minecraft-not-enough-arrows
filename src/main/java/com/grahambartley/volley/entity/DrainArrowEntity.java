package com.grahambartley.volley.entity;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.terrain.DrainService;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DrainArrowEntity extends BlockAreaArrowEntity {

  public DrainArrowEntity(
      final EntityType<? extends DrainArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public DrainArrowEntity(
      final EntityType<? extends DrainArrowEntity> entityType,
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
    return DrainService.drain(world, center, shooter);
  }

  @Override
  protected SoundEvent effectSound() {
    return ModSounds.DRAIN_ARROW_ABSORB;
  }

  @Override
  protected boolean discardsOnBlockHit(final boolean changedAny) {
    return ServerConfigService.get().terrain().drain().enabled();
  }
}
