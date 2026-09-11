package com.grahambartley.notenougharrows.item;

import com.grahambartley.notenougharrows.arrow.ArrowEntityFactory;
import com.grahambartley.notenougharrows.entity.BaseArrowEntity;
import java.util.Objects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BaseArrowItem extends ArrowItem {
  private static final double SHOOTER_EYE_OFFSET = 0.1;

  private final ArrowEntityFactory arrowFactory;

  public BaseArrowItem(final Item.Settings settings, final ArrowEntityFactory arrowFactory) {
    super(settings);
    this.arrowFactory = Objects.requireNonNull(arrowFactory, "arrowFactory");
  }

  public static boolean isModArrow(final ItemStack stack) {
    return stack.getItem() instanceof BaseArrowItem;
  }

  @Override
  public PersistentProjectileEntity createArrow(
      final World world,
      final ItemStack stack,
      final LivingEntity shooter,
      @Nullable final ItemStack shotFrom) {
    final BaseArrowEntity arrow =
        arrowFactory.create(
            world,
            shooter.getX(),
            shooter.getEyeY() - SHOOTER_EYE_OFFSET,
            shooter.getZ(),
            stack.copyWithCount(1),
            shotFrom);
    arrow.setOwner(shooter);
    return arrow;
  }

  @Override
  public ProjectileEntity createEntity(
      final World world, final Position pos, final ItemStack stack, final Direction direction) {
    final BaseArrowEntity arrow =
        arrowFactory.create(
            world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1), null);
    arrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
    return arrow;
  }
}
