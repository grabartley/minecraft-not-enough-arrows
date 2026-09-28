package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.structure.TimedStructureService;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public abstract class WorldChunkStructureMixin {

  @Inject(method = "setBlockState", at = @At("RETURN"))
  private void notEnoughArrows$forgetAStructureBlockSomethingElseReplaced(
      final BlockPos pos,
      final BlockState state,
      final boolean moved,
      final CallbackInfoReturnable<BlockState> cir) {
    final WorldChunk chunk = (WorldChunk) (Object) this;
    if (cir.getReturnValue() != null && chunk.getWorld() instanceof ServerWorld world) {
      TimedStructureService.onBlockChanged(world, chunk, pos, state);
    }
  }
}
