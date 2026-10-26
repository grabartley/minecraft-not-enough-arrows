package com.grahambartley.volley.render;

import com.grahambartley.volley.SocialArrows;
import com.grahambartley.volley.Volley;
import com.grahambartley.volley.social.CourierPayloads;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public final class CourierArrowPredicates {
  public static final Identifier LOADED = Identifier.of(Volley.MOD_ID, "loaded");
  public static final float IS_LOADED = 1f;
  public static final float IS_EMPTY = 0f;

  private CourierArrowPredicates() {}

  public static void register() {
    ModelPredicateProviderRegistry.register(
        SocialArrows.COURIER_ARROW.item(),
        LOADED,
        (stack, world, entity, seed) -> loadedness(CourierPayloads.isLoaded(stack)));
  }

  public static float loadedness(final boolean loaded) {
    return loaded ? IS_LOADED : IS_EMPTY;
  }
}
