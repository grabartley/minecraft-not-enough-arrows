package com.grahambartley.notenougharrows.combat;

import com.grahambartley.notenougharrows.config.VolleyArrowConfig;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class VolleyBurst {

  private VolleyBurst() {}

  public static void split(
      final ServerWorld world,
      final PersistentProjectileEntity source,
      final VolleyArrowConfig config) {
    final List<Vec3d> velocities =
        VolleySpread.fragmentVelocities(
            source.getVelocity(), config.fragmentCount(), config.spreadDegrees());
    final double damage = VolleySpread.fragmentDamage(source.getDamage(), config.damageShare());

    for (final Vec3d velocity : velocities) {
      world.spawnEntity(fragment(world, source, velocity, damage, source.getOwner()));
    }
  }

  private static ArrowEntity fragment(
      final ServerWorld world,
      final PersistentProjectileEntity source,
      final Vec3d velocity,
      final double damage,
      @Nullable final Entity owner) {
    final ArrowEntity arrow =
        new ArrowEntity(
            world, source.getX(), source.getY(), source.getZ(), new ItemStack(Items.ARROW), null);
    arrow.setOwner(owner);
    arrow.setVelocity(velocity);
    arrow.setDamage(damage);
    arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
    return arrow;
  }
}
