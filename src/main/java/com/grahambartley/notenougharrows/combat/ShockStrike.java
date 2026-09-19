package com.grahambartley.notenougharrows.combat;

import com.grahambartley.notenougharrows.config.ShockArrowConfig;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
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
      final Entity source) {
    flash(world, at);
    shock(world, struck, config, source);
    arcFrom(world, at, struck, config, source)
        .ifPresent(arced -> shock(world, arced, config, source));
  }

  public static java.util.Optional<Entity> arcFrom(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final Entity struck,
      final ShockArrowConfig config,
      final Entity source) {
    if (!config.arcs()) {
      return java.util.Optional.empty();
    }
    return ShockArc.nearest(
        at, candidates(world, at, struck, config, source), Entity::getPos, config.arcRadius());
  }

  private static List<Entity> candidates(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final Entity struck,
      final ShockArrowConfig config,
      final Entity source) {
    final Box search = new Box(at, at).expand(config.arcRadius());
    return world.getOtherEntities(
        source,
        search,
        candidate ->
            candidate instanceof LivingEntity
                && candidate.isAlive()
                && candidate != struck
                && candidate != source);
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

  private static void shock(
      final ServerWorld world,
      @Nullable final Entity target,
      final ShockArrowConfig config,
      final Entity source) {
    if (!(target instanceof LivingEntity living)
        || config.damage() <= ShockArrowConfig.DAMAGE_MIN) {
      return;
    }
    living.damage(world.getDamageSources().lightningBolt(), config.damage());
  }
}
