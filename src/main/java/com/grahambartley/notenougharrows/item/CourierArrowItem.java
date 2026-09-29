package com.grahambartley.notenougharrows.item;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.arrow.ArrowEntityFactory;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CourierArrowItem extends BaseArrowItem {
  public static final String CARRYING_KEY =
      "item." + NotEnoughArrows.MOD_ID + ".courier_arrow.carrying";
  public static final String EMPTY_KEY = "item." + NotEnoughArrows.MOD_ID + ".courier_arrow.empty";

  public CourierArrowItem(final Item.Settings settings, final ArrowEntityFactory arrowFactory) {
    super(settings, arrowFactory);
  }

  @Override
  public PersistentProjectileEntity createArrow(
      final World world,
      final ItemStack stack,
      final LivingEntity shooter,
      @Nullable final ItemStack shotFrom) {
    return super.createArrow(
        world, CourierPayloads.asFired(stack, shooter.isInCreativeMode()), shooter, shotFrom);
  }

  @Override
  public void appendTooltip(
      final ItemStack stack,
      final Item.TooltipContext context,
      final List<Text> tooltip,
      final TooltipType type) {
    tooltip.add(
        CourierPayloads.payloadOf(stack)
            .map(payload -> Text.translatable(CARRYING_KEY, payload.getCount(), payload.getName()))
            .orElseGet(() -> Text.translatable(EMPTY_KEY))
            .formatted(Formatting.GRAY));
  }

  @Override
  public boolean hasGlint(final ItemStack stack) {
    return CourierPayloads.isLoaded(stack) || super.hasGlint(stack);
  }
}
