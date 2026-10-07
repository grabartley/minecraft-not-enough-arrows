package com.grahambartley.notenougharrows.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;

public final class BucketReturningShapedRecipeSerializer
    implements RecipeSerializer<BucketReturningShapedRecipe> {

  private static final MapCodec<BucketReturningShapedRecipe> CODEC =
      ShapedRecipe.Serializer.CODEC.xmap(BucketReturningShapedRecipe::new, recipe -> recipe);

  private static final PacketCodec<RegistryByteBuf, BucketReturningShapedRecipe> PACKET_CODEC =
      ShapedRecipe.Serializer.PACKET_CODEC.xmap(BucketReturningShapedRecipe::new, recipe -> recipe);

  @Override
  public MapCodec<BucketReturningShapedRecipe> codec() {
    return CODEC;
  }

  @Override
  public PacketCodec<RegistryByteBuf, BucketReturningShapedRecipe> packetCodec() {
    return PACKET_CODEC;
  }
}
