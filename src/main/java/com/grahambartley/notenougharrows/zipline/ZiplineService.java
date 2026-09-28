package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.anchor.AnchorSite;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.ZiplineArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class ZiplineService {
  private static final float STRING_VOLUME = 1.0f;
  private static final float STRING_PITCH = 1.0f;

  private ZiplineService() {}

  public static ZiplineOutcome shoot(
      final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      final BlockPos struck,
      final UUID arrowId) {
    return shoot(world, shooter, struck, arrowId, ServerConfigService.get().traversal().zipline());
  }

  public static ZiplineOutcome shoot(
      final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      final BlockPos struck,
      final UUID arrowId,
      final ZiplineArrowConfig zipline) {
    if (world == null
        || shooter == null
        || struck == null
        || arrowId == null
        || zipline == null
        || !AnchorSite.isSuitable(world, struck)) {
      return ZiplineOutcome.NOTHING;
    }
    final Optional<PendingAnchor> first =
        PendingAnchorService.claim(world, shooter)
            .filter(anchor -> !anchor.arrowId().equals(arrowId))
            .filter(anchor -> stillStands(world.getEntity(anchor.arrowId())));
    if (first.isEmpty()) {
      return holdFor(world, shooter, struck, arrowId, zipline, ZiplineOutcome.ANCHOR_SET);
    }
    final SpanResult result =
        SpanService.string(world, shooter, first.get().pos(), struck, zipline);
    if (!result.wasStrung()) {
      return holdFor(world, shooter, struck, arrowId, zipline, ZiplineOutcome.of(result.refusal()));
    }
    world.getEntity(first.get().arrowId()).discard();
    announce(world, result.span());
    return ZiplineOutcome.STRUNG;
  }

  private static void announce(final ServerWorld world, final Span span) {
    for (final Vec3d end : List.of(span.firstEnd(), span.lastEnd())) {
      ModSoundPlayer.play(
          world,
          end,
          ModSounds.ZIPLINE_ARROW_STRING,
          SoundCategory.BLOCKS,
          STRING_VOLUME,
          STRING_PITCH);
    }
  }

  static boolean stillStands(@Nullable final Entity firstArrow) {
    return firstArrow instanceof PersistentProjectileEntity arrow && !arrow.isRemoved();
  }

  private static ZiplineOutcome holdFor(
      final ServerWorld world,
      final PlayerEntity shooter,
      final BlockPos struck,
      final UUID arrowId,
      final ZiplineArrowConfig zipline,
      final ZiplineOutcome outcome) {
    return PendingAnchorService.hold(world, shooter, struck, arrowId, zipline.pendingWindowTicks())
        .map(anchor -> outcome)
        .orElse(ZiplineOutcome.NOTHING);
  }
}
