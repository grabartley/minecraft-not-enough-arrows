package com.grahambartley.notenougharrows.traversal;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public final class TrampolineLaunch {
  private static final float LAUNCH_VOLUME = 1.0f;
  private static final float LAUNCH_PITCH = 1.0f;

  private TrampolineLaunch() {}

  public static Vec3d velocity(final Vec3d current, final float strength) {
    return new Vec3d(current.getX(), strength > 0.0f ? strength : 0.0, current.getZ());
  }

  public static void launch(final Entity entity) {
    final float strength = ServerConfigService.get().traversal().trampoline().strength();
    entity.setVelocity(velocity(entity.getVelocity(), strength));
    entity.velocityModified = true;
    if (strength > 0.0f) {
      ModSoundPlayer.playFrom(
          entity, ModSounds.TRAMPOLINE_ARROW_LAUNCH, LAUNCH_VOLUME, LAUNCH_PITCH);
    }
  }
}
