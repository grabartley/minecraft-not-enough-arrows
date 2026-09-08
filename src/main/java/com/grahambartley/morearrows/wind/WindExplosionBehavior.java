package com.grahambartley.morearrows.wind;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public final class WindExplosionBehavior extends AdvancedExplosionBehavior {
  private static final float THROWN_CHARGE_KNOCKBACK = 1.22f;
  private static final boolean DESTROYS_BLOCKS = true;
  private static final boolean DAMAGES_ENTITIES = false;
  private static final float NO_KNOCKBACK = 0.0f;

  @Nullable private final Entity spared;

  public WindExplosionBehavior(@Nullable final Entity spared) {
    super(DESTROYS_BLOCKS, DAMAGES_ENTITIES, Optional.of(THROWN_CHARGE_KNOCKBACK), immuneBlocks());
    this.spared = spared;
  }

  @Override
  public float getKnockbackModifier(final Entity entity) {
    return entity == spared ? NO_KNOCKBACK : super.getKnockbackModifier(entity);
  }

  private static Optional<RegistryEntryList<Block>> immuneBlocks() {
    return Registries.BLOCK
        .getEntryList(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)
        .map(Function.identity());
  }
}
