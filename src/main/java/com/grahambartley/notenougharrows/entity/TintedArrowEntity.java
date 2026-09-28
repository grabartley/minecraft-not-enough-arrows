package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.tint.ArrowChoice;
import com.grahambartley.notenougharrows.tint.TintChoice;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class TintedArrowEntity extends BaseArrowEntity {
  private static final TrackedData<String> CHOICE =
      DataTracker.registerData(TintedArrowEntity.class, TrackedDataHandlerRegistry.STRING);

  protected TintedArrowEntity(
      final EntityType<? extends TintedArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  protected TintedArrowEntity(
      final EntityType<? extends TintedArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
    syncChoice();
  }

  public TintChoice tint() {
    return tintedItem().palette().resolve(new ArrowChoice(dataTracker.get(CHOICE)));
  }

  @Override
  protected void initDataTracker(final DataTracker.Builder builder) {
    super.initDataTracker(builder);
    builder.add(CHOICE, "");
  }

  @Override
  protected void setStack(final ItemStack stack) {
    super.setStack(stack);
    syncChoice();
  }

  private void syncChoice() {
    dataTracker.set(CHOICE, tintedItem().choiceOf(getItemStack()).key());
  }

  private TintedArrowItem tintedItem() {
    return (TintedArrowItem) getDefaultItemStack().getItem();
  }
}
