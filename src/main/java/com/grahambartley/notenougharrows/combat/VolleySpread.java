package com.grahambartley.notenougharrows.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;

public final class VolleySpread {
  private static final Vec3d UPRIGHT_REFERENCE = new Vec3d(0.0, 1.0, 0.0);
  private static final Vec3d SIDEWAYS_REFERENCE = new Vec3d(1.0, 0.0, 0.0);
  private static final double NEARLY_VERTICAL = 0.9;

  private VolleySpread() {}

  public static List<Vec3d> fragmentVelocities(
      final Vec3d velocity, final int fragmentCount, final float spreadDegrees) {
    if (velocity == null || velocity.lengthSquared() <= 0.0 || fragmentCount <= 0) {
      return List.of();
    }

    final double speed = velocity.length();
    final Vec3d forward = velocity.normalize();
    final Vec3d right = perpendicularTo(forward);
    final Vec3d up = forward.crossProduct(right).normalize();

    final double spread = Math.toRadians(spreadDegrees);
    final double cosine = Math.cos(spread);
    final double sine = Math.sin(spread);

    final List<Vec3d> fragments = new ArrayList<>(fragmentCount);
    for (int index = 0; index < fragmentCount; index++) {
      final double aroundAxis = 2.0 * Math.PI * index / fragmentCount;
      final Vec3d axis =
          right.multiply(Math.cos(aroundAxis)).add(up.multiply(Math.sin(aroundAxis)));
      final Vec3d tilted =
          forward.multiply(cosine).add(axis.crossProduct(forward).multiply(sine)).normalize();
      fragments.add(tilted.multiply(speed));
    }
    return List.copyOf(fragments);
  }

  public static double fragmentDamage(final double baseDamage, final float damageShare) {
    if (baseDamage <= 0.0 || damageShare <= 0.0f) {
      return 0.0;
    }
    return baseDamage * Math.min(1.0f, damageShare);
  }

  private static Vec3d perpendicularTo(final Vec3d forward) {
    final Vec3d reference =
        Math.abs(forward.y) < NEARLY_VERTICAL ? UPRIGHT_REFERENCE : SIDEWAYS_REFERENCE;
    return forward.crossProduct(reference).normalize();
  }
}
