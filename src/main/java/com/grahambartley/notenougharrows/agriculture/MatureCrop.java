package com.grahambartley.notenougharrows.agriculture;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;

public final class MatureCrop {

  private MatureCrop() {}

  public static Optional<BlockState> replanted(final BlockState state) {
    if (state == null) {
      return Optional.empty();
    }
    if (state.getBlock() instanceof CropBlock crop) {
      return crop.isMature(state) ? Optional.of(crop.withAge(0)) : Optional.empty();
    }
    if (state.getBlock() instanceof NetherWartBlock) {
      return state.get(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE
          ? Optional.of(state.with(NetherWartBlock.AGE, 0))
          : Optional.empty();
    }
    if (state.getBlock() instanceof CocoaBlock) {
      return state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE
          ? Optional.of(state.with(CocoaBlock.AGE, 0))
          : Optional.empty();
    }
    return Optional.empty();
  }
}
