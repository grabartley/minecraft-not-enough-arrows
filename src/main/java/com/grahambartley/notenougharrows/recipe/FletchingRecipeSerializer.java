package com.grahambartley.notenougharrows.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;

public final class FletchingRecipeSerializer implements RecipeSerializer<FletchingRecipe> {
  private static final MapCodec<FletchingRecipe> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Codec.STRING
                          .optionalFieldOf("group", "")
                          .forGetter(FletchingRecipe::getGroup),
                      FletchingIngredient.CODEC
                          .listOf(FletchingRecipe.MIN_INPUTS, FletchingRecipe.MAX_INPUTS)
                          .fieldOf("ingredients")
                          .forGetter(FletchingRecipe::inputs),
                      ItemStack.VALIDATED_CODEC
                          .fieldOf("result")
                          .forGetter(FletchingRecipe::result))
                  .apply(instance, FletchingRecipe::new));

  private static final PacketCodec<RegistryByteBuf, FletchingRecipe> PACKET_CODEC =
      PacketCodec.tuple(
          PacketCodecs.STRING,
          FletchingRecipe::getGroup,
          FletchingIngredient.PACKET_CODEC.collect(PacketCodecs.toList(FletchingRecipe.MAX_INPUTS)),
          FletchingRecipe::inputs,
          ItemStack.PACKET_CODEC,
          FletchingRecipe::result,
          FletchingRecipe::new);

  @Override
  public MapCodec<FletchingRecipe> codec() {
    return CODEC;
  }

  @Override
  public PacketCodec<RegistryByteBuf, FletchingRecipe> packetCodec() {
    return PACKET_CODEC;
  }
}
