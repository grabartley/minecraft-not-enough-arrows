package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.world.BlockEditPermission;
import com.grahambartley.notenougharrows.world.DropGrant;
import com.grahambartley.notenougharrows.world.SpawnedDrops;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public final class ShearService {
  private static final double DROP_REACH = 2.0;
  private static final int FULL_HIVE = BeehiveBlock.FULL_HONEY_LEVEL;
  private static final int PUMPKIN_SEEDS = 4;
  private static final double SEED_OFFSET = 0.65;
  private static final double SEED_HEIGHT = 0.1;
  private static final double SEED_PUSH = 0.05;
  private static final double SEED_JITTER = 0.02;

  private ShearService() {}

  public static boolean shearBlock(
      final ServerWorld world,
      final BlockPos pos,
      final Direction face,
      final Direction facingShooter,
      @Nullable final PlayerEntity shooter) {
    if (world == null
        || pos == null
        || face == null
        || facingShooter == null
        || !world.isInBuildLimit(pos)
        || !BlockEditPermission.allows(world, pos, shooter)) {
      return false;
    }
    final BlockState state = world.getBlockState(pos);
    if (isFullHive(state)) {
      shearHive(world, pos, state, shooter);
      return true;
    }
    if (state.isOf(Blocks.PUMPKIN)) {
      final Direction carved = CarvingFace.carved(face, facingShooter);
      collect(
          world,
          new Box(pos).expand(DROP_REACH),
          () -> carve(world, pos, carved, shooter),
          shooter);
      return true;
    }
    return false;
  }

  public static boolean shearEntity(final Entity target, @Nullable final PlayerEntity shooter) {
    if (!(target instanceof Shearable shearable)
        || !target.isAlive()
        || !shearable.isShearable()
        || !(target.getWorld() instanceof ServerWorld world)
        || !BlockEditPermission.allows(world, target.getBlockPos(), shooter)) {
      return false;
    }
    final SoundCategory category = shooter == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS;
    collect(
        world,
        target.getBoundingBox().expand(DROP_REACH),
        () -> shearable.sheared(category),
        shooter);
    world.emitGameEvent(shooter, GameEvent.SHEAR, target.getBlockPos());
    return true;
  }

  private static boolean isFullHive(final BlockState state) {
    return state.getBlock() instanceof BeehiveBlock
        && state.get(BeehiveBlock.HONEY_LEVEL) >= FULL_HIVE;
  }

  private static void shearHive(
      final ServerWorld world,
      final BlockPos pos,
      final BlockState state,
      @Nullable final PlayerEntity shooter) {
    ModSoundPlayer.play(
        world, Vec3d.ofCenter(pos), ModSounds.SHEAR_ARROW_HIVE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    collect(
        world,
        new Box(pos).expand(DROP_REACH),
        () -> BeehiveBlock.dropHoneycomb(world, pos),
        shooter);
    ((BeehiveBlock) state.getBlock())
        .takeHoney(world, state, pos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
    world.emitGameEvent(shooter, GameEvent.SHEAR, pos);
  }

  private static void carve(
      final ServerWorld world,
      final BlockPos pos,
      final Direction facing,
      @Nullable final PlayerEntity shooter) {
    ModSoundPlayer.play(
        world, Vec3d.ofCenter(pos), ModSounds.SHEAR_ARROW_CARVE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    world.setBlockState(
        pos,
        Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, facing),
        Block.NOTIFY_ALL_AND_REDRAW);
    final ItemEntity seeds =
        new ItemEntity(
            world,
            pos.getX() + 0.5 + facing.getOffsetX() * SEED_OFFSET,
            pos.getY() + SEED_HEIGHT,
            pos.getZ() + 0.5 + facing.getOffsetZ() * SEED_OFFSET,
            new ItemStack(Items.PUMPKIN_SEEDS, PUMPKIN_SEEDS));
    seeds.setVelocity(
        SEED_PUSH * facing.getOffsetX() + world.random.nextDouble() * SEED_JITTER,
        SEED_PUSH,
        SEED_PUSH * facing.getOffsetZ() + world.random.nextDouble() * SEED_JITTER);
    world.spawnEntity(seeds);
    world.emitGameEvent(shooter, GameEvent.SHEAR, pos);
  }

  private static void collect(
      final ServerWorld world,
      final Box around,
      final Runnable shearing,
      @Nullable final PlayerEntity shooter) {
    DropGrant.collect(SpawnedDrops.during(world, around, shearing), shooter);
  }
}
