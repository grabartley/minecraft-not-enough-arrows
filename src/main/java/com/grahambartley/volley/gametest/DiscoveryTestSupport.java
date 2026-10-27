package com.grahambartley.volley.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class DiscoveryTestSupport {
  static final String TEMPLATE = FiringRangeSupport.TEMPLATE;
  static final String ARENA = MobArena.TEMPLATE;
  static final int PIERCING_LANE_Z = 8;
  static final int PIERCING_FLOOR_Y = 3;
  static final BlockPos PIERCING_SHOOTER = new BlockPos(2, PIERCING_FLOOR_Y, PIERCING_LANE_Z);
  static final BlockPos PIERCED = new BlockPos(6, PIERCING_FLOOR_Y, PIERCING_LANE_Z);
  static final int PIERCING_WALL_X = 20;

  private static final int PIERCING_WALL_HEIGHT = 3;

  private DiscoveryTestSupport() {}

  static ZombieEntity piercingRange(final TestContext context) {
    for (int up = 0; up < PIERCING_WALL_HEIGHT; up++) {
      context.setBlockState(
          new BlockPos(PIERCING_WALL_X, PIERCING_FLOOR_Y + up, PIERCING_LANE_Z), Blocks.STONE);
    }
    final ZombieEntity zombie = context.spawnEntity(EntityType.ZOMBIE, PIERCED);
    zombie.setAiDisabled(true);
    return zombie;
  }

  static PersistentProjectileEntity firePiercingEast(final TestContext context, final Item arrow) {
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, PIERCING_SHOOTER);
    shooter.setYaw(MockPlayerSupport.EASTWARD_YAW);
    shooter.setPitch(MockPlayerSupport.LEVEL_PITCH);
    final ItemStack crossbow = new ItemStack(Items.CROSSBOW);
    crossbow.addEnchantment(
        context
            .getWorld()
            .getRegistryManager()
            .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.PIERCING),
        1);
    return MockPlayerSupport.launch(context, shooter, arrow, crossbow);
  }
}
