package com.grahambartley.notenougharrows.combat;

import com.grahambartley.notenougharrows.config.ShockArrowConfig;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class ShockStrike {

  private ShockStrike() {}

  public static void strike(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final Entity struck,
      final ShockArrowConfig config,
      final ProjectileEntity source) {
    flash(world, at);
    arcFrom(world, at, struck, config, source)
        .ifPresent(arced -> shock(world, arced, config, source));
  }

  private static Optional<LivingEntity> arcFrom(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final Entity struck,
      final ShockArrowConfig config,
      final ProjectileEntity source) {
    if (!config.arcs()) {
      return Optional.empty();
    }
    return NearestCandidate.nearest(
        at,
        candidates(world, at, struck, config, source),
        LivingEntity::getPos,
        config.arcRadius());
  }

  private static List<LivingEntity> candidates(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final Entity struck,
      final ShockArrowConfig config,
      final ProjectileEntity source) {
    final Box search = new Box(at, at).expand(config.arcRadius());
    final Entity shooter = source.getOwner();
    return world.getEntitiesByClass(
        LivingEntity.class,
        search,
        candidate -> candidate.isAlive() && candidate != struck && candidate != shooter);
  }

  private static void shock(
      final ServerWorld world,
      final LivingEntity target,
      final ShockArrowConfig config,
      final ProjectileEntity source) {
    if (config.damage() <= 0.0f) {
      return;
    }
    target.damage(lightningFrom(world, source), config.damage());
  }

  private static DamageSource lightningFrom(
      final ServerWorld world, final ProjectileEntity source) {
    final RegistryEntry<DamageType> lightning =
        world
            .getRegistryManager()
            .get(RegistryKeys.DAMAGE_TYPE)
            .entryOf(DamageTypes.LIGHTNING_BOLT);
    return new DamageSource(lightning, source, source.getOwner());
  }

  private static void flash(final ServerWorld world, final Vec3d at) {
    final LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
    if (bolt == null) {
      return;
    }
    bolt.refreshPositionAfterTeleport(at);
    bolt.setCosmetic(true);
    world.spawnEntity(bolt);
  }
}
