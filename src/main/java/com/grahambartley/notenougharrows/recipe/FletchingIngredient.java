package com.grahambartley.notenougharrows.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.dynamic.Codecs;

public record FletchingIngredient(Ingredient ingredient, int count) {
  public static final int MIN_COUNT = 1;
  public static final int MAX_COUNT = 64;

  public static final Codec<FletchingIngredient> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Ingredient.DISALLOW_EMPTY_CODEC
                          .fieldOf("ingredient")
                          .forGetter(FletchingIngredient::ingredient),
                      Codecs.rangedInt(MIN_COUNT, MAX_COUNT)
                          .optionalFieldOf("count", MIN_COUNT)
                          .forGetter(FletchingIngredient::count))
                  .apply(instance, FletchingIngredient::new));

  public static final PacketCodec<RegistryByteBuf, FletchingIngredient> PACKET_CODEC =
      PacketCodec.tuple(
          Ingredient.PACKET_CODEC,
          FletchingIngredient::ingredient,
          PacketCodecs.VAR_INT,
          FletchingIngredient::count,
          FletchingIngredient::new);

  public FletchingIngredient {
    Objects.requireNonNull(ingredient, "ingredient");
    if (count < MIN_COUNT || count > MAX_COUNT) {
      throw new IllegalArgumentException(
          "Fletching ingredient count must be between "
              + MIN_COUNT
              + " and "
              + MAX_COUNT
              + " but was "
              + count);
    }
  }

  public boolean test(final ItemStack stack) {
    return stack.getCount() >= count && ingredient.test(stack);
  }
}
