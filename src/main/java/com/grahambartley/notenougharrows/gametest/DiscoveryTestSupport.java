package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.DiscoveryArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import java.util.function.UnaryOperator;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class DiscoveryTestSupport {
  static final String TEMPLATE = FiringRangeSupport.TEMPLATE;
  static final String ARENA = "not-enough-arrows:open_arena";
  static final int PIERCING_LANE_Z = 8;
  static final int PIERCING_FLOOR_Y = 3;
  static final BlockPos PIERCING_SHOOTER = new BlockPos(2, PIERCING_FLOOR_Y, PIERCING_LANE_Z);
  static final BlockPos PIERCED = new BlockPos(6, PIERCING_FLOOR_Y, PIERCING_LANE_Z);
  static final int PIERCING_WALL_X = 20;

  private static final int PIERCING_WALL_HEIGHT = 3;
  private static final float EASTWARD_YAW = 270.0f;
  private static final float LEVEL_PITCH = 0.0f;
  private static final float BOW_SPEED = 3.0f;

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
    shooter.setYaw(EASTWARD_YAW);
    shooter.setPitch(LEVEL_PITCH);
    final ItemStack crossbow = new ItemStack(Items.CROSSBOW);
    crossbow.addEnchantment(
        context
            .getWorld()
            .getRegistryManager()
            .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.PIERCING),
        1);
    final PersistentProjectileEntity projectile =
        ((ArrowItem) arrow)
            .createArrow(context.getWorld(), new ItemStack(arrow), shooter, crossbow);
    projectile.setVelocity(shooter, LEVEL_PITCH, EASTWARD_YAW, 0.0f, BOW_SPEED, 0.0f);
    context.getWorld().spawnEntity(projectile);
    return projectile;
  }

  static void useDiscovery(final UnaryOperator<DiscoveryArrowConfig> change) {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();
    ServerConfigHolder.set(defaults.withDiscovery(change.apply(defaults.discovery())));
  }
}
